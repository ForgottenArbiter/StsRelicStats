package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Calipers;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = GameActionManager.class,
        method = "getNextAction"
)
public class CalipersInfo extends CombatStatsInfo {

    private static String statId = getLocId(Calipers.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static CalipersInfo INSTANCE = new CalipersInfo();

    private CalipersInfo() {
    }

    public static CalipersInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator= Locator.class
    )
    public static void patch(GameActionManager _instance) {
        int blockRetained = AbstractDungeon.player.currentBlock - 15;
        if (blockRetained < 0) {
            blockRetained = 0;
        }
        getInstance().amount += blockRetained;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseBlock");
            int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(matches, 1, 2);
        }
    }

}
