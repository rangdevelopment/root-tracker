package com.rang.forestrycc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(ForestryccConfig.GROUP)
public interface ForestryccConfig extends Config
{
	String GROUP = "forestrycc";

	@ConfigSection(
			name = "General",
			description = "General settings",
			position = 0
	)
	String generalSettings = "generalSettings";

	@ConfigItem(
		position = 1,
		keyName = "expirationWarning",
		name = "Expiration Warning",
		description = "Display a tomato when an event is close to expiring.",
		section = generalSettings
	)
	default boolean expirationWarning()
	{
		return true;
	}

	@Range(
			min = 0,
			max = 119
	)
	@ConfigItem(
			position = 2,
			keyName = "expirationWarningTime",
			name = "Warn after (sec)",
			description = "Display a tomato at X seconds after an event started.",
			section = generalSettings
	)
	default int expirationWarningTime()
	{
		return 90;
	}



	@ConfigSection(
			name = "Locations",
			description = "Locations",
			position = 1
	)
	String locations = "locations";

	@ConfigItem(
			position = 1,
			keyName = "enableNmage",
			name = "North Sorcerer's Tower (Nmage)",
			description = "North Sorcerer's Tower (Nmage). Use Combat bracelet teleport to ranging guild.",
			section = locations
	)
	default boolean enableNmage()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "enableSmage",
			name = "South Sorcerer's Tower (Smage)",
			description = "South Sorcerer's Tower (Smage). Use Combat bracelet teleport to ranging guild.",
			section = locations
	)
	default boolean enableSmage()
	{
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "enableDray",
			name = "Draynor (Dray)",
			description = "Draynor (Dray). Use Glory teleport.",
			section = locations
	)
	default boolean enableDray()
	{
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "enableChurch",
			name = "Seers Church (Church)",
			description = "Seers Church (Church). Use Camelot teleport.",
			section = locations
	)
	default boolean enableChurch()
	{
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "enableNSeers",
			name = "North Seers (N Seers)",
			description = "North Seers (N Seers). Use Camelot teleport.",
			section = locations
	)
	default boolean enableNSeers()
	{
		return true;
	}

	@ConfigItem(
			position = 6,
			keyName = "enableSSeers",
			name = "South Seers (S Seers)",
			description = "South Seers (S Seers / Seers Oaks). Use Camelot teleport.",
			section = locations
	)
	default boolean enableSSeers()
	{
		return true;
	}

	@ConfigItem(
			position = 7,
			keyName = "enableSeersBank",
			name = "Seers Bank (Seers)",
			description = "Seers Bank (Seers). Use Camelot teleport.",
			section = locations
	)
	default boolean enableSeersBank()
	{
		return true;
	}

	@ConfigItem(
			position = 8,
			keyName = "enableGlade",
			name = "Xeric's Glade (Glade)",
			description = "Xeric's Glade (Glade). Use Xeric's talisman option 2.",
			section = locations
	)
	default boolean enableGlade()
	{
		return true;
	}

	@ConfigItem(
			position = 9,
			keyName = "enableBees",
			name = "Seers Bees (Bees)",
			description = "Seers Bees (Bees). Use Camelot teleport.",
			section = locations
	)
	default boolean enableBees()
	{
		return true;
	}

	@ConfigItem(
			position = 10,
			keyName = "enableZalc",
			name = "Zalcano (Zalc)",
			description = "Zalcano (Zalc). Located in Prifddinas, use Teleport crystal.",
			section = locations
	)
	default boolean enableZalc()
	{
		return true;
	}

	@ConfigItem(
			position = 11,
			keyName = "enableMyth",
			name = "Myth's Guild (Myth)",
			description = "Myth's Guild (Myth). Use mythical cape",
			section = locations
	)
	default boolean enableMyth()
	{
		return true;
	}

	@ConfigItem(
			position = 12,
			keyName = "enableArc",
			name = "Arceuus Magics (Arc)",
			description = "Arceuus Magics (Arc). Use book of the dead, option 5. Also Kharedst's memoirs option 5.",
			section = locations
	)
	default boolean enableArc()
	{
		return true;
	}

	@ConfigItem(
			position = 13,
			keyName = "enablePrif",
			name = "Prifddinas (Prif Teak/Prif Mahog)",
			description = "Prifddinas (Prif Teak/Prif Mahog). Use Teleport crystal",
			section = locations
	)
	default boolean enablePrif()
	{
		return true;
	}

	@ConfigItem(
			position = 14,
			keyName = "enableYak",
			name = "Neitiznot (Yak)",
			description = "Neitiznot (Yak). Use enchanted lyre. ",
			section = locations
	)
	default boolean enableYak()
	{
		return true;
	}

	@ConfigItem(
			position = 15,
			keyName = "enableGE",
			name = "GE Yews",
			description = "GE Yews. Teleport to GE... ",
			section = locations
	)
	default boolean enableGE()
	{
		return true;
	}

}
