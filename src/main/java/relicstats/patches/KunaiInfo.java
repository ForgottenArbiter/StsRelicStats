package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Kunai;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Kunai.class,
        method = "onUseCard"
)
public class KunaiInfo extends CombatStatsInfo {

    private static KunaiInfo INSTANCE = new KunaiInfo();
    private static String statId = getLocId(Kunai.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private KunaiInfo() {
    }

    public static KunaiInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = KunaiInfo.Locator.class
    )
    public static void insert(Kunai _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Kunai.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
