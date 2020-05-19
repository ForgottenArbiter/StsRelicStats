package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Necronomicon;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Necronomicon.class,
        method = "onUseCard"
)
public class NecronomiconInfo extends CombatStatsInfo {

    private static NecronomiconInfo INSTANCE = new NecronomiconInfo();
    private static String statId = getLocId(Necronomicon.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private NecronomiconInfo() {
    }

    public static NecronomiconInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = NecronomiconInfo.Locator.class
    )
    public static void insert(Necronomicon _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Necronomicon.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
