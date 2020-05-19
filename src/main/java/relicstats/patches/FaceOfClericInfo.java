package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.FaceOfCleric;
import relicstats.StatsInfo;

@SpirePatch(
        clz = FaceOfCleric.class,
        method = "onVictory"
)
public class FaceOfClericInfo extends StatsInfo {


    private static int hp;
    private static String statId = getLocId(FaceOfCleric.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], hp);
    }

    @Override
    public void resetStats() {
        hp = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(hp);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            hp = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(FaceOfCleric _instance) {
        hp += 1;
    }

}
