package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.ArtOfWar;
import com.megacrit.cardcrawl.relics.HappyFlower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = HappyFlower.class,
        method = "atTurnStart"
)
public class HappyFlowerInfo extends CombatStatsInfo {

    private static HappyFlowerInfo INSTANCE = new HappyFlowerInfo();
    private static String statId = getLocId(HappyFlower.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private HappyFlowerInfo() {
    }

    public static HappyFlowerInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = HappyFlowerInfo.Locator.class
    )
    public static void insert(HappyFlower _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(HappyFlower.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
