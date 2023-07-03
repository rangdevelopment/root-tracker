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
package com.example;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.runelite.api.ItemID;


enum Root {
	NMAGE("North Sorcerer's Tower (Nmage)", "nmage", null,null, null, ItemID.COMBAT_BRACELET),
	SMAGE("South Sorcerer's Tower (Smage)", "smage", null,null, null, ItemID.CAPE_OF_LEGENDS),
	DRAY("Draynor (Dray)", "dray", null, null,null, ItemID.AMULET_OF_GLORY),
	CHURCH("Seers Church (Church)", "chur", null, null,null, ItemID.HOLY_SYMBOL),
	N_SEERS("North Seers (NSeers)", "seer", "n", null,null, ItemID.MAPLE_LOGS),
	S_SEERS("South Seers (Seers Oak)", "seer", null, "n","bank", ItemID.CAMELOT_TELEPORT),
	SEERS("Seers Bank", "seer", "bank", "n",null, ItemID.CAMELOT_TELEPORT),
	GLADE("Xeric's Glade (Glade)", "glade", null,null, null, ItemID.XERICS_TALISMAN),
	BEE("Seers Bees (Bees)", "bee", null, null,null, ItemID.BEE_ON_A_STICK),
	ZALC("Zalcano (Zalc)", "zalc", null, null,null, ItemID.ZALCANO_SHARD),
	MYTH("Myth's Guild (Myth)", "myth", null, null,null, ItemID.MYTHICAL_CAPE_22114),
	ARC("Arceuus Magics (Arc)", "arc", null, null,null, ItemID.BOOK_OF_THE_DEAD),
	PRIFF_T("Prifddinas Teaks (Prif Teaks)", "prif", "t", null,null, ItemID.TEAK_LOGS),
	PRIFF_M("Prifddinas Mahogany (Prif Mahog)", "prif", "m", null,null, ItemID.MAHOGANY_LOGS),
	YAK("Neitiznot (Yak)", "yak", null, null,null, ItemID.HELM_OF_NEITIZNOT),
	;

	private static final Map<String, Root> roots;

	private final String name;
	private final String filter1;
	private final String filter2;
	private final String exclude;
	private final String exclude2;
	private final int itemSpriteId;

	static {
		ImmutableMap.Builder<String, Root> builder = new ImmutableMap.Builder<>();

		for (Root roots : values()) {
			builder.put(roots.getName(), roots);
		}

		roots = builder.build();
	}

	Root(String name, String filter1, String filter2, String exclude, String exclude2, int itemSpriteId) {
		this.name = name;
		this.filter1 = filter1;
		this.filter2 = filter2;
		this.exclude = exclude;
		this.exclude2 = exclude2;
		this.itemSpriteId = itemSpriteId;
	}

	public String getName() {
		return name;
	}

	public Integer getSize() {
		return roots.size();
	}

	public boolean isMatch(String msg) {
		if (exclude != null) {
			if (msg.contains(exclude)) {
				return false;
			}
		}

		if (exclude2 != null) {
			if (msg.contains(exclude2)) {
				return false;
			}
		}

		if (filter2 != null) {
			if (!msg.contains(filter2)) {
				return false;
			}
		}

		if (msg.contains(filter1)) {
			return true;
		}
		return false;
	}

	public boolean isConfirmed(String msg) {
		if (filter1 != null) {
			msg = msg.replace(filter1, "");
		}
		if (filter2 != null) {
			msg = msg.replace(filter2, "");
		}
		return msg.contains("c") || msg.contains("real") || msg.contains("alive");
		// c, con, con*, conf, confirmed, real, alive
	}

	public boolean isDead(String msg) {
		if (filter1 != null) {
			msg = msg.replace(filter1, "");
		}
		if (filter2 != null) {
			msg = msg.replace(filter2, "");
		}
		return msg.contains("d") || msg.contains("clear") || msg.contains("bust");
		// dead, d, ded, done, clear, cleared, bust, busted
	}

	public boolean isFake(String msg) {
		if (filter1 != null) {
			msg = msg.replace(filter1, "");
		}
		if (filter2 != null) {
			msg = msg.replace(filter2, "");
		}
		return msg.contains("fake");
		// fake
	}

	public int getItemSpriteId() {
		return itemSpriteId;
	}

	public static Root find(String msg) {
		for (String key : roots.keySet()) {
			if (roots.get(key).isMatch(msg)) {
				return roots.get(key);
			}
		}
		return null;
	}

}
