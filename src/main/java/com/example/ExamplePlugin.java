package com.example;

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
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;


@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	private long long_dur = 120;
	private Integer roots_size = Root.SMAGE.getSize();
	private String msg_short;
	private HashMap<String, Instant> active_roots = new HashMap<String, Instant>();


	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		infoBoxManager.removeIf(t -> t instanceof ExampleTimer);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{

		if (chatMessage.getSender() == null) {
			return;
		}

		if (chatMessage.getSender().toLowerCase().contains("root")) {

			String msg = chatMessage.getMessage().toLowerCase();

			if (msg.contains("mulch")) {
				return;
			}

			if (msg.contains("roots")) {
				msg_short = msg.replace("roots","");
			} else if (msg.contains("root")) {
				msg_short = msg.replace("root","");
			} else {
				msg_short = msg;
			}

			if (msg_short.length() > 12) {
				msg_short = msg_short.substring(0, 12);
			}

			Root event = Root.find(msg_short);

			if (event != null) {
				displayTimer(event, msg);
			}

		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (client.getTickCount() % 10 == 0) {  // every 6 seconds roughly
			for (String key : active_roots.keySet()) {
				// check if expired
				if (Duration.between(active_roots.get(key), Instant.now()).toSeconds() > long_dur) {
					active_roots.remove(key);
				}
				// if 80 secs, it's probably too late
				if (Duration.between(active_roots.get(key), Instant.now()).toSeconds() > 90) {
					infoBoxManager.removeIf(t -> t instanceof ExampleTimer && ((ExampleTimer) t).getEvent().equals(key));
					long new_duration = long_dur - Duration.between(active_roots.get(key), Instant.now()).toSeconds();
					ExampleTimer timer = new ExampleTimer(key, new_duration, itemManager.getImage(ItemID.TOMATO), this);
					timer.setTooltip(key);
					infoBoxManager.addInfoBox(timer);
				}
				// alive debug
				//log.info(key + " alive " + Duration.between(active_roots.get(key), Instant.now()).toSeconds() + " sec");
			}
		}
	}

	private void displayTimer(Root event, String msg) {

		if (event.isConfirmed(msg)) {
			log.info(event.getName() + " confirmed.");

			if (!active_roots.containsKey(event.getName())) {
				infoBoxManager.removeIf(t -> t instanceof ExampleTimer && ((ExampleTimer) t).getEvent().equals(event.getName()));
				ExampleTimer timer = new ExampleTimer(event.getName(), long_dur, itemManager.getImage(event.getItemSpriteId()), this);
				timer.setTooltip(event.getName());
				infoBoxManager.addInfoBox(timer);
				active_roots.put(event.getName(),Instant.now());
			}


		} else if (event.isDead(msg)) {
			if (active_roots.containsKey(event.getName())) {
				long duration = Duration.between(active_roots.get(event.getName()), Instant.now()).toSeconds();
				log.info(event.getName() + " dead after " + duration + "sec");
			} else {
				log.info(event.getName() + " dead." );
			}

			infoBoxManager.removeIf(t -> t instanceof ExampleTimer && ((ExampleTimer) t).getEvent().equals(event.getName()));
			active_roots.remove(event.getName());


		} else if (event.isFake(msg)) {
			log.info(event.getName() + " fake.");

			infoBoxManager.removeIf(t -> t instanceof ExampleTimer && ((ExampleTimer) t).getEvent().equals(event.getName()));
			active_roots.remove(event.getName());


		} else {
			log.info(event.getName());

			if (!active_roots.containsKey(event.getName())) {
				infoBoxManager.removeIf(t -> t instanceof ExampleTimer && ((ExampleTimer) t).getEvent().equals(event.getName()));
				ExampleTimer timer = new ExampleTimer(event.getName(), long_dur, itemManager.getImage(event.getItemSpriteId()), this);
				timer.setTooltip(event.getName());
				timer.setPriority(InfoBoxPriority.HIGH);
				infoBoxManager.addInfoBox(timer);
				active_roots.put(event.getName(),Instant.now());
			}


		}

	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}

}
