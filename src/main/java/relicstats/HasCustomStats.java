package relicstats;

import basemod.abstracts.CustomSavableRaw;

public interface HasCustomStats extends CustomSavableRaw {

    // Gets the stats description that is displayed to the user
    String getStatsDescription();

    // Gets the stats description that is displayed to the user if the extended stats are enabled
    String getExtendedStatsDescription(int totalCombats, int totalTurns);

    // Resets the stats for the relic
    void resetStats();

    // Whether to show the relic's stats
    boolean showStats();

}
