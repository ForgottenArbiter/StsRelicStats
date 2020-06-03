package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.LetterOpener;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

@SpirePatch(
        clz = LetterOpener.class,
        method = "onUseCard"
)
public class LetterOpenerInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(LetterOpener.ID);
    private static String[] description = null;
    private static PreAoeDamageAction preAction;
    private static LetterOpenerInfo INSTANCE = null;
    public static boolean first = true;

    private LetterOpenerInfo () {}

    public static LetterOpenerInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LetterOpenerInfo();
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

    public static void do_before() {
        preAction = new PreAoeDamageAction();
        AbstractDungeon.actionManager.addToBottom(preAction);
        System.out.println("before");
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(LetterOpener.class.getName()) && m.getMethodName().equals("addToBot")) {
                    if (LetterOpenerInfo.first) {
                        LetterOpenerInfo.first = false;
                    } else {
                        m.replace("{relicstats.patches.relics.LetterOpenerInfo.do_before(); $_ = $proceed($$); relicstats.patches.relics.LetterOpenerInfo.do_after();}");
                    }
                }
            }
        };
    }

    public static void do_after() {
        LetterOpenerInfo info = LetterOpenerInfo.getInstance();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(info, LetterOpenerInfo.preAction);
        AbstractDungeon.actionManager.addToBottom(postAction);
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += amount;
    }


}
