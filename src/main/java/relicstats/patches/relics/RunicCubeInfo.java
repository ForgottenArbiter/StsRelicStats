package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.RunicCube;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = RunicCube.class,
        method = "wasHPLost"
)
public class RunicCubeInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static RunicCubeInfo INSTANCE = new RunicCubeInfo();
    private static String statId = getLocId(RunicCube.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingCards;

    private RunicCubeInfo() {
    }

    public static RunicCubeInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = RunicCubeInfo.Locator1.class
    )
    public static void before(RunicCube _instance, int damageAmount) {
        AbstractDungeon.actionManager.addToTop(new CardDrawFollowupAction(RunicCubeInfo.getInstance()));
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(RunicCube.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @SpireInsertPatch(
            locator = RunicCubeInfo.Locator2.class
    )
    public static void after(RunicCube _instance, int damageAmount) {
        AbstractDungeon.actionManager.addToTop(new PreCardDrawAction(RunicCubeInfo.getInstance()));
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(RunicCube.class, "addToTop");
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
