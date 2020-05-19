package relicstats.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.HandDrill;
import relicstats.CombatStatsInfo;

@SpirePatch(
        clz = HandDrill.class,
        method = "onBlockBroken"
)
public class HandDrillInfo extends CombatStatsInfo {

    private static HandDrillInfo INSTANCE = new HandDrillInfo();
    private static String statId = getLocId(HandDrill.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int VULN = (int) ReflectionHacks.getPrivateStatic(HandDrill.class, "VULNERABLE_AMT");

    private HandDrillInfo() {
    }

    public static HandDrillInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePostfixPatch
    public static void patch(HandDrill _instance, AbstractCreature m) {
        getInstance().amount += VULN;
    }

}
