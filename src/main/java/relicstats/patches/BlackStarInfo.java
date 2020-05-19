package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BlackStar;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import relicstats.StatsInfo;

@SpirePatch(
        clz= BlackStar.class,
        method = "onVictory"
)
public class BlackStarInfo extends StatsInfo {


    private static int relics;
    private static String statId = getLocId(BlackStar.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], relics);
    }

    @Override
    public void resetStats() {
        relics = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(relics);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            relics = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(BlackStar _instance) {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
            relics += 1;
        }
    }

}
