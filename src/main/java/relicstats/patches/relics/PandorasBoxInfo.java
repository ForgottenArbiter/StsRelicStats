package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = PandorasBox.class,
        method = "onEquip"
)
public class PandorasBoxInfo extends StatsInfo {

    private static ArrayList<String> cards;
    private static String savedText;
    private static String statId = getLocId(PandorasBox.ID);
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
            locator = PandorasBoxInfo.Locator.class,
            localvars = {"group"}
    )
    public static void insert(PandorasBox _instance, CardGroup group) {
        for (AbstractCard card : group.group) {
            cards.add(card.cardID);
        }
        updateDescription(cards);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "openConfirmationGrid");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
