package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.relics.FossilizedHelix;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

public class FossilizedHelixInfo extends CombatStatsInfo {

    private static String statId = getLocId(FossilizedHelix.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static FossilizedHelixInfo INSTANCE = new FossilizedHelixInfo();
    private static boolean active = false;

    private FossilizedHelixInfo() {
    }

    public static FossilizedHelixInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @Override
    public void resetStats() {
        super.resetStats();
        active = false;
    }

    @SpirePatch(
            clz = BufferPower.class,
            method = "onAttackedToChangeDamage"
    )
    public static class BufferPatch {

        @SpireInsertPatch(locator = BufferPatch.Locator.class)
        public static void patch(BufferPower _instance, DamageInfo info, int damageAmount) {
            if (active) {
                getInstance().amount += damageAmount;
                active = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(BufferPower.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

    @SpirePatch(
            clz = FossilizedHelix.class,
            method = "atBattleStart"
    )
    public static class HelixPatch {

        @SpirePostfixPatch
        public static void patch(FossilizedHelix _instance) {
            active = true;
        }

    }

}
