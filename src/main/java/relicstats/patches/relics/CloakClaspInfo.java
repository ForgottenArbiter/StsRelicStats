package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CloakClasp;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CloakClasp.class,
        method = "onPlayerEndTurn"
)
public class CloakClaspInfo extends CombatStatsInfo {

    private static CloakClaspInfo INSTANCE = new CloakClaspInfo();
    private static String statId = getLocId(CloakClasp.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private CloakClaspInfo() {
    }

    public static CloakClaspInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = CloakClaspInfo.Locator.class
    )
    public static void insert(CloakClasp _instance) {
        getInstance().amount += AbstractDungeon.player.hand.group.size();
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(CloakClasp.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }


}
