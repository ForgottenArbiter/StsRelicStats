package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.InkBottle;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

@SpirePatch(
        clz = InkBottle.class,
        method = "onUseCard"
)
public class InkBottleInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    public static int methodCount = 0;
    private static InkBottleInfo INSTANCE;
    private static String statId = getLocId(InkBottle.ID);
    private static String[] description = null;

    private int cardsBefore;

    private InkBottleInfo () {}

    public static InkBottleInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InkBottleInfo();
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
        AbstractDungeon.actionManager.addToBottom(new PreCardDrawAction(InkBottleInfo.getInstance()));
    }

    public static void doAfter() {
        AbstractDungeon.actionManager.addToBottom(new CardDrawFollowupAction(InkBottleInfo.getInstance()));
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(InkBottle.class.getName()) && m.getMethodName().equals("addToBot")) {
                    methodCount += 1;
                    if (methodCount == 1) {
                        m.replace("{relicstats.patches.relics.InkBottleInfo.doBefore(); $_ = $proceed($$);}");
                    } else if (methodCount == 2) {
                        m.replace("{$_ = $proceed($$); relicstats.patches.relics.InkBottleInfo.doAfter();}");
                    }
                }
            }
        };
    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        cardsBefore = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        amount += endingAmount - cardsBefore;
    }
}
