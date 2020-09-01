package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BagOfPreparation;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

@SpirePatch(
        clz = BagOfPreparation.class,
        method = "atBattleStart"
)
public class BagOfPreparationInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static BagOfPreparationInfo INSTANCE = new BagOfPreparationInfo();
    private static String statId = getLocId(BagOfPreparation.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingCards;

    private BagOfPreparationInfo() {
    }

    public static BagOfPreparationInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void before(BagOfPreparation _instance) {
        AbstractDungeon.actionManager.addToBottom(new PreCardDrawAction(BagOfPreparationInfo.getInstance()));
    }

    @SpirePostfixPatch
    public static void after(BagOfPreparation _instance) {
        AbstractDungeon.actionManager.addToBottom(new CardDrawFollowupAction(BagOfPreparationInfo.getInstance()));
    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        startingCards = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        amount += endingAmount - startingCards;
    }
}
