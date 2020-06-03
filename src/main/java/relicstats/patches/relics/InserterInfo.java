package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Inserter;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Inserter.class,
        method = "atTurnStart"
)
public class InserterInfo extends CombatStatsInfo {

    private static InserterInfo INSTANCE = new InserterInfo();
    private static String statId = getLocId(Inserter.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private InserterInfo() {
    }

    public static InserterInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = InserterInfo.Locator.class
    )
    public static void insert(Inserter _instance) {
        if (AbstractDungeon.player.maxOrbs < 10) {
            getInstance().amount += 1;
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(Inserter.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
