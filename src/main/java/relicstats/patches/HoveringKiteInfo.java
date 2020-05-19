package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.HoveringKite;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = HoveringKite.class,
        method = "onManualDiscard"
)
public class HoveringKiteInfo extends CombatStatsInfo {

    private static HoveringKiteInfo INSTANCE = new HoveringKiteInfo();
    private static String statId = getLocId(HoveringKite.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private HoveringKiteInfo() {
    }

    public static HoveringKiteInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = HoveringKiteInfo.Locator.class
    )
    public static void insert(HoveringKite _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(HoveringKite.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
