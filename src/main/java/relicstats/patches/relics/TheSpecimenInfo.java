package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerToRandomEnemyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.TheSpecimen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.CombatStatsInfo;


public class TheSpecimenInfo extends CombatStatsInfo {

    private static String statId = getLocId(TheSpecimen.ID);
    private static String[] description = null;
    private static TheSpecimenInfo INSTANCE = null;
    private static ApplyPowerToRandomEnemyAction action;
    private static boolean first = true;

    private TheSpecimenInfo() {
    }

    public static TheSpecimenInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TheSpecimenInfo();
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

    @SpirePatch(
            clz = TheSpecimen.class,
            method = "onMonsterDeath"
    )
    public static class TheSpecimenPatch {

        @SpireInstrumentPatch()
        public static ExprEditor patch()
        {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(TheSpecimen.class.getName()) && m.getMethodName().equals("addToBot")) {
                        if (first) {
                            first = false;
                        } else {
                            m.replace("{$_ = $proceed($$); relicstats.patches.relics.TheSpecimenInfo.do_after();}");
                        }
                    }
                }
            };
        }

    }

    @SpirePatch(
            clz = ApplyPowerToRandomEnemyAction.class,
            method = "update"
    )
    public static class ApplyPowerPatch {

        @SpirePostfixPatch
        public static void postfix(ApplyPowerToRandomEnemyAction _instance) {
            if (_instance == action) {
                AbstractCreature target = _instance.target;
                AbstractPower poisonPower = target.getPower(PoisonPower.POWER_ID);
                int poisonAmount = 0;
                if (poisonPower != null) {
                    poisonAmount = poisonPower.amount;
                }
                int remainingHp = target.currentHealth - poisonAmount;
                int relevantPoison = Math.min(remainingHp, _instance.amount);
                getInstance().amount += relevantPoison;
            }
        }

    }

    public static void do_after() {
        GameActionManager manager = AbstractDungeon.actionManager;
        AbstractGameAction lastAction = manager.actions.get(manager.actions.size() - 1);
        if (lastAction instanceof ApplyPowerToRandomEnemyAction) {
            action = (ApplyPowerToRandomEnemyAction) lastAction;
        }
    }

}
