package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BlackBlood;
import com.megacrit.cardcrawl.relics.BurningBlood;
import relicstats.AmountAdjustmentCallback;
import relicstats.StatsInfo;

public class BurningBlackBloodInfo extends StatsInfo implements AmountAdjustmentCallback {

    private static int healing;
    private static String statId = getLocId(BurningBlood.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int startingHp;
    private static boolean inCombat;

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

    @SpirePatch(
            clz=BurningBlood.class,
            method="onVictory"
    )
    public static class BurningBloodPatch {

        @SpirePrefixPatch
        public static void prefix(BurningBlood _instance) {
            new BurningBlackBloodInfo().registerStartingAmount(AbstractDungeon.player.currentHealth);
        }

        @SpirePostfixPatch
        public static void postfix(BurningBlood _instance) {
            new BurningBlackBloodInfo().registerEndingAmount(AbstractDungeon.player.currentHealth);
        }
    }

    @SpirePatch(
            clz= BlackBlood.class,
            method="onVictory"
    )
    public static class BlackBloodPatch {

        @SpirePrefixPatch
        public static void prefix(BlackBlood _instance) {
            new BurningBlackBloodInfo().registerStartingAmount(AbstractDungeon.player.currentHealth);
        }

        @SpirePostfixPatch
        public static void postfix(BlackBlood _instance) {
            new BurningBlackBloodInfo().registerEndingAmount(AbstractDungeon.player.currentHealth);
        }

    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        startingHp = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        healing += endingAmount - startingHp;
    }

}
