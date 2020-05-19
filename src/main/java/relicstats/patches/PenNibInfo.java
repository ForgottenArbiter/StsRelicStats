package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.PenNibPower;
import com.megacrit.cardcrawl.relics.PenNib;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = PenNibPower.class,
        method = "onUseCard"
)
public class PenNibInfo extends CombatStatsInfo {

    private static PenNibInfo INSTANCE = new PenNibInfo();
    private static String statId = getLocId(PenNib.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private PenNibInfo() {
    }

    public static PenNibInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = PenNibInfo.Locator.class
    )
    public static void insert(PenNibPower _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(PenNibPower.class, "addToBot");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
