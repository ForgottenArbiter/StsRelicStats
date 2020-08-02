package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.relics.BronzeScales;
import com.megacrit.cardcrawl.relics.MagicFlower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

import java.util.ArrayList;

@SpirePatch(
        clz = ThornsPower.class,
        method = "onAttacked"
)
public class BronzeScalesInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(BronzeScales.ID);
    private static String[] description = null;
    private static PreAoeDamageAction preAction;
    private static BronzeScalesInfo INSTANCE;

    private BronzeScalesInfo () {}

    public static BronzeScalesInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BronzeScalesInfo();
        }
        return INSTANCE;
    }

    public static void do_before() {
        preAction = new PreAoeDamageAction();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(getInstance(), BronzeScalesInfo.preAction);
        AbstractDungeon.actionManager.addToTop(postAction);
    }

    public static void do_after() {
        if (preAction != null) {
            AbstractDungeon.actionManager.addToTop(preAction);
        }
    }


    @SpireInsertPatch(
            locator = BronzeScalesInfo.Locator.class
    )
    public static void patch(ThornsPower _instance, DamageInfo info, int damageAmount) {
        if (_instance.owner.isPlayer && AbstractDungeon.player.hasRelic(BronzeScales.ID)) {
            do_before();
        } else {
            preAction = null;
        }
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(ThornsPower.class.getName()) && m.getMethodName().equals("addToTop")) {
                    m.replace("{$_ = $proceed($$); relicstats.patches.relics.BronzeScalesInfo.do_after();}");
                }
            }
        };
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(ThornsPower.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @Override
    public String getBaseDescription() {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
        }
        return description[0];
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += Math.min(amount, 3);
    }

}
