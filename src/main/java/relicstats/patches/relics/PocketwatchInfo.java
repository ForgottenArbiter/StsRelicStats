package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Pocketwatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

@SpirePatch(
        clz = Pocketwatch.class,
        method = "atTurnStartPostDraw"
)
public class PocketwatchInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static PocketwatchInfo INSTANCE = null;
    private static String statId = getLocId(Pocketwatch.ID);
    private static String[] description = null;
    private static int startingCards;

    private PocketwatchInfo() {
    }

    public static PocketwatchInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PocketwatchInfo();
        }
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
        }
        return description[0];
    }

    public static void doBefore() {
        AbstractDungeon.actionManager.addToBottom(new PreCardDrawAction(PocketwatchInfo.getInstance()));
    }

    public static void doAfter() {
        AbstractDungeon.actionManager.addToBottom(new CardDrawFollowupAction(PocketwatchInfo.getInstance()));
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(Pocketwatch.class.getName()) && m.getMethodName().equals("addToBot")) {
                    m.replace("{relicstats.patches.relics.PocketwatchInfo.doBefore(); $_ = $proceed($$); relicstats.patches.relics.PocketwatchInfo.doAfter();}");
                }
            }
        };
    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        startingCards = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        amount += endingAmount - startingCards;
    }

}
