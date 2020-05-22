package relicstats.actions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class PreHealingAction extends PreAmountAdjustmentAction {

    public PreHealingAction(AmountAdjustmentCallback statTracker) {
        super(statTracker);
    }

    protected int getStartingAmount() {
        return AbstractDungeon.player.currentHealth;
    }

    protected boolean canceledOnEndCombat() {
        return false;
    }

}
