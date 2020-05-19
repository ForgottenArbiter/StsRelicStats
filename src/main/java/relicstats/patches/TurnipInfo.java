package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Turnip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
public class TurnipInfo extends CombatStatsInfo {

    private static TurnipInfo INSTANCE = new TurnipInfo();
    private static String statId = getLocId(Turnip.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private TurnipInfo() {
    }

    public static TurnipInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = TurnipInfo.Locator.class
    )
    public static void insert(ApplyPowerAction _instance) {
        getInstance().amount += _instance.amount;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            int[] results = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(results, 2, 3);
        }
    }

}
