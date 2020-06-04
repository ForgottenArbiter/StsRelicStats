package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldenEye;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.PreGoldenEyeScryAction;

import java.util.ArrayList;

@SpirePatch(
        clz = ScryAction.class,
        method = SpirePatch.CONSTRUCTOR
)
public class GoldenEyeInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static GoldenEyeInfo INSTANCE = new GoldenEyeInfo();
    private static String statId = getLocId(GoldenEye.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private GoldenEyeInfo() {
    }

    public static GoldenEyeInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = GoldenEyeInfo.Locator.class
    )
    public static void insert(ScryAction _instance, int numCards) {
        // We are assuming all scry actions add to bottom, which is true in the base game
        AbstractDungeon.actionManager.addToBottom(new PreGoldenEyeScryAction(GoldenEyeInfo.getInstance(), numCards));
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    public void registerStartingAmount(int amount) {
        this.amount += amount;
    }

    public void registerEndingAmount(int amount) {}

}
