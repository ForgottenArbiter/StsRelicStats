package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class PreCardDrawAction extends AbstractGameAction {

    private AmountAdjustmentCallback statTracker;

    public PreCardDrawAction(AmountAdjustmentCallback statTracker) {
        this.statTracker = statTracker;
    }

    @Override
    public void update() {
        statTracker.registerStartingAmount(AbstractDungeon.player.hand.group.size());
        isDone = true;
    }

}
