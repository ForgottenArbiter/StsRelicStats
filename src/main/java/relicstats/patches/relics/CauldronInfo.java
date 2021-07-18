package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.MetricData;
import com.megacrit.cardcrawl.relics.Cauldron;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

public class CauldronInfo extends StatsInfo {

    private static int counter;
    private static String statId = getLocId(Cauldron.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static ArrayList<RewardItem> rewards = new ArrayList<>();

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], counter);
    }

    @Override
    public void resetStats() {
        counter = 0;
        rewards = new ArrayList<>();
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(counter);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            counter = jsonElement.getAsInt();
            rewards = new ArrayList<>();
        } else {
            resetStats();
        }
    }

    @SpirePatch(
            clz = Cauldron.class,
            method = "onEquip"
    )
    public static class CauldronPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(Cauldron _instance) {
            rewards = new ArrayList<>(AbstractDungeon.getCurrRoom().rewards);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(CombatRewardScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

    @SpirePatch(
            clz = RewardItem.class,
            method = "claimReward"
    )
    public static class PotionRewardItemPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(RewardItem _instance) {
            if (rewards.contains(_instance)) {
                counter += 1;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(MetricData.class, "addPotionObtainData");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

}
