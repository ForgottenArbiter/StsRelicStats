package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class PreCardDrawAction extends PreAmountAdjustmentAction {

    public PreCardDrawAction(AmountAdjustmentCallback statTracker) {
        super(statTracker);
    }

    protected int getStartingAmount() {
        return AbstractDungeon.player.hand.group.size();
    }

    protected boolean canceledOnEndCombat() {
        return true;
    }

}
