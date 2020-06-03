package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.ChampionsBelt;
import relicstats.CombatStatsInfo;

@SpirePatch(
        clz = ChampionsBelt.class,
        method = "onTrigger"
)
public class ChampionsBeltInfo extends CombatStatsInfo {

    private static String statId = getLocId(ChampionsBelt.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static ChampionsBeltInfo INSTANCE = new ChampionsBeltInfo();

    private ChampionsBeltInfo() {
    }

    public static ChampionsBeltInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void patch(ChampionsBelt _instance, AbstractCreature target) {
        getInstance().amount += 1;
    }

}
