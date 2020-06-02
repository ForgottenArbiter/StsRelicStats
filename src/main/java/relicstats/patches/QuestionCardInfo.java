package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.QuestionCard;
import relicstats.StatsInfo;

@SpirePatch(
        clz = QuestionCard.class,
        method = "changeNumberOfCardsInReward"
)
public class QuestionCardInfo extends StatsInfo {

    private static int cards;
    private static String statId = getLocId(QuestionCard.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], cards);
    }

    @Override
    public void resetStats() {
        cards = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(cards);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            cards = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(QuestionCard _instance, int numberOfCards) {
        cards += 1;
    }

}
