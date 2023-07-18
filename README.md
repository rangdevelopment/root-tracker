# Forestry CC
A plugin for tracking forestry CC events such as roots, saps, or bees. 

Currently modeled on calls from the "Roots cc" channel.

Can work in other channels.

# Features!
- Optional support for all events (Roots, Saps, or Bees)
- Timer that estimates the end of an event (120-180 seconds)
- Silent adjustable warning before an event is likely to end so you don't waste time teleporting
- Ability to disable locations you don't want / don't have access to
- Displays state of multiple locations at once
- Stackable confirmations of a location
- Revive timers that ended early or accidentally killed

# Locations
- North Sorcerer's Tower (Nmage)
- South Sorcerer's Tower (Smage)
- Draynor (Dray)
- Seers Church (Church)
- North Seers (NSeers)
- South Seers (Seers Oaks)
- Seers Bank (Seers)
- Xeric's Glade (Glade)
- Seers Bees (Bees)
- Zalcano (Zalc)
- Myth's Guild (Myth)
- Arceuus Magics (Arc)
- Prifddinas (Prif Teaks/Prif Mahog)
- Neitiznot (Yak)
- GE Yews
- Rimmington (Rimm)
- Xeric's Lookout (Lookout)
- Kourend Woodland (Woodland)
- Barbarian Outpost (Barb)

# How to use
1. Join a cc
2. Call out an active location like "Dray" and a timer will begin for everyone in CC
3. You may add the event type like "Dray roots" or "Dray sap"
3. Additional people can confirm with "Dray c" or "Dray conf"
4. When the event ends type "Dray d" or "Dray dead" and the timer will end
5. If an event is fake, type "Dray fake"
6. If someone kills an event early, you can revive it by typing "Dray not dead" or "Dray alive"

# Supported Phrases
- New: Dray (and more)
- Confirm: Dray c, Dray conf, Dray con (and more)
- Dead: Dray d, Dray dead, Dray rip, Dray clear (and more)
- Fake: Dray fake
- Revive: Dray not dead, Dray alive, Dray still up, Dray still going (and more)

# Change log

7/17/2023
- fix bee event timer (now 3min)
- fix bug where events do not end correctly
- change how the warming setting works
- add "arctic pines" to call options for yaks

7/8/2023
- added 4 new locations (Rimm, Lookout, Woodland, Barb)
- improve event matching routine for more accurate timers, less false positives.
- add support for bees, saps, and roots (previously saps were disabled)
- Ability to revive false dead calls (with toggle)
- improve filtering of questions & conversations in CC
- banned characters: ? " =
- update teleport recommendations
- better matching for misspelled words

7/7/2023
- forestry cc is born