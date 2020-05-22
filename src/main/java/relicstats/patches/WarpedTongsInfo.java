package relicstats.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.UpgradeRandomCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.WarpedTongs;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;
import relicstats.actions.WarpedTongsFollowupAction;

import java.util.ArrayList;
import java.util.Arrays;

public class WarpedTongsInfo extends CombatStatsInfo {

    private static WarpedTongsInfo INSTANCE = new WarpedTongsInfo();
    private static String statId = getLocId(WarpedTongs.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private WarpedTongsInfo() {
    }

    public static WarpedTongsInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    public void incrementAmount() {
        amount += 1;
    }

    public void resetStats() {
        super.resetStats();
        UpgradeRandomCardActionPatch.upgradedCards = false;
    }

    @SpirePatch(
            clz = WarpedTongs.class,
            method = "atTurnStartPostDraw"
    )
    public static class WarpedTongsPatch {

        @SpirePostfixPatch
        public static void patch(WarpedTongs _instance) {
            AbstractDungeon.actionManager.addToBottom(new WarpedTongsFollowupAction());
        }

    }

    @SpirePatch(
            clz = UpgradeRandomCardAction.class,
            method = "update"
    )
    public static class UpgradeRandomCardActionPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void patch(UpgradeRandomCardAction _instance) {
            upgradedCards = true;
        }

        public static boolean upgradedCards = false;

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "superFlash");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }


}
