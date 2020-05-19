package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Ginger;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = ApplyPowerAction.class,
        method = "update"
)
public class GingerInfo extends CombatStatsInfo {

    private static GingerInfo INSTANCE = new GingerInfo();
    private static String statId = getLocId(Ginger.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private GingerInfo() {
    }

    public static GingerInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = GingerInfo.Locator.class
    )
    public static void insert(ApplyPowerAction _instance) {
        getInstance().amount += _instance.amount;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            int[] results = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(results, 1, 2);
        }
    }

}
