package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AncientTeaSet;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = AncientTeaSet.class,
        method = "atTurnStart"
)
public class AncientTeaSetInfo extends CombatStatsInfo {

    private static AncientTeaSetInfo INSTANCE = new AncientTeaSetInfo();
    private static String statId = getLocId(AncientTeaSet.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private AncientTeaSetInfo() {
    }

    public static AncientTeaSetInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = AncientTeaSetInfo.Locator.class
    )
    public static void insert(AncientTeaSet _instance) {
        getInstance().amount += 2;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AncientTeaSet.class, "addToTop");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
