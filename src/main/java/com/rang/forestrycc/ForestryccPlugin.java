package com.rang.forestrycc;

import java.time.Duration;
import java.util.HashMap;
import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Instant;

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
	private Integer roots_size = Root.SMAGE.getSize();
	private String msg_short;
	private HashMap<String, Instant> active_roots = new HashMap<String, Instant>();
	private HashMap<String, Instant> death_timeouts = new HashMap<String, Instant>();
	private HashMap<String, Integer> roots_confirmed = new HashMap<String, Integer>();
	private long death_timeout = 6;


	@Override
	protected void startUp() throws Exception
	{
		log.info("Forestry CC started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Forestry CC stopped!");
		infoBoxManager.removeIf(t -> t instanceof ForestryccTimer);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{

		// sender null is a system message
		if (chatMessage.getSender() == null) {
			return;
		}

		// chat should contain root ?
		if (chatMessage.getMessageNode().getType().equals(ChatMessageType.FRIENDSCHAT)) {

			// msg to lowercase
			String msg = chatMessage.getMessage().toLowerCase();
			String msg_short = msg;

			String event_type = "";

			if (msg.contains("mulch")) {
				event_type = "mulch";
				msg_short = msg_short.replace("mulch","");
				return;
			} else if (msg.contains("sap")) {
				event_type = "sap";
				msg_short = msg_short.replace("saps","");
				msg_short = msg_short.replace("sap","");
				return;
			} else if (msg.contains("root")) {
				event_type = "root";
				msg_short = msg_short.replace("roots","");
				msg_short = msg_short.replace("root","");
			}

			// shorten the message to prevent false triggers from long sentences
			if (msg_short.length() > 12) {
				msg_short = msg_short.substring(0, 12);
			}

			// find matching location
			Root event = Root.find(msg_short);

			// display timer if location is found
			if (event != null) {
				displayTimer(event, msg);
			}

		}
	}

	@Subscribe
	public void onGameTick(GameTick tickEvent) {
		if (client.getTickCount() % 5 == 0) {  // every 3 seconds roughly

			for (String key : active_roots.keySet()) {
				// remove if expired
				if (Duration.between(active_roots.get(key), Instant.now()).toSeconds() > event_duration) {
					active_roots.remove(key);
				}
				// if 90 secs, it's probably too late (show tomato)
				if (config.expirationWarning()) {
					if (Duration.between(active_roots.get(key), Instant.now()).toSeconds() > Long.valueOf(config.expirationWarningTime())) {
						Root event = Root.findFromName(key);
						if (event != null) {
							updateTimerImage(event, event_duration, itemManager.getImage(ItemID.TOMATO));
						}
					}
				}
			}
		}
	}

	private void updateTimerImage(Root event, long duration, AsyncBufferedImage image) {
		if (active_roots.containsKey(event.getName())) {
			infoBoxManager.removeIf(t -> t instanceof ForestryccTimer && ((ForestryccTimer) t).getEvent().equals(event.getName()));
			long new_duration = duration - Duration.between(active_roots.get(event.getName()), Instant.now()).toSeconds();
			ForestryccTimer timer = new ForestryccTimer(event.getName(), new_duration, image, this);
			timer.setTooltip(event.getName());
			infoBoxManager.addInfoBox(timer);
		}
	}

	private void updateTimerTooltip(Root event, long duration, String tooltip) {
		if (active_roots.containsKey(event.getName())) {
			infoBoxManager.removeIf(t -> t instanceof ForestryccTimer && ((ForestryccTimer) t).getEvent().equals(event.getName()));
			long new_duration = duration - Duration.between(active_roots.get(event.getName()), Instant.now()).toSeconds();
			ForestryccTimer timer = new ForestryccTimer(event.getName(), new_duration, itemManager.getImage(event.getItemSpriteId()), this);
			timer.setTooltip(tooltip);
			infoBoxManager.addInfoBox(timer);
		}
	}

	private void deleteTimerUI(Root event) {
		infoBoxManager.removeIf(t -> t instanceof ForestryccTimer && ((ForestryccTimer) t).getEvent().equals(event.getName()));
	}


	private void createTimerUI(Root event, long duration) {
		ForestryccTimer timer = new ForestryccTimer(event.getName(), duration, itemManager.getImage(event.getItemSpriteId()), this);
		timer.setTooltip(event.getName());
		infoBoxManager.addInfoBox(timer);
	}

	private boolean validEvent(Root event) {
		if (event.getName().equals(Root.NMAGE.getName()) && config.enableNmage()) { return true; }
		if (event.getName().equals(Root.SMAGE.getName()) && config.enableSmage()) { return true; }
		if (event.getName().equals(Root.DRAY.getName()) && config.enableDray()) { return true; }
		if (event.getName().equals(Root.CHURCH.getName()) && config.enableChurch()) { return true; }
		if (event.getName().equals(Root.N_SEERS.getName()) && config.enableNSeers()) { return true; }
		if (event.getName().equals(Root.S_SEERS.getName()) && config.enableSSeers()) { return true; }
		if (event.getName().equals(Root.SEERS.getName()) && config.enableSeersBank()) { return true; }
		if (event.getName().equals(Root.GLADE.getName()) && config.enableGlade()) { return true; }
		if (event.getName().equals(Root.BEE.getName()) && config.enableBees()) { return true; }
		if (event.getName().equals(Root.ZALC.getName()) && config.enableZalc()) { return true; }
		if (event.getName().equals(Root.MYTH.getName()) && config.enableMyth()) { return true; }
		if (event.getName().equals(Root.ARC.getName()) && config.enableArc()) { return true; }
		if (event.getName().equals(Root.PRIF.getName()) && config.enablePrif()) { return true; }
		if (event.getName().equals(Root.YAK.getName()) && config.enableYak()) { return true; }
		if (event.getName().equals(Root.GEYEWS.getName()) && config.enableGE()) { return true; }
		return false;
	}

	private void newEvent(Root event) {
		if (!active_roots.containsKey(event.getName())) {
			deleteTimerUI(event);
			createTimerUI(event,event_duration);
			active_roots.put(event.getName(),Instant.now());
			death_timeouts.remove(event.getName());
			roots_confirmed.remove(event.getName());
			roots_confirmed.put(event.getName(), 0);
		}
	}

	private void removeEvent(Root event) {
		deleteTimerUI(event);
		active_roots.remove(event.getName());
		roots_confirmed.remove(event.getName());
		death_timeouts.put(event.getName(), Instant.now());
	}

	private void displayTimer(Root event, String msg) {

		if (event.isDead(msg)) {
			// dead
			if (active_roots.containsKey(event.getName())) {
				long duration = Duration.between(active_roots.get(event.getName()), Instant.now()).toSeconds();
				//log.info(event.getName() + " dead after " + duration + "sec");
			} else {
				//log.info(event.getName() + " dead." );
			}
			removeEvent(event);

		} else if (event.isFake(msg)) {
			// fake (dead)
			//log.info(event.getName() + " fake.");
			removeEvent(event);

		} else if (event.isConfirmed(msg)) {
			// confirmed
			if (roots_confirmed.containsKey(event.getName())) {
				Integer confirmations = roots_confirmed.get(event.getName());
				roots_confirmed.put(event.getName(), confirmations+1);
				updateTimerTooltip(event,event_duration,event.getName() + " +" + roots_confirmed.get(event.getName()).toString());
				//log.info(event.getName() + " confirmed. +" + roots_confirmed.get(event.getName()).toString());
			} else {
				//log.info(event.getName() + " confirmed.");
				if (validEvent(event)) {
					newEvent(event);
				}
			}

		} else {
			// standard call
			// check if recently dead
			if (death_timeouts.containsKey(event.getName())) {
				if (Duration.between(death_timeouts.get(event.getName()), Instant.now()).toSeconds() < death_timeout) {
					//log.info(event.getName() + " recently died, ignoring call.");
					return;
				}
			}
			//log.info(event.getName());
			if (validEvent(event)) {
				newEvent(event);
			}

		}

	}

	@Provides
	ForestryccConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ForestryccConfig.class);
	}

}
