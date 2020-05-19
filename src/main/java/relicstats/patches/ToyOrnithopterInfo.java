package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.ToyOrnithopter;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import relicstats.AmountAdjustmentCallback;
import relicstats.StatsInfo;
import relicstats.actions.HealingFollowupAction;
import relicstats.actions.PreHealingAction;

@SpirePatch(
        clz = ToyOrnithopter.class,
        method = "onUsePotion"
)
public class ToyOrnithopterInfo extends StatsInfo implements AmountAdjustmentCallback {

    private static int healing;
    private static String statId = getLocId(ToyOrnithopter.ID);
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

    @SpirePrefixPatch
    public static void prefix(ToyOrnithopter _instance) {
        startingHp = AbstractDungeon.player.currentHealth;
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            inCombat = true;
            AbstractDungeon.actionManager.addToBottom(new PreHealingAction(new ToyOrnithopterInfo()));
        } else {
            inCombat = false;
        }
    }

    @SpirePostfixPatch
    public static void postfix(ToyOrnithopter _instance) {
        if (inCombat) {
            AbstractDungeon.actionManager.addToBottom(new HealingFollowupAction(new ToyOrnithopterInfo()));
        } else {
            healing += (AbstractDungeon.player.currentHealth - startingHp);
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
