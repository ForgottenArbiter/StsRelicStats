package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import relicstats.patches.relics.WarpedTongsInfo;

public class WarpedTongsFollowupAction extends AbstractGameAction {

    @Override
    public void update() {
        if(WarpedTongsInfo.UpgradeRandomCardActionPatch.upgradedCards) {
            WarpedTongsInfo.getInstance().incrementAmount();
            WarpedTongsInfo.UpgradeRandomCardActionPatch.upgradedCards = false;
        }
        isDone = true;
    }

}
