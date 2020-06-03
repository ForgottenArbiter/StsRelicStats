package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BloodVial;
import relicstats.AmountAdjustmentCallback;
import relicstats.StatsInfo;
import relicstats.actions.HealingFollowupAction;
import relicstats.actions.PreHealingAction;

@SpirePatch(
        clz = BloodVial.class,
        method = "atBattleStart"
)
public class BloodVialInfo extends StatsInfo implements AmountAdjustmentCallback {

    private static int healing;
    private static String statId = getLocId(BloodVial.ID);
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
    public static void prefix(BloodVial _instance) {
        AbstractDungeon.actionManager.addToTop(new HealingFollowupAction(new BloodVialInfo()));
    }

    @SpirePostfixPatch
    public static void postfix(BloodVial _instance) {
        AbstractDungeon.actionManager.addToTop(new PreHealingAction(new BloodVialInfo()));
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
