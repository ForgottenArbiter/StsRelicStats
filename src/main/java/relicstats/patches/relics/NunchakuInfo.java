package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Nunchaku;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Nunchaku.class,
        method = "onUseCard"
)
public class NunchakuInfo extends CombatStatsInfo {

    private static NunchakuInfo INSTANCE = new NunchakuInfo();
    private static String statId = getLocId(Nunchaku.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private NunchakuInfo() {
    }

    public static NunchakuInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = NunchakuInfo.Locator.class
    )
    public static void insert(Nunchaku _instance) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Nunchaku.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
