package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import relicstats.AmountAdjustmentCallback;

public class PreOrangePelletsAction extends AbstractGameAction {

    private AmountAdjustmentCallback statTracker;

    public PreOrangePelletsAction(AmountAdjustmentCallback statTracker) {
        this.statTracker = statTracker;
    }

    @Override
    public void update() {
        int debuffs = 0;
        for (AbstractPower p: AbstractDungeon.player.powers) {
            if (p.type == AbstractPower.PowerType.DEBUFF) {
                debuffs += 1;
            }
        }
        statTracker.registerStartingAmount(debuffs);
        isDone = true;
    }
}
