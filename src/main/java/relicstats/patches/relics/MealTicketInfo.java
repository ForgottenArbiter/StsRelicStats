package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MealTicket;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import relicstats.StatsInfo;

@SpirePatch(
        clz= MealTicket.class,
        method = "justEnteredRoom"
)
public class MealTicketInfo extends StatsInfo {

    private static int healing;
    private static String statId = getLocId(MealTicket.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingHp;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], healing);
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
    public static void prefix(MealTicket _instance, AbstractRoom room) {
        startingHp = AbstractDungeon.player.currentHealth;
    }

    @SpirePostfixPatch
    public static void postfix(MealTicket _instance, AbstractRoom room) {
        healing += AbstractDungeon.player.currentHealth - startingHp;
    }
}
