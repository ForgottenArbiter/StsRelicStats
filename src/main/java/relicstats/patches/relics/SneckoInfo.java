package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.powers.CorruptionPower;
import com.megacrit.cardcrawl.relics.SneckoEye;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpirePatch(
        clz = ConfusionPower.class,
        method = "onCardDraw"
)
public class SneckoInfo extends StatsInfo {

    private static int[] costs = new int[5];
    private static String statId = getLocId(SneckoEye.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static Map<AbstractCard, Integer> cards;

    // Apparently static methods can't be overridden in Java or these would probably be static

    public String getStatsDescription() {
        StringBuilder toDisplay = new StringBuilder();
        int total_cards = 0;
        for(int i = 0; i < 4; i++) {
            toDisplay.append(description[i]);
            toDisplay.append(costs[i]);
            total_cards += costs[i];
        }
        toDisplay.append(description[4]);
        if (total_cards == 0) {
            total_cards = 1;
        }
        toDisplay.append(new DecimalFormat("#.###").format((float) (costs[4]) / total_cards));
        return toDisplay.toString();
    }

    public void resetStats() {
        costs = new int[5];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(costs);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < 5; i++) {
                if (i >= array.size()) {
                    costs[i] = 0;
                } else {
                    costs[i] = array.get(i).getAsInt();
                }
            }
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"newCost"}
    )
    public static void patch(ConfusionPower _instance, AbstractCard c, int newCost) {
        if (AbstractDungeon.player.hasRelic(SneckoEye.ID) && newCost >= 0 && newCost <= 3) {
            if (c.type != AbstractCard.CardType.SKILL || !AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID)) {
                costs[newCost] += 1;
                if (!cards.containsKey(c)) {
                    cards.put(c, c.cost);
                }
                // Record the difference between the original cost of the card (when first drawn) and the new cost
                costs[4] += (cards.get(c) - newCost);
            }
        }
    }

    public static void onBattleStart() {
        cards = new HashMap<>();
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "cost");
            int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(matches, 1, 2);
        }
    }

}
