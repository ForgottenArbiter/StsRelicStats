package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.DollysMirror;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = DollysMirror.class,
        method = "update"
)
public class DollysMirrorInfo extends StatsInfo {

    private static String cardId;
    private static String savedText;
    private static String statId = getLocId(DollysMirror.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return savedText;
    }

    private static void updateDescription() {
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
        cardId = "";
        savedText = description[0];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(cardId);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            cardId = jsonElement.getAsString();
        } else {
            resetStats();
        }
        updateDescription();
    }

    @SpireInsertPatch(
            locator = DollysMirrorInfo.Locator.class
    )
    public static void insert(DollysMirror _instance) {
        cardId = AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID;
        updateDescription();
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "makeStatEquivalentCopy");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }
}

