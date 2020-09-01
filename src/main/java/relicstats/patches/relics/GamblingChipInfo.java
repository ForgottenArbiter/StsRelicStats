package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.GamblingChip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = GamblingChipAction.class,
        method = "update"
)
public class GamblingChipInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static GamblingChipInfo INSTANCE = new GamblingChipInfo();
    private static String statId = getLocId(GamblingChip.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingCards;

    public static GamblingChipInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = GamblingChipInfo.Locator1.class
    )
    public static void before(GamblingChipAction _instance) {
        AbstractDungeon.actionManager.addToTop(new CardDrawFollowupAction(GamblingChipInfo.getInstance()));
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(GamblingChipAction.class, "addToTop");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @SpireInsertPatch(
            locator = GamblingChipInfo.Locator2.class
    )
    public static void after(GamblingChipAction _instance) {
        AbstractDungeon.actionManager.addToTop(new PreCardDrawAction(GamblingChipInfo.getInstance()));
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(GamblingChipAction.class, "addToTop");
            int[] results = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            results[0] += 1;
            return results;
        }
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
