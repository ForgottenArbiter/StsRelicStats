package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.DeadBranch;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = DeadBranch.class,
        method = "onExhaust"
)
public class DeadBranchInfo extends CombatStatsInfo {

    private static DeadBranchInfo INSTANCE = new DeadBranchInfo();
    private static String statId = getLocId(DeadBranch.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private DeadBranchInfo() {
    }

    public static DeadBranchInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = DeadBranchInfo.Locator.class
    )
    public static void insert(DeadBranch _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(DeadBranch.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
