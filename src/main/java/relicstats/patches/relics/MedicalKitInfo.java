package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.MedicalKit;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = MedicalKit.class,
        method = "onUseCard"
)
public class MedicalKitInfo extends CombatStatsInfo {

    private static MedicalKitInfo INSTANCE = new MedicalKitInfo();
    private static String statId = getLocId(MedicalKit.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private MedicalKitInfo() {
    }

    public static MedicalKitInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator = MedicalKitInfo.Locator.class
    )
    public static void insert(MedicalKit _instance, AbstractCard card, UseCardAction action) {
        if (!card.exhaust) {
            getInstance().amount += 1;
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
