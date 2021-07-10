package relicstats.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.RelicStats;

import java.util.ArrayList;

@SpirePatch(
        clz = SingleRelicViewPopup.class,
        method = "renderTips"
)
public class SingleRelicViewPopupPatch {

    static String statsHeader = null;

    public static boolean shouldShowStats() {
        if (CardCrawlGame.mode == CardCrawlGame.GameMode.CHAR_SELECT && CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RUN_HISTORY) {
            return RunHistoryScreenPatch.runHistoryHasStats;
        }
        return CardCrawlGame.mode == CardCrawlGame.GameMode.GAMEPLAY && AbstractDungeon.player != null;
    }

    @SpireInsertPatch(
            locator= SingleRelicViewPopupPatch.Locator.class,
            localvars={"t"}
    )
    public static void patch(SingleRelicViewPopup _instance, SpriteBatch sb, ArrayList<PowerTip> t) {
        AbstractRelic relic = (AbstractRelic)ReflectionHacks.getPrivate(_instance, SingleRelicViewPopup.class, "relic");
        if (RelicStats.hasStatsMessage(relic.relicId) && shouldShowStats()) {
            t.add(new PowerTip(RelicStats.statsHeader, RelicStats.getStatsDescription(relic.relicId)));
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
