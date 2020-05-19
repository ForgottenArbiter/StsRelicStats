package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.SsserpentHead;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import relicstats.StatsInfo;

@SpirePatch(
        clz= SsserpentHead.class,
        method = "onEnterRoom"
)
public class SsserpentHeadInfo extends StatsInfo {

    private static int gold;
    private static String statId = getLocId(SsserpentHead.ID);
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
    public static void prefix(SsserpentHead _instance, AbstractRoom room) {
        startingGold = AbstractDungeon.player.gold;
    }

    @SpirePostfixPatch
    public static void postfix(SsserpentHead _instance, AbstractRoom room) {
        gold += AbstractDungeon.player.gold - startingGold;
    }

}

