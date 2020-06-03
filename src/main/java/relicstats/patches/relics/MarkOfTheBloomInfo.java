package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.MarkOfTheBloom;
import relicstats.StatsInfo;

@SpirePatch(
        clz = MarkOfTheBloom.class,
        method = "onPlayerHeal"
)
public class MarkOfTheBloomInfo extends StatsInfo {


    private static int amount;
    private static String statId = getLocId(MarkOfTheBloom.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], amount);
    }

    @Override
    public void resetStats() {
        amount = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(amount);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            amount = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePostfixPatch
    public static void patch(MarkOfTheBloom _instance, int healAmount) {
        amount += healAmount;
    }

}
