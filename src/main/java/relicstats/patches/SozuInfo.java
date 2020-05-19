package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.potions.EntropicBrew;
import com.megacrit.cardcrawl.relics.Sozu;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

public class SozuInfo extends StatsInfo {

    private static int potions;
    private static String statId = getLocId(Sozu.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], potions);
    }

    @Override
    public void resetStats() {
        potions = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(potions);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            potions = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    public static void increment() {
        potions += 1;
    }

    @SpirePatch(
            clz = ObtainPotionAction.class,
            method = "update"
    )
    public static class ObtainPotionActionPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(ObtainPotionAction _instance) {
            SozuInfo.increment();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }


    @SpirePatch(
            clz = KnowingSkull.class,
            method = "obtainReward"
    )
    public static class KnowingSkullPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(KnowingSkull _instance, int slot) {
            SozuInfo.increment();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }


    @SpirePatch(
            clz = EntropicBrew.class,
            method = "use"
    )
    public static class EntropicBrewPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(EntropicBrew _instance, AbstractCreature target) {
            for (int i = 0; i < AbstractDungeon.player.potionSlots; i++) {
                SozuInfo.increment();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }


    @SpirePatch(
            clz = RewardItem.class,
            method = "claimReward"
    )
    public static class RewardItemPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(RewardItem _instance) {
            SozuInfo.increment();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }
}
