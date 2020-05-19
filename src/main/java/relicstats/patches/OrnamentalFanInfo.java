package relicstats.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.OrnamentalFan;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = OrnamentalFan.class,
        method = "onUseCard"
)
public class OrnamentalFanInfo extends CombatStatsInfo {

    private static OrnamentalFanInfo INSTANCE = new OrnamentalFanInfo();
    private static String statId = getLocId(OrnamentalFan.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int BLOCK = (int) ReflectionHacks.getPrivateStatic(OrnamentalFan.class, "BLOCK");

    private OrnamentalFanInfo() {
    }

    public static OrnamentalFanInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = OrnamentalFanInfo.Locator.class
    )
    public static void insert(OrnamentalFan _instance) {
        getInstance().amount += BLOCK;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(OrnamentalFan.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
