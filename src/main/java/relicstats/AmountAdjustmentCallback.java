package relicstats;

public interface AmountAdjustmentCallback {

    void registerStartingAmount(int startingAmount);
    void registerEndingAmount(int endingAmount);

}
