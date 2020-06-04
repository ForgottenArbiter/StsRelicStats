package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Melange;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.PreAmountAdjustmentAction;
import relicstats.actions.PreScryAction;

@SpirePatch(
        clz = Melange.class,
        method = "onShuffle"
)
public class MelangeInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static MelangeInfo INSTANCE = new MelangeInfo();
    private static String statId = getLocId(Melange.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private MelangeInfo() {
    }

    public static MelangeInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void patch(Melange _instance) {
        AbstractDungeon.actionManager.addToBottom(new PreScryAction(MelangeInfo.getInstance()));
    }

    public void registerStartingAmount(int amount) {
        this.amount += Math.min(amount, 3);
    }

    public void registerEndingAmount(int amount) {}

}
