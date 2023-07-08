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

	// ----------------------------------------------------- GENERAL

	@ConfigSection(
			name = "General",
			description = "General settings",
			position = 0
	)
	String generalSettings = "generalSettings";

	@ConfigItem(
			position = 1,
			keyName = "allowRevives",
			name = "Allow Revive",
			description = "Someone may call an event fake/dead when it's still alive. Type \"Dray not dead\", \"Dray still up\", or \"Dray alive\" within 10 seconds to revive the timer.",
			section = generalSettings
	)
	default boolean allowRevives()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
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
			position = 3,
			keyName = "expirationWarningTime",
			name = "Warn after (sec)",
			description = "Display a tomato at X seconds after an event started.",
			section = generalSettings
	)
	default int expirationWarningTime()
	{
		return 90;
	}

	@ConfigItem(
			position = 4,
			keyName = "rootsEnabled",
			name = "Enable Roots (root/r)",
			description = "Disable to hide root events when possible.",
			section = generalSettings
	)
	default boolean rootsEnabled()
	{
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "sapsEnabled",
			name = "Enable Saplings (sap)",
			description = "Disable to hide sapling events when possible.",
			section = generalSettings
	)
	default boolean sapsEnabled()
	{
		return true;
	}

	@ConfigItem(
			position = 6,
			keyName = "BeesEnabled",
			name = "Enable Bees (bees/b)",
			description = "Disable to hide bee events when possible.",
			section = generalSettings
	)
	default boolean beesEnabled()
	{
		return true;
	}

	// ----------------------------------------------------- LOCATIONS

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
			description = "Use Kandarin headgear 4 or Combat bracelet teleport to ranging guild.",
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
			description = "Use Kandarin headgear 4 or Combat bracelet teleport to ranging guild.",
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
			description = "Teleport with Glory.",
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
			description = "Use Kandarin headgear 4 or Camelot teleport.",
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
			description = "Seers Maples / Seers Willows. Use Camelot teleport.",
			section = locations
	)
	default boolean enableNSeers()
	{
		return true;
	}

	@ConfigItem(
			position = 6,
			keyName = "enableSeers",
			name = "Seers (South/Bank)",
			description = "Seers Bank / Seers Oaks / S Seers. Use Camelot teleport.",
			section = locations
	)
	default boolean enableSeers()
	{
		return true;
	}

	@ConfigItem(
			position = 7,
			keyName = "enableGlade",
			name = "Xeric's Glade (Glade)",
			description = "Teleport with Xeric's talisman option 2.",
			section = locations
	)
	default boolean enableGlade()
	{
		return true;
	}

	@ConfigItem(
			position = 8,
			keyName = "enableBees",
			name = "Seers Hive (Hive)",
			description = "Use Kandarin headgear 4 or Camelot teleport.",
			section = locations
	)
	default boolean enableBees()
	{
		return true;
	}

	@ConfigItem(
			position = 9,
			keyName = "enableZalc",
			name = "Zalcano (Zalc)",
			description = "Located in Prifddinas, use Teleport crystal or Spirit tree.",
			section = locations
	)
	default boolean enableZalc()
	{
		return true;
	}

	@ConfigItem(
			position = 10,
			keyName = "enableMyth",
			name = "Myth's Guild (Myth)",
			description = "Teleport with mythical cape",
			section = locations
	)
	default boolean enableMyth()
	{
		return true;
	}

	@ConfigItem(
			position = 11,
			keyName = "enableArc",
			name = "Arceuus Magics (Arc)",
			description = "Teleport with book of the dead, option 5. Also Kharedst's memoirs option 5.",
			section = locations
	)
	default boolean enableArc()
	{
		return true;
	}

	@ConfigItem(
			position = 12,
			keyName = "enablePrif",
			name = "Prifddinas (Prif Teak/Prif Mahog)",
			description = "Use Teleport crystal",
			section = locations
	)
	default boolean enablePrif()
	{
		return true;
	}

	@ConfigItem(
			position = 13,
			keyName = "enableYak",
			name = "Neitiznot (Yak)",
			description = "Teleport with enchanted lyre. ",
			section = locations
	)
	default boolean enableYak()
	{
		return true;
	}

	@ConfigItem(
			position = 14,
			keyName = "enableGE",
			name = "GE Yews",
			description = "Teleport to Varrock or GE.",
			section = locations
	)
	default boolean enableGE()
	{
		return true;
	}

	@ConfigItem(
			position = 15,
			keyName = "enableRimm",
			name = "Rimmington (Rimm)",
			description = "Teleport to House or Skills necklace.",
			section = locations
	)
	default boolean enableRimm()
	{
		return true;
	}

	@ConfigItem(
			position = 16,
			keyName = "enableLookout",
			name = "Xeric's Lookout (Lookout)",
			description = "Teleport with Xeric's Talisman.",
			section = locations
	)
	default boolean enableLookout()
	{
		return true;
	}

	@ConfigItem(
			position = 17,
			keyName = "enableWoodland",
			name = "Kourend Woodland (Woodland)",
			description = "Teleport with Rada's blessing.",
			section = locations
	)
	default boolean enableWoodland()
	{
		return true;
	}

	@ConfigItem(
			position = 18,
			keyName = "enableOutpost",
			name = "Barbarian Outpost (Barb)",
			description = "Teleport with games necklace.",
			section = locations
	)
	default boolean enableOutpost()
	{
		return true;
	}

}
