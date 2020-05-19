package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class HealingFollowupAction extends AbstractGameAction {

    private AmountAdjustmentCallback statTracker;

    public HealingFollowupAction(AmountAdjustmentCallback statTracker) {
        this.statTracker = statTracker;
        this.actionType = ActionType.DAMAGE;  // So it's not cleared if damage kills
    }

    @Override
    public void update() {
        statTracker.registerEndingAmount(AbstractDungeon.player.currentHealth);
        isDone = true;
    }
}
