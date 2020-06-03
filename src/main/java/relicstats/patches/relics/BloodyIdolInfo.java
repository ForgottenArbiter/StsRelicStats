package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BloodyIdol;
import relicstats.StatsInfo;

@SpirePatch(
        clz = BloodyIdol.class,
        method = "onGainGold"
)
public class BloodyIdolInfo extends StatsInfo {

    private static int healing;
    private static String statId = getLocId(BloodyIdol.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingHp;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d NL %s", description[0], healing, new GoldenIdolInfo().getStatsDescription());
    }

    @Override
    public void resetStats() {
        healing = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(healing);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            healing = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(BloodyIdol _instance) {
        startingHp = AbstractDungeon.player.currentHealth;
    }

    @SpirePostfixPatch
    public static void postfix(BloodyIdol _instance) {
        healing += AbstractDungeon.player.currentHealth - startingHp;
    }

}
