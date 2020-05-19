package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.OrangePellets;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.OrangePelletsFollowupAction;
import relicstats.actions.PreOrangePelletsAction;

@SpirePatch(
        clz = OrangePellets.class,
        method = "onUseCard"
)
public class OrangePelletsInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private static OrangePelletsInfo INSTANCE;
    private static String statId = getLocId(OrangePellets.ID);
    private static String[] description;
    public static int methodcall = 0;
    private int previous_debuffs;

    private OrangePelletsInfo() {
    }

    public static OrangePelletsInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrangePelletsInfo();
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
    @Override
    public void registerStartingAmount(int startingAmount) {
        previous_debuffs = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        amount += previous_debuffs - endingAmount;
    }

    public static void doBefore() {
        AbstractDungeon.actionManager.addToBottom(new PreOrangePelletsAction(OrangePelletsInfo.getInstance()));
    }

    public static void doAfter() {
        AbstractDungeon.actionManager.addToBottom(new OrangePelletsFollowupAction(OrangePelletsInfo.getInstance()));
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(OrangePellets.class.getName()) && m.getMethodName().equals("addToBot")) {
                    methodcall += 1;
                    if (methodcall == 2) {
                        m.replace("{relicstats.patches.OrangePelletsInfo.doBefore(); $_ = $proceed($$); relicstats.patches.OrangePelletsInfo.doAfter();}");
                    }
                }
            }
        };
    }

}
