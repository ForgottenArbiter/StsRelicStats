package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.ArtOfWar;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = ArtOfWar.class,
        method = "atTurnStart"
)
public class ArtOfWarInfo extends CombatStatsInfo {

    private static ArtOfWarInfo INSTANCE = new ArtOfWarInfo();
    private static String statId = getLocId(ArtOfWar.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private ArtOfWarInfo() {
    }

    public static ArtOfWarInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = ArtOfWarInfo.Locator.class
    )
    public static void insert(ArtOfWar _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(ArtOfWar.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
