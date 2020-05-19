package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.StoneCalendar;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

import java.util.ArrayList;

@SpirePatch(
        clz = StoneCalendar.class,
        method = "onPlayerEndTurn"
)
public class StoneCalendarInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(StoneCalendar.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static PreAoeDamageAction preAction;
    private static StoneCalendarInfo INSTANCE = new StoneCalendarInfo();

    private StoneCalendarInfo () {}

    public static StoneCalendarInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator=StoneCalendarInfo.Locator1.class
    )
    public static void before(StoneCalendar _instance) {
        preAction = new PreAoeDamageAction();
        AbstractDungeon.actionManager.addToBottom(preAction);
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(StoneCalendar.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @SpireInsertPatch(
            locator=StoneCalendarInfo.Locator2.class
    )
    public static void after(StoneCalendar _instance) {
        StoneCalendarInfo info = StoneCalendarInfo.getInstance();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(info, StoneCalendarInfo.preAction);
        AbstractDungeon.actionManager.addToBottom(postAction);
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(StoneCalendar.class, "stopPulse");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += amount;
    }

}
