package relicstats;

import basemod.abstracts.CustomSavableRaw;

public interface HasCustomStats extends CustomSavableRaw {

    // Gets the stats description that is displayed to the user
    String getStatsDescription();

    // Gets the stats description that is displayed to the user if the extended stats are enabled
    String getExtendedStatsDescription();

    // Resets the stats for the relic
    void resetStats();

    // Is called at the start of a combat
    void onCombatStartForStats();

}
