package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.relics.IceCream;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = EnergyManager.class,
        method = "recharge"
)
public class IceCreamInfo extends CombatStatsInfo {

    private static String statId = getLocId(IceCream.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static IceCreamInfo INSTANCE = new IceCreamInfo();

    private IceCreamInfo() {
    }

    public static IceCreamInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpireInsertPatch(
            locator= Locator.class
    )
    public static void patch(EnergyManager _instance) {
        getInstance().amount += EnergyPanel.getCurrentEnergy();
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(EnergyPanel.class, "addEnergy");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
