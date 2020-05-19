package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CeramicFish;
import relicstats.StatsInfo;

@SpirePatch(
        clz = CeramicFish.class,
        method = "onObtainCard"
)
public class CeramicFishInfo extends StatsInfo {

    private static int gold;
    private static String statId = getLocId(CeramicFish.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingGold;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], gold);
    }

    @Override
    public void resetStats() {
        gold = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(gold);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            gold = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(CeramicFish _instance, AbstractCard card) {
        startingGold = AbstractDungeon.player.gold;
    }

    @SpirePostfixPatch
    public static void postfix(CeramicFish _instance, AbstractCard card) {
        gold += AbstractDungeon.player.gold - startingGold;
    }

}
