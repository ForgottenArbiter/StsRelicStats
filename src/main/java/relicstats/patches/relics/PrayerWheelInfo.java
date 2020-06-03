package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.PrayerWheel;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CombatRewardScreen.class,
        method = "setupItemReward"
)
public class PrayerWheelInfo extends StatsInfo {

    private static int cardRewards;
    private static String statId = getLocId(PrayerWheel.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], cardRewards);
    }

    @Override
    public void resetStats() {
        cardRewards = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(cardRewards);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            cardRewards = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void patch(CombatRewardScreen _instance) {
        cardRewards += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher1 = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
            int[] results1 = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher1);
            int threshold = results1[0];
            Matcher matcher2 = new Matcher.FieldAccessMatcher(CombatRewardScreen.class, "rewards");
            int[] results2 = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher2);
            for (int result : results2) {
                if (result > threshold) {
                    return new int[] {result};
                }
            }
            throw new RuntimeException("Can't find patch location for PrayerWheelInfo.");
        }
    }


}
