package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.FrozenCore;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = FrozenCore.class,
        method = "onPlayerEndTurn"
)
public class FrozenCoreInfo extends CombatStatsInfo {

    private static FrozenCoreInfo INSTANCE = new FrozenCoreInfo();
    private static String statId = getLocId(FrozenCore.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private FrozenCoreInfo() {
    }

    public static FrozenCoreInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = FrozenCoreInfo.Locator.class
    )
    public static void insert(FrozenCore _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(FrozenCore.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
