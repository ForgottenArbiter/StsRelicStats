package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.VioletLotus;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = VioletLotus.class,
        method = "onChangeStance"
)
public class VioletLotusInfo extends CombatStatsInfo {

    private static VioletLotusInfo INSTANCE = new VioletLotusInfo();
    private static String statId = getLocId(VioletLotus.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private VioletLotusInfo() {
    }

    public static VioletLotusInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = VioletLotusInfo.Locator.class
    )
    public static void insert(VioletLotus _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(VioletLotus.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
