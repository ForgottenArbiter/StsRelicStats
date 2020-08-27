package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Whetstone;
import relicstats.StatsInfo;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = Whetstone.class,
        method = "onEquip"
)
public class WhetstoneInfo extends StatsInfo {

    private static ArrayList<String> cards;
    private static String savedText;
    private static String statId = getLocId(Whetstone.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return savedText;
    }

    private static void updateDescription(ArrayList<String> relics) {
        StringBuilder statText = new StringBuilder(description[0]);
        for (String cardId: cards) {
            statText.append(" NL ");
            if (CardLibrary.isACard(cardId)) {
                AbstractCard card = CardLibrary.getCard(cardId);
                String regexkey = "";
                if (card.rarity == AbstractCard.CardRarity.RARE) {
                    regexkey = "$1#y$2";
                } else if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
                    regexkey = "$1#b$2";
                }
                String cardname = card.name;
                if (!regexkey.equals("")) {
                    cardname = cardname.replaceAll("(^|\\s)([^\\s])", regexkey);
                }
                statText.append(cardname);
            } else {
                statText.append(description[1]);
            }
        }
        savedText = statText.toString();
    }

    @Override
    public void resetStats() {
        cards = new ArrayList<>();
        savedText = description[0];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(cards);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            cards = new ArrayList<>();
            for (JsonElement e : jsonArray) {
                cards.add(e.getAsString());
            }
        } else {
            resetStats();
        }
        updateDescription(cards);
    }

    @SpireInsertPatch(
            locator = WhetstoneInfo.Locator.class,
            localvars = {"upgradableCards"}
    )
    public static void insert(Whetstone _instance, ArrayList<AbstractCard> upgradableCards) {
        List<AbstractCard> sublist = upgradableCards.subList(0, Math.min(2, upgradableCards.size()));
        for (AbstractCard card : sublist) {
            cards.add(card.cardID);
        }
        updateDescription(cards);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}

