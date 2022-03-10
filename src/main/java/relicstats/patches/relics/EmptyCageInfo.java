package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.EmptyCage;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = EmptyCage.class,
        method = "deleteCards"
)
public class EmptyCageInfo extends StatsInfo {

    private static ArrayList<String> cards;
    private static String savedText;
    private static String statId = getLocId(EmptyCage.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return savedText;
    }

    private static void updateDescription() {
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
        updateDescription();
    }

    @SpirePrefixPatch
    public static void prefix(EmptyCage _instance, ArrayList<AbstractCard> group) {
        for (final AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
            cards.add(card.cardID);
        }
        updateDescription();
    }
}
