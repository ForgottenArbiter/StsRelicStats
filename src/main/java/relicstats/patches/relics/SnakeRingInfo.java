package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.SnakeRing;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

@SpirePatch(
        clz = SnakeRing.class,
        method = "atBattleStart"
)
public class SnakeRingInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static SnakeRingInfo INSTANCE = new SnakeRingInfo();
    private static String statId = getLocId(SnakeRing.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingCards;

    private SnakeRingInfo() {
    }

    public static SnakeRingInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void before(SnakeRing _instance) {
        AbstractDungeon.actionManager.addToBottom(new PreCardDrawAction(SnakeRingInfo.getInstance()));
    }

    @SpirePostfixPatch
    public static void after(SnakeRing _instance) {
        AbstractDungeon.actionManager.addToBottom(new CardDrawFollowupAction(SnakeRingInfo.getInstance()));
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

