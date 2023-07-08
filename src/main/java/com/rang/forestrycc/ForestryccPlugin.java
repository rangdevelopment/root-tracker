package com.rang.forestrycc;

import java.time.Duration;
import java.util.HashMap;
import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.api.events.*;
import net.runelite.client.util.AsyncBufferedImage;


@Slf4j
@PluginDescriptor(
	name = "Forestry CC"
)
public class ForestryccPlugin extends Plugin
{
	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private ForestryccConfig config;

	private long event_duration = 120;
	private List<String> events_alive = new ArrayList<String>();
	private HashMap<String, Instant> events_starttime = new HashMap<String, Instant>();
	private HashMap<String, Instant> events_timeofdeath = new HashMap<String, Instant>();
	private HashMap<String, Integer> events_confirmed = new HashMap<String, Integer>();
	private HashMap<String, String> events_type = new HashMap<String, String>();
	private List<String> bee_filters = List.of("bees","bee","b");
	private List<String> root_filters = List.of("roots","root","r");
	private List<String> sap_filters = List.of("saps","sap");
	private List<String> unsupported_chars = List.of(",","'","/","-","_","(",")");
	private List<String> excessive_spaces = List.of("          ","         ","        ","       ","      ","     ","    ","   ","  ");
	private long death_timeout = 6;
	private long revive_timeout = 10;

	String chat_msg_orignal;
	String chat_msg;


