# Relic Stats
A mod to track and display relic stats in Slay the Spire

# To integrate stats into your mod
1. Implement the HasCustomStats interface for your relic
2. Call `RelicStats.registerCustomStats()` with your relic's ID and the object with HasCustomStats to track the relic's stats.
