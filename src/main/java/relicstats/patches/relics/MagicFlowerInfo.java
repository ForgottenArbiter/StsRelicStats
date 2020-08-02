package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MagicFlower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.HealingFollowupAction;

import java.util.ArrayList;

public class MagicFlowerInfo extends CombatStatsInfo {

    private static String statId = getLocId(MagicFlower.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static MagicFlowerInfo INSTANCE = new MagicFlowerInfo();

    private int startingHp;

    private MagicFlowerInfo () {}

    public static MagicFlowerInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePatch(
            clz = MagicFlower.class,
            method = "onPlayerHeal"
    )
    public static class FlowerPatch {

        @SpireInsertPatch(
                locator = MagicFlowerInfo.Locator.class
        )
        public static void before(MagicFlower _instance, int healAmount) {
            MagicFlowerInfo.getInstance().startingHp = AbstractDungeon.player.currentHealth + healAmount;
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "heal",
            paramtypez = {int.class, boolean.class}
    )
    public static class HealPatch {

        @SpirePostfixPatch
        public static void after(AbstractCreature _instance, int healAmount, boolean showEffect) {
            MagicFlowerInfo info = MagicFlowerInfo.getInstance();
            if (info.startingHp != 0) {
                int amountHealed = _instance.currentHealth - info.startingHp;
                info.amount += Math.max(amountHealed, 0);
                info.startingHp = 0;
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(MagicFlower.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
