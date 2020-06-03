package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import relicstats.StatsInfo;

@SpirePatch(
        clz = SingingBowlButton.class,
        method = "onClick"
)
public class SingingBowlInfo extends StatsInfo {
    private static int hp;
    private static String statId = getLocId(SingingBowl.ID);
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

    @SpirePostfixPatch
    public static void patch(SingingBowlButton _instance) {
        hp += 2;
    }

}
