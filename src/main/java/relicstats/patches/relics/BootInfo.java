package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Boot;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Boot.class,
        method = "onAttackToChangeDamage"
)
public class BootInfo extends CombatStatsInfo {

    private static String statId = getLocId(Boot.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static BootInfo INSTANCE = new BootInfo();

    private BootInfo() {
    }

    public static BootInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator= BootInfo.Locator.class
    )
    public static void patch(Boot _instance, DamageInfo info, int damageAmount) {
        getInstance().amount += (5 - damageAmount);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Boot.class, "addToBot");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
