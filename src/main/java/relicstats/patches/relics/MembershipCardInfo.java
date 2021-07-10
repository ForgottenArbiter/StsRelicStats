package relicstats.patches.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import relicstats.RelicStats;
import relicstats.StatsInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MembershipCardInfo extends StatsInfo {

    private static final Logger logger = LogManager.getLogger(MembershipCardInfo.class.getName());
    private static int discount;
    private static String statId = getLocId(MembershipCard.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static Map<AbstractCard, Integer> cardDiscounts = new HashMap<>();
    private static Map<StoreRelic, Integer> relicDiscounts = new HashMap<>();
    private static Map<StorePotion, Integer> potionDiscounts = new HashMap<>();

    @Override
    public String getStatsDescription() {
        String addendum = "";
        if ((AbstractDungeon.player == null) || (!CardCrawlGame.isInARun()) || (AbstractDungeon.player.hasRelic(Courier.ID) && AbstractDungeon.player.hasRelic(MembershipCard.ID))) {
            addendum = description[1];
        }
        return String.format("%s%d%s", description[0], discount, addendum);
    }

    @Override
    public void resetStats() {
        discount = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(discount);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            discount = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "init"
    )
    public static class ShopScreenInitPatch {

        @SpirePrefixPatch
        public static void patch(ShopScreen _instance, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
            cardDiscounts = new HashMap<>();
            relicDiscounts = new HashMap<>();
            potionDiscounts = new HashMap<>();
        }

    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "applyDiscount"
    )
    public static class ShopScreenCardPricePatch {

        @SpireInsertPatch(
                locator=RelicLocator.class,
                localvars={"r"}
        )
        public static void relicPricePatch(ShopScreen _instance, float multiplier, boolean affectPurge, StoreRelic r) {
            if (multiplier >= 1.0) {
                return;
            }
            int currentDiscount = relicDiscounts.getOrDefault(r, 0);
            currentDiscount += (r.price - MathUtils.round(r.price * multiplier));
            relicDiscounts.put(r, currentDiscount);
        }

        private static class RelicLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(MathUtils.class, "round");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

        @SpireInsertPatch(
                locator=PotionLocator.class,
                localvars={"p"}
        )
        public static void potionPricePatch(ShopScreen _instance, float multiplier, boolean affectPurge, StorePotion p) {
            if (multiplier >= 1.0) {
                return;
            }
            int currentDiscount = potionDiscounts.getOrDefault(p, 0);
            currentDiscount += (p.price - MathUtils.round(p.price * multiplier));
            potionDiscounts.put(p, currentDiscount);
        }

        private static class PotionLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.FieldAccessMatcher(StorePotion.class, "price");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

        @SpireInsertPatch(
                locator=CardLocator.class,
                localvars={"c"}
        )
        public static void cardPricePatch(ShopScreen _instance, float multiplier, boolean affectPurge, AbstractCard c) {
            if (multiplier >= 1.0) {
                return;
            }
            int currentDiscount = cardDiscounts.getOrDefault(c, 0);
            currentDiscount += (c.price - MathUtils.round(c.price * multiplier));
            cardDiscounts.put(c, currentDiscount);
        }

        private static class CardLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(MathUtils.class, "round");
                int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
                return Arrays.copyOfRange(matches, 2, 4);
            }
        }
    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "purchaseCard"
    )
    public static class ShopScreenCardPurchasePatch {

        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void insert(ShopScreen _instance, AbstractCard hoveredCard) {
            if (cardDiscounts.containsKey(hoveredCard)) {
                int cardDiscount = cardDiscounts.remove(hoveredCard);
                discount += cardDiscount;
            } else {
                logger.error("Could not find discount for purchased card.");
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseGold");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }
    }

    @SpirePatch(
            clz = StoreRelic.class,
            method = "purchaseRelic"
    )
    public static class RelicPurchasePatch {

        @SpireInsertPatch(
                locator= Locator.class
        )
        public static void insert(StoreRelic _instance) {
            if (relicDiscounts.containsKey(_instance)) {
                int relicDiscount = relicDiscounts.remove(_instance);
                discount += relicDiscount;
            } else {
                logger.error("Could not find discount for purchased relic.");
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseGold");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

    @SpirePatch(
            clz = StorePotion.class,
            method = "purchasePotion"
    )
    public static class PotionPurchasePatch {

        @SpireInsertPatch(
                locator= Locator.class
        )
        public static void insert(StorePotion _instance) {
            if (potionDiscounts.containsKey(_instance)) {
                int potionDiscount = potionDiscounts.remove(_instance);
                discount += potionDiscount;
            } else {
                logger.error("Could not find discount for purchased potion.");
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseGold");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "getNewPrice",
            paramtypez = {StoreRelic.class}
    )
    public static class RelicRestockPatch {

        private static int initialPrice;

        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {"retVal"}
        )
        public static void insert(ShopScreen _instance, StoreRelic r, int retVal) {
            initialPrice = retVal;
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

        @SpirePostfixPatch
        public static void postfix(ShopScreen _instance, StoreRelic r) {
            int relicDiscount = initialPrice - r.price;
            relicDiscounts.put(r, relicDiscount);
        }

    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "getNewPrice",
            paramtypez = {StorePotion.class}
    )
    public static class PotionRestockPatch {

        private static int initialPrice;

        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {"retVal"}
        )
        public static void insert(ShopScreen _instance, StorePotion p, int retVal) {
            initialPrice = retVal;
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

        @SpirePostfixPatch
        public static void postfix(ShopScreen _instance, StorePotion p) {
            int potionDiscount = initialPrice - p.price;
            potionDiscounts.put(p, potionDiscount);
        }

    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "setPrice"
    )
    public static class CardRestockPatch {

        private static int initialPrice;

        @SpireInsertPatch(
                locator= Locator.class,
                localvars = {"tmpPrice"}
        )
        public static void insert(ShopScreen _instance, AbstractCard c, float tmpPrice) {
            initialPrice = (int)tmpPrice;
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

        @SpirePostfixPatch
        public static void postfix(ShopScreen _instance, AbstractCard c) {
            int cardDiscount = initialPrice - c.price;
            cardDiscounts.put(c, cardDiscount);
        }

    }

    @SpirePatch(
            clz = ShopScreen.class,
            method = "purgeCard"
    )
    public static class PurgeCardPatch {

        @SpirePrefixPatch
        public static void patch() {
            float discountedCost = (float)ShopScreen.purgeCost;
            if (AbstractDungeon.player.hasRelic(MembershipCard.ID)) {
                discountedCost *= 0.5;
            } else if (AbstractDungeon.player.hasRelic(Courier.ID)) {
                // This is else if because the base game is bugged to only apply membership card here if you own both
                discountedCost *= 0.8;
            }
            discountedCost = MathUtils.round(discountedCost);
            discount += ShopScreen.purgeCost - discountedCost;
        }

    }



}
