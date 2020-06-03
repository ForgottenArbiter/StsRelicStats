package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.UnceasingTop;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = UnceasingTop.class,
        method = "onRefreshHand"
)
public class UnceasingTopInfo extends CombatStatsInfo {

    private static UnceasingTopInfo INSTANCE = new UnceasingTopInfo();
    private static String statId = getLocId(UnceasingTop.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private UnceasingTopInfo() {
    }

    public static UnceasingTopInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = UnceasingTopInfo.Locator.class
    )
    public static void insert(UnceasingTop _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(UnceasingTop.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
