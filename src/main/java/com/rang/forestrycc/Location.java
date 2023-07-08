/*
 * Copyright (c) 2023, Rang <rangdevelopment@gmail.com>
 * based on
 * Copyright (c) 2016-2017, Cameron Moberg <Moberg@tuta.io>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rang.forestrycc;

import com.google.common.collect.ImmutableMap;
import java.util.*;

import net.runelite.api.ItemID;


enum Location {
	NMAGE("North Sorcerer's Tower (Nmage)", List.of("nmage","n mage","north mage"), null, false, false, ItemID.COMBAT_BRACELET),
	SMAGE("South Sorcerer's Tower (Smage)", List.of("smage","s mage","south mage"), null, false, false, ItemID.CAPE_OF_LEGENDS),
	DRAY("Draynor (Dray)", List.of("draynor","dray"),null, false, false, ItemID.WILLOW_LOGS),
	CHURCH("Seers Church (Church)", List.of("church","churc","curch","churh","chrch","chuch"),null, false, false, ItemID.HOLY_SYMBOL),
	N_SEERS("North Seers (NSeers)", List.of("north seers", "north seer", "n seers","nseers","n seer","nseer"), List.of("bank","south","s "), false, true, ItemID.MAPLE_LOGS),
	SEERS("Seers (S Seers/Seers Bank)", List.of("seer oaks","seers oaks", "seer oak", "seers oak","seers bank", "seer bank", "s seers","sseers","s seer","sseer", "seers", "seer"),List.of("n","north","nseers"), false, true, ItemID.CAMELOT_TELEPORT),
	GLADE("Xeric's Glade (Glade)", List.of("glade"),null, false, false, ItemID.XERICS_TALISMAN),
	BEE("Seers Beehives (Hive)", List.of("hive"),null, false, false, ItemID.BEE_ON_A_STICK),
	ZALC("Zalcano (Zalc)", List.of("zalc"),null, false, false, ItemID.ZALCANO_SHARD),
	MYTH("Myth's Guild (Myth)", List.of("myths","myth"),null, false, false, ItemID.MYTHICAL_CAPE_22114),
	ARC("Arceuus Magics (Arc)", List.of("arc"),null, false, false, ItemID.BOOK_OF_THE_DEAD),
	PRIF("Prifddinas (Prif Teaks/Prif Mahog)", List.of("prif teaks","prif teak","prif t","prif mahogs","prif mahog","prif m","prift","prifm","prif"),null, false, false, ItemID.CRYSTAL_TELEPORT_SEED),
	YAK("Neitiznot (Yak)", List.of("yak"),null, false, false, ItemID.HELM_OF_NEITIZNOT),
	GEYEWS("GE Yews", List.of("ge"),null, true, false, ItemID.COINS),
	RIMM("Rimmington (Rimm)", List.of("Rimmington","rimm","rim"),null, false, false, ItemID.SAW),
	LOOK("Xeric's Lookout (Lookout)", List.of("lookout"),null, false, false, ItemID.BLACK_KITESHIELD),
	WOOD("Kourend Woodland (Woodland)", List.of("woodland"),null, false, false, ItemID.RADAS_BLESSING_4),
	OUTPOST("Barbarian Outpost (Barb)", List.of("barbarian outpost","barbarian","outpost","barb"),null, false, false, ItemID.GAMES_NECKLACE8),
	;

	private static final Map<String, Location> locations;

	private final String name;
	public final List<String> match_filters;
	public final List<String> exclude_filters;
	private final boolean match_strict;
	private final boolean exclude_strict;
	private final int itemSpriteId;

	static {
		ImmutableMap.Builder<String, Location> builder = new ImmutableMap.Builder<>();

		for (Location roots : values()) {
			builder.put(roots.getName(), roots);
		}

		locations = builder.build();
	}

	Location(String name, List<String> match_filters, List<String> exclude_filters, boolean match_strict, boolean exclude_strict, int itemSpriteId) {
		this.name = name;
		this.match_filters = match_filters;
		this.exclude_filters = exclude_filters;
		this.match_strict = match_strict;
		this.exclude_strict = exclude_strict;
		this.itemSpriteId = itemSpriteId;
	}

	public String getName() {
		return name;
	}

	public Integer getSize() {
		return locations.size();
	}

	public boolean isMatch(String msg) {

		// split msg
		String[] msg_split = msg.split(" ");

		// rebuild string from first 3 words
		String first_three_words = null;
		Integer num_spaces = msg_split.length;
		if (num_spaces > 3) { num_spaces = 3; }
		first_three_words = String.join(" ", Arrays.copyOfRange(msg_split,0,num_spaces));

		// check exclude first
		if (exclude_filters != null) {
			if (exclude_strict) {
				// strict match
				for (String split_value : msg_split) {
					for (String value : exclude_filters) {
						if (split_value.equals(value)) { return false; }
					}
				}
			} else {
				// match anything
				for (String value : exclude_filters) {
					if (first_three_words.contains(value)) { return false; }
				}
			}
		}

		// look for match
		if (match_filters != null) {
			if (match_strict) {
				// strict match
				for (String split_value : msg_split) {
					for (String value : match_filters) {
						if (split_value.equals(value)) { return true; }
					}
				}
			} else {
				// match anything
				for (String value : match_filters) {
					if (first_three_words.contains(value)) { return true; }
				}
			}
		}

		return false;
	}

	public boolean isRevive(String msg) {
		String[] confirmed_strings = {"not dead","alive","still here","not d","still up","real"};
		for (String value : confirmed_strings) {
			if (msg.contains(value)) { return true; }
		}
		return false;
	}

	public boolean isConfirmed(String msg) {
		String[] confirmed_strings = {"confirmed","confirm","conf","con*","real","alive","c"};
		String[] msg_split = msg.split(" ");
		for (String split_value : msg_split) {
			for (String value : confirmed_strings) {
				if (split_value.matches(value)) { return true; }
			}
		}
		return false;
	}

	public boolean isDead(String msg) {
		String[] confirmed_strings = {"murder","bust","rip","clear","cleared","busted","dead","done","ded","dea","d"};
		String[] msg_split = msg.split(" ");
		for (String split_value : msg_split) {
			for (String value : confirmed_strings) {
				if (split_value.matches(value)) { return true; }
			}
		}
		return false;
	}

	public boolean isFake(String msg) {
		String[] confirmed_strings = {"fake"};
		String[] msg_split = msg.split(" ");
		for (String split_value : msg_split) {
			for (String value : confirmed_strings) {
				if (split_value.matches(value)) { return true; }
			}
		}
		return false;
	}

	public int getItemSpriteId() {
		return itemSpriteId;
	}

	public static Location find(String msg) {
		for (String key : locations.keySet()) {
			if (locations.get(key).isMatch(msg)) {
				return locations.get(key);
			}
		}
		return null;
	}

	public static Location findFromName(String rootName) {
		for (String key : locations.keySet()) {
			if (locations.get(key).getName().equals(rootName)) {
				return locations.get(key);
			}
		}
		return null;
	}

}
