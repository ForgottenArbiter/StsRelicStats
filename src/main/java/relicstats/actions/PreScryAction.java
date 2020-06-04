package relicstats.actions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class PreScryAction extends PreAmountAdjustmentAction {

    public PreScryAction(AmountAdjustmentCallback statTracker) {
        super(statTracker);
    }

    protected int getStartingAmount() {
        return AbstractDungeon.player.drawPile.group.size();
    }

    protected boolean canceledOnEndCombat() {
        return true;
    }

}
