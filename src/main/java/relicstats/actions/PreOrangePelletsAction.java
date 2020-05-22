package relicstats.actions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import relicstats.AmountAdjustmentCallback;

public class PreOrangePelletsAction extends PreAmountAdjustmentAction {

    public PreOrangePelletsAction(AmountAdjustmentCallback statTracker) {
        super(statTracker);
    }

    @Override
    protected int getStartingAmount() {
        int debuffs = 0;
        for (AbstractPower p: AbstractDungeon.player.powers) {
            if (p.type == AbstractPower.PowerType.DEBUFF) {
                debuffs += 1;
            }
        }
        return debuffs;
    }

    @Override
    protected boolean canceledOnEndCombat() {
        return true;
    }

}
