package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Duality;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Duality.class,
        method = "onUseCard"
)
public class DualityInfo extends CombatStatsInfo {

    private static DualityInfo INSTANCE = new DualityInfo();
    private static String statId = getLocId(Duality.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private DualityInfo() {
    }

    public static DualityInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = DualityInfo.Locator.class
    )
    public static void insert(Duality _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Duality.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
