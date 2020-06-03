package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.ToughBandages;
import relicstats.CombatStatsInfo;

@SpirePatch(
        clz = ToughBandages.class,
        method = "onManualDiscard"
)
public class ToughBandagesInfo extends CombatStatsInfo {

    private static ToughBandagesInfo INSTANCE = new ToughBandagesInfo();
    private static String statId = getLocId(ToughBandages.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int BLOCK = (int) ReflectionHacks.getPrivateStatic(ToughBandages.class, "BLOCK_AMT");

    private ToughBandagesInfo() {
    }

    public static ToughBandagesInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePostfixPatch
    public static void patch(ToughBandages _instance) {
        getInstance().amount += BLOCK;
    }

}
