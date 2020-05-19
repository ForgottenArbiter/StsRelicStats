package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BirdFacedUrn;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.HealingFollowupAction;
import relicstats.actions.PreHealingAction;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = BirdFacedUrn.class,
        method = "onUseCard"
)
public class BirdFacedUrnInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static String statId = getLocId(BirdFacedUrn.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static BirdFacedUrnInfo INSTANCE = new BirdFacedUrnInfo();

    private int startingHp;

    private BirdFacedUrnInfo () {}

    public static BirdFacedUrnInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator=BirdFacedUrnInfo.Locator1.class
    )
    public static void before(BirdFacedUrn _instance, AbstractCard card, UseCardAction action) {
        HealingFollowupAction postAction = new HealingFollowupAction(BirdFacedUrnInfo.getInstance());
        AbstractDungeon.actionManager.addToTop(postAction);
    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        startingHp = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        amount += endingAmount - startingHp;
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(BirdFacedUrn.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @SpireInsertPatch(
            locator=BirdFacedUrnInfo.Locator2.class
    )
    public static void after(BirdFacedUrn _instance, AbstractCard card, UseCardAction action) {
        BirdFacedUrnInfo info = BirdFacedUrnInfo.getInstance();
        PreHealingAction preHealingAction = new PreHealingAction(info);
        AbstractDungeon.actionManager.addToTop(preHealingAction);
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(BirdFacedUrn.class, "addToTop");
            int[] results = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(results, 1, 2);
        }
    }


}
