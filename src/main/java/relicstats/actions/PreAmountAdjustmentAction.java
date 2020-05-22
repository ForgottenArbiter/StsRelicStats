package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import relicstats.AmountAdjustmentCallback;

public abstract class PreAmountAdjustmentAction extends AbstractGameAction {

    private AmountAdjustmentCallback statTracker;

    protected abstract int getStartingAmount();

    protected abstract boolean canceledOnEndCombat();

    public PreAmountAdjustmentAction(AmountAdjustmentCallback statTracker) {
        this.statTracker = statTracker;
        if (!canceledOnEndCombat()) {
            this.actionType = ActionType.DAMAGE;
        }
    }

    @Override
    public void update() {
        statTracker.registerStartingAmount(getStartingAmount());
        isDone = true;
    }

}
