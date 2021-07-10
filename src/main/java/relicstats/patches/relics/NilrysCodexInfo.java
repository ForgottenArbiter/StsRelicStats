package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.unique.CodexAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.NilrysCodex;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CodexAction.class,
        method = "update"
)
public class NilrysCodexInfo extends StatsInfo {

    private static int[] totals = new int[4];
    // Rares, uncommons, commons
    private static String statId = getLocId(NilrysCodex.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    // Apparently static methods can't be overridden in Java or these would probably be static

    public String getStatsDescription() {
        StringBuilder toDisplay = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            toDisplay.append(description[i]);
            toDisplay.append(totals[i]);
        }
        return toDisplay.toString();
    }

    public void resetStats() {
        totals = new int[4];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(totals);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < 4; i++) {
                totals[i] = array.get(i).getAsInt();
            }
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator= Locator.class
    )
    public static void patch(CodexAction _instance) {
        AbstractCard discoveredCard = AbstractDungeon.cardRewardScreen.discoveryCard;
        if (discoveredCard == null) {
            totals[3] += 1;
        } else if (discoveredCard.rarity == AbstractCard.CardRarity.COMMON) {
            totals[0] += 1;
        } else if (discoveredCard.rarity == AbstractCard.CardRarity.UNCOMMON) {
            totals[1] += 1;
        } else if (discoveredCard.rarity == AbstractCard.CardRarity.RARE) {
            totals[2] += 1;
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(CardRewardScreen.class, "discoveryCard");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
