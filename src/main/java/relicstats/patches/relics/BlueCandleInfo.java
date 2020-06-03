package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.BlueCandle;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = BlueCandle.class,
        method = "onUseCard"
)
public class BlueCandleInfo extends CombatStatsInfo {

    private static BlueCandleInfo INSTANCE = new BlueCandleInfo();
    private static String statId = getLocId(BlueCandle.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private BlueCandleInfo() {
    }

    public static BlueCandleInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = BlueCandleInfo.Locator.class
    )
    public static void insert(BlueCandle _instance, AbstractCard card, UseCardAction action) {
        getInstance().amount += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
