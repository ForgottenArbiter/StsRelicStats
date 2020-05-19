package relicstats.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.shop.ShopScreen;
import relicstats.StatsInfo;

@SpirePatch(
        clz = ShopScreen.class,
        method = "purgeCard"
)
public class SmilingMaskInfo extends StatsInfo {

    private static int gold;
    private static String statId = getLocId(SmilingMask.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

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
    public static void patch() {
        float baseCost = (float)ShopScreen.purgeCost;
        if (AbstractDungeon.player.hasRelic(Courier.ID)) {
            baseCost *= 0.8;
        }
        if (AbstractDungeon.player.hasRelic(MembershipCard.ID)) {
            baseCost *= 0.5;
        }
        int discount = MathUtils.round(baseCost) - 50;
        gold += discount;
    }

}
