package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.MummifiedHand;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = MummifiedHand.class,
        method = "onUseCard"
)
public class MummifiedHandInfo extends CombatStatsInfo {

    private static MummifiedHandInfo INSTANCE = new MummifiedHandInfo();
    private static String statId = getLocId(MummifiedHand.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private MummifiedHandInfo() {
    }

    public static MummifiedHandInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = MummifiedHandInfo.Locator.class,
            localvars = {"c"}
    )
    public static void insert(MummifiedHand _instance, AbstractCard card, UseCardAction action, AbstractCard c) {
        if (c.costForTurn > 0) {
            getInstance().amount += c.costForTurn;
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "setCostForTurn");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
