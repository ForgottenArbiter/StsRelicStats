package relicstats.actions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import relicstats.AmountAdjustmentCallback;

public class PreGoldenEyeScryAction extends PreAmountAdjustmentAction {

    private int baseScry =  0;

    public PreGoldenEyeScryAction(AmountAdjustmentCallback statTracker, int baseScry) {
        super(statTracker);
        this.baseScry = baseScry;
    }

    protected int getStartingAmount() {
        int actualBaseScry = Math.min(AbstractDungeon.player.drawPile.group.size(), baseScry);
        int actualImprovedScry = Math.min(AbstractDungeon.player.drawPile.group.size(), baseScry + 2);
        return actualImprovedScry - actualBaseScry;
    }

    protected boolean canceledOnEndCombat() {
        return true;
    }

}
