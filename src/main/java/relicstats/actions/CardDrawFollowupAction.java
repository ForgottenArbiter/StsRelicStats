package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class CardDrawFollowupAction extends AbstractGameAction {

    private AmountAdjustmentCallback statTracker;

    public CardDrawFollowupAction(AmountAdjustmentCallback statTracker) {
        this.statTracker = statTracker;
    }

    @Override
    public void update() {
        statTracker.registerEndingAmount(AbstractDungeon.player.hand.group.size());
        isDone = true;
    }

}
