package relicstats.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Orichalcum;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Orichalcum.class,
        method = "onPlayerEndTurn"
)
public class OrichalcumInfo extends CombatStatsInfo {

    private static OrichalcumInfo INSTANCE = new OrichalcumInfo();
    private static String statId = getLocId(Orichalcum.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int BLOCK = (int) ReflectionHacks.getPrivateStatic(Orichalcum.class, "BLOCK_AMT");

    private OrichalcumInfo() {
    }

    public static OrichalcumInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = OrichalcumInfo.Locator.class
    )
    public static void insert(Orichalcum _instance) {
        getInstance().amount += BLOCK;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Orichalcum.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
