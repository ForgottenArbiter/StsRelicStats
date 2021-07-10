package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.relics.WingBoots;
import relicstats.StatsInfo;

@SpirePatch(
        clz = Omamori.class,
        method = "use"
)
public class OmamoriInfo extends StatsInfo {

    private static int counter;
    private static String statId = getLocId(WingBoots.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], counter);
    }

    @Override
    public void resetStats() {
        counter = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(counter);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            counter = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void patch(Omamori _instance) {
        counter += 1;
    }

    public boolean showStats() {
        return !CardCrawlGame.isInARun();
    }

}
