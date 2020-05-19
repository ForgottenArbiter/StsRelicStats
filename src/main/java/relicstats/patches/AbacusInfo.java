package relicstats.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Abacus;
import relicstats.CombatStatsInfo;

@SpirePatch(
        clz = Abacus.class,
        method = "onShuffle"
)
public class AbacusInfo extends CombatStatsInfo {

    private static AbacusInfo INSTANCE = new AbacusInfo();
    private static String statId = getLocId(Abacus.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int BLOCK = (int) ReflectionHacks.getPrivateStatic(Abacus.class, "BLOCK_AMT");

    private AbacusInfo() {
    }

    public static AbacusInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePostfixPatch
    public static void patch(Abacus _instance) {
        getInstance().amount += BLOCK;
    }

}
