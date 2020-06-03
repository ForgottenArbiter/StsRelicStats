package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;
import com.megacrit.cardcrawl.relics.SelfFormingClay;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

public class SelfFormingClayInfo extends CombatStatsInfo {

    private static SelfFormingClayInfo INSTANCE = new SelfFormingClayInfo();
    private static String statId = getLocId(SelfFormingClay.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int BLOCK = (int) ReflectionHacks.getPrivateStatic(SelfFormingClay.class, "BLOCK_AMT");
    private static int clayBlock = 0;

    private SelfFormingClayInfo() {
    }

    public static SelfFormingClayInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    public void onCombatStartForStats() {
        super.onCombatStartForStats();
        clayBlock = 0;
    }

    @SpirePatch(
            clz = SelfFormingClay.class,
            method = "wasHPLost"
    )
    public static class SelfFormingClayPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(SelfFormingClay _instance, int damageAmount) {
            clayBlock += BLOCK;
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(SelfFormingClay.class, "flash");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }
    }

    @SpirePatch(
            clz = NextTurnBlockPower.class,
            method = "atStartOfTurn"
    )
    public static class NextTurnBlockPowerPatch {

        @SpirePostfixPatch
        public static void patch(NextTurnBlockPower _instance) {
            SelfFormingClayInfo.getInstance().amount += clayBlock;
            clayBlock = 0;
        }

    }

}
