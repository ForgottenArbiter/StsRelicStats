package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CentennialPuzzle;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = CentennialPuzzle.class,
        method = "wasHPLost"
)
public class CentennialPuzzleInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static CentennialPuzzleInfo INSTANCE = new CentennialPuzzleInfo();
    private static String statId = getLocId(CentennialPuzzle.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingCards;

    private CentennialPuzzleInfo() {
    }

    public static CentennialPuzzleInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = CentennialPuzzleInfo.Locator1.class
    )
    public static void before(CentennialPuzzle _instance, int damageAmount) {
        AbstractDungeon.actionManager.addToTop(new CardDrawFollowupAction(CentennialPuzzleInfo.getInstance()));
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(CentennialPuzzle.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @SpireInsertPatch(
            locator = CentennialPuzzleInfo.Locator2.class
    )
    public static void after(CentennialPuzzle _instance, int damageAmount) {
        AbstractDungeon.actionManager.addToTop(new PreCardDrawAction(CentennialPuzzleInfo.getInstance()));
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(CentennialPuzzle.class, "addToTop");
            int[] results = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(results, 1, 2);
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
