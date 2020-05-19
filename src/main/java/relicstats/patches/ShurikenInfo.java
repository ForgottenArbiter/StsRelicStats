package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Shuriken;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Shuriken.class,
        method = "onUseCard"
)
public class ShurikenInfo extends CombatStatsInfo {

    private static ShurikenInfo INSTANCE = new ShurikenInfo();
    private static String statId = getLocId(Shuriken.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private ShurikenInfo() {
    }

    public static ShurikenInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = ShurikenInfo.Locator.class
    )
    public static void insert(Shuriken _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Shuriken.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
