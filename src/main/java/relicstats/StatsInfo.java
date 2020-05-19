package relicstats;

public abstract class StatsInfo implements HasCustomStats {

    public abstract String getStatsDescription();

    public String getExtendedStatsDescription() {
        return getStatsDescription();
    }

    public abstract void resetStats();

    protected static String getLocId(String relicId) {
        return "STATS:" + relicId;
    }

    public void onCombatStartForStats() {

    }

}