	@Override
	protected void startUp() throws Exception
	{
		log.info("Forestry CC started!");
		for (Location r : Location.values()) {
			events_starttime.put(r.getName(), Instant.now());
			events_timeofdeath.put(r.getName(), Instant.now());
			events_type.put(r.getName(), null);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Forestry CC stopped!");
		infoBoxManager.removeIf(t -> t instanceof ForestryccTimer);
	}

	// -------------------------------------------------------------------------- PROCESS CHAT CHANNEL MESSAGE

	private boolean validChatMsg(ChatMessage chatMessage) {
		String msg_text = chatMessage.getMessage().toLowerCase();
		// sender null is a system message, exit
		if (chatMessage.getSender() == null) {
			return false;
		}
		// we only read messages from group chat
		if (!chatMessage.getMessageNode().getType().equals(ChatMessageType.FRIENDSCHAT)) {
			return false;
		}
		// ignore anything over 25 characters long
		if (msg_text.length() > 25) {
			//log.info(msg_text + " over limit: " + msg_text.length());
			return false;
		}
		// ignore questions, and attempt to pre-filter sentences
		if (msg_text.contains("?") || msg_text.contains("\"")) {
			return false;
		}
		return true;
	}

	private String classifyEvent() {
		String[] msg_split = chat_msg.split(" ");
		for (String split_value : msg_split) {
			for (String value : bee_filters) {
				if (split_value.equals(value)) { return "BEES"; }
			}
		}
		for (String split_value : msg_split) {
			for (String value : root_filters) {
				if (split_value.equals(value)) { return "ROOTS"; }
			}
		}
		for (String split_value : msg_split) {
			for (String value : sap_filters) {
				if (split_value.equals(value)) { return "SAP"; }
			}
		}
		return null;
	}

	private void clean_chat_msg() {
		// remove unsupported characters
		for (String c : unsupported_chars) {
			chat_msg = chat_msg.replace(c,"");
		}
		// remove excessive spaces
		for (String excessive_space : excessive_spaces) {
			chat_msg = chat_msg.replace(excessive_space, " ");
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		// continue if valid
		if (validChatMsg(chatMessage)) {

			chat_msg_orignal = chatMessage.getMessage().toLowerCase();
			chat_msg = chat_msg_orignal;

			// clean chat msg
			clean_chat_msg();

			// classify event
			String event_type = classifyEvent();
			if (event_type != null) {
				// check if enabled
				if (event_type.equals("BEES") && !config.enableBees()) { return; } // exit
				if (event_type.equals("SAP") && !config.sapsEnabled()) { return; } // exit
				if (event_type.equals("ROOTS") && !config.rootsEnabled()) { return; } // exit
				// filter from messages
				if (event_type.equals("BEES")) {
					chat_msg = chat_msg.replace("bee", "");
					chat_msg = chat_msg.replace("bees", "");
				} else if (event_type.equals("SAP")) {
					chat_msg = chat_msg.replace("saps", "");
					chat_msg = chat_msg.replace("sap", "");
					chat_msg = chat_msg.replace("mulch", "");
				} else if (event_type.equals("ROOTS")) {
					chat_msg = chat_msg.replace("roots", "");
					chat_msg = chat_msg.replace("root", "");
				}
			}

			// shorten the message to prevent false triggers from long sentences.
			String chat_msg_substring = chat_msg;
			if (chat_msg_substring.length() > 17) {
				chat_msg_substring = chat_msg_substring.substring(0, 17);
			}

			// find matching location
			Location event = Location.find(chat_msg_substring);

			// display timer if location is found
			if (event != null) {
				displayTimer(event, event_type, chat_msg);
			}

		}
	}

	// -------------------------------------------------------------------------- ON GAME TICKS

	@Subscribe
	public void onGameTick(GameTick tickEvent) {
		if (client.getTickCount() % 5 == 0) {  // every 3 seconds roughly
			activeRootsRemoveExpired();
			activeRootsExpirationWarning();
		}
	}

	private void activeRootsExpirationWarning() {
		if (!config.expirationWarning()) { return; }
		for (String rootName : events_alive) {
			// if 90 secs, it's probably too late (show tomato)
			if (Duration.between(events_starttime.get(rootName), Instant.now()).toSeconds() > Long.valueOf(config.expirationWarningTime())) {
				Location event = Location.findFromName(rootName);
				if (event != null) {
					updateTimerImage(event, event_duration, itemManager.getImage(ItemID.TOMATO));
				}
			}
		}
	}

	private void activeRootsRemoveExpired() {
		// remove if expired
		events_alive.removeIf(entry -> Duration.between(events_starttime.get(entry), Instant.now()).toSeconds() > event_duration);
	}

	// -------------------------------------------------------------------------- UPDATE TIMER

	private void updateTimerImage(Location event, long duration, AsyncBufferedImage image) {
		if (events_alive.contains(event.getName())) {
			deleteTimerUI(event);
			long new_duration = duration - Duration.between(events_starttime.get(event.getName()), Instant.now()).toSeconds();
			if (new_duration > 0) {
				String tooltip = event.getName();
				createTimerUI(event, new_duration, tooltip);
			}
		}
	}

	private void updateTimerTooltip(Location event, long duration, String tooltip) {
		if (events_alive.contains(event.getName())) {
			deleteTimerUI(event);
			long new_duration = duration - Duration.between(events_starttime.get(event.getName()), Instant.now()).toSeconds();
			if (new_duration > 0) {
				createTimerUI(event, new_duration, tooltip);
			}
		}
	}

	// -------------------------------------------------------------------------- TIMER UI

	private void deleteTimerUI(Location event) {
		infoBoxManager.removeIf(t -> t instanceof ForestryccTimer && ((ForestryccTimer) t).getEvent().equals(event.getName()));
	}


	private void createTimerUI(Location event, long duration, String tooltip) {
		ForestryccTimer timer = new ForestryccTimer(event.getName(), duration, itemManager.getImage(event.getItemSpriteId()), this);
		timer.setTooltip(tooltip);
		infoBoxManager.addInfoBox(timer);
	}

	// -------------------------------------------------------------------------- VALIDATION

	private boolean validEvent(Location event) {
		if (event.getName().equals(Location.NMAGE.getName()) && config.enableNmage()) { return true; }
		if (event.getName().equals(Location.SMAGE.getName()) && config.enableSmage()) { return true; }
		if (event.getName().equals(Location.DRAY.getName()) && config.enableDray()) { return true; }
		if (event.getName().equals(Location.CHURCH.getName()) && config.enableChurch()) { return true; }
		if (event.getName().equals(Location.N_SEERS.getName()) && config.enableNSeers()) { return true; }
		if (event.getName().equals(Location.SEERS.getName()) && config.enableSeers()) { return true; }
		if (event.getName().equals(Location.GLADE.getName()) && config.enableGlade()) { return true; }
		if (event.getName().equals(Location.BEE.getName()) && config.enableBees()) { return true; }
		if (event.getName().equals(Location.ZALC.getName()) && config.enableZalc()) { return true; }
		if (event.getName().equals(Location.MYTH.getName()) && config.enableMyth()) { return true; }
		if (event.getName().equals(Location.ARC.getName()) && config.enableArc()) { return true; }
		if (event.getName().equals(Location.PRIF.getName()) && config.enablePrif()) { return true; }
		if (event.getName().equals(Location.YAK.getName()) && config.enableYak()) { return true; }
		if (event.getName().equals(Location.GEYEWS.getName()) && config.enableGE()) { return true; }
		if (event.getName().equals(Location.RIMM.getName()) && config.enableRimm()) { return true; }
		if (event.getName().equals(Location.LOOK.getName()) && config.enableLookout()) { return true; }
		if (event.getName().equals(Location.WOOD.getName()) && config.enableWoodland()) { return true; }
		if (event.getName().equals(Location.OUTPOST.getName()) && config.enableOutpost()) { return true; }
		return false;
	}

	// -------------------------------------------------------------------------- FORMATTING

	private String generateTooltip(Location event, String event_type, Integer confirmations)
	{
		String tooltip = event.getName();
		// event
		if (event_type != null) {
			tooltip = tooltip + " " + event_type;
		}
		// confirmations
		if (confirmations != null && confirmations > 0) {
			tooltip = tooltip + " +" + confirmations;
		}
		return tooltip;
	}

	// -------------------------------------------------------------------------- EVENTS

	private void newEvent(Location event, String event_type) {
		// remove any active event timer
		deleteTimerUI(event);
		// create tooltip
		String tooltip = generateTooltip(event, event_type, null);
		// create timer
		createTimerUI(event, event_duration, tooltip);
		// update Lists
		events_alive.add(event.getName());
		events_confirmed.put(event.getName(), 0);
		events_starttime.put(event.getName(),Instant.now());
		if (events_type.get(event.getName()) == null) {
			events_type.put(event.getName(),event_type);
		}
	}

	private void reviveEvent(Location event) {
		// remove any active event timer
		deleteTimerUI(event);
		// create tooltip
		String tooltip = generateTooltip(event, events_type.get(event.getName()), null);
		tooltip = tooltip + " *revived";
		// create timer
		long remaining_duration = event_duration - Duration.between(events_starttime.get(event.getName()), Instant.now()).toSeconds();
		createTimerUI(event, remaining_duration, tooltip);
		// update Lists
		events_alive.add(event.getName());
	}

	private void confirmEvent(Location event) {
		// update total confirmations
		Integer confirmations = events_confirmed.get(event.getName());
		events_confirmed.put(event.getName(), confirmations+1);
		// set tooltip
		String tooltip = generateTooltip(event, events_type.get(event.getName()), events_confirmed.get(event.getName()));
		// update timer
		updateTimerTooltip(event, event_duration, tooltip);
	}

	private void removeEvent(Location event) {
		// remove active event timers
		deleteTimerUI(event);
		// update Lists
		events_alive.remove(event.getName());
		events_confirmed.remove(event.getName());
		events_timeofdeath.put(event.getName(), Instant.now());
		events_type.put(event.getName(),null);
	}

	// -------------------------------------------------------------------------- INSTRUCTIONS

	private void Dead(Location event) {
		if (events_alive.contains(event.getName())) {
			long duration = Duration.between(events_starttime.get(event.getName()), Instant.now()).toSeconds();
			//log.info(event.getName() + " dead after " + duration + "sec");
		} else {
			//log.info(event.getName() + " dead: " + chat_msg_orignal );
		}
		removeEvent(event);
	}

	private void Revive(Location event) {
		// check if event is active
		if (events_alive.contains(event.getName())) { 
			return;  // cancel revive
		} 
		// check if event is within revive window
		if (Duration.between(events_timeofdeath.get(event.getName()), Instant.now()).toSeconds() < revive_timeout) {
			reviveEvent(event);
			//log.info(event.getName() + " attempt revive: " + chat_msg_orignal);
		}
	}

	private void Fake(Location event) {
		//log.info(event.getName() + " fake: " + chat_msg_orignal);
		removeEvent(event);
	}

	private void New(Location event, String event_type) {
		// check if recently died
		if (Duration.between(events_timeofdeath.get(event.getName()), Instant.now()).toSeconds() < death_timeout) {
			//log.info(event.getName() + " recently died, ignoring call: " + chat_msg_orignal);
			return;
		}
		// create new event if valid
		if (validEvent(event)) {
			//log.info(event.getName() + " new: " + chat_msg_orignal);
			newEvent(event, event_type);
		}
	}

	private void Confirm(Location event) {
		// confirm event if valid
		if (validEvent(event)) {
			//log.info(event.getName() + " confirmed: " + chat_msg_orignal);
			confirmEvent(event);
		}
	}

	// -------------------------------------------------------------------------- CREATE TIMER

	private void displayTimer(Location event, String event_type, String msg) {

		if (event.isRevive(msg)) {
			// revive
			if (config.allowRevives()) {
				Revive(event);
			}
			
		} else if (event.isFake(msg)) {
			// fake
			Fake(event);

		} else if (event.isConfirmed(msg)) {
			// confirm
			if (events_confirmed.containsKey(event.getName())) {
				Confirm(event);
			} else {
				New(event, event_type);
			}

		} else if (event.isDead(msg)) {
			// dead
			Dead(event);

		} else {
			// new / confirm
			if (events_confirmed.containsKey(event.getName())) {
				Confirm(event);
			} else {
				New(event, event_type);
			}

		}

	}

	@Provides
	ForestryccConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ForestryccConfig.class);
	}

}
