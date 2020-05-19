package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Sundial;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Sundial.class,
        method = "onShuffle"
)
public class SundialInfo extends CombatStatsInfo {

    private static SundialInfo INSTANCE = new SundialInfo();
    private static String statId = getLocId(Sundial.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private SundialInfo() {
    }

    public static SundialInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = SundialInfo.Locator.class
    )
    public static void insert(Sundial _instance) {
        getInstance().amount += 2;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Sundial.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
