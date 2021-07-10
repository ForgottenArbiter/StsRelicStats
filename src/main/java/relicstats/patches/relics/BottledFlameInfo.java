package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.BottledFlame;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = BottledFlame.class,
        method = "update"
)
public class BottledFlameInfo extends StatsInfo {

    private static String card;
    private static String savedText;
    private static String statId = getLocId(BottledFlame.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return savedText;
    }

    private static void updateDescription(String cardId) {
        StringBuilder statText = new StringBuilder(description[0]);
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
        savedText = statText.toString();
    }

    @Override
    public void resetStats() {
        card = "";
        savedText = description[0];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(card);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            String card = jsonElement.getAsString();
        } else {
            resetStats();
        }
        updateDescription(card);
    }

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void insert(BottledFlame _instance) {
        card = _instance.card.cardID;
        updateDescription(card);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(BottledFlame.class, "tips");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    @Override
    public boolean showStats() {
        return !CardCrawlGame.isInARun();
    }
}
