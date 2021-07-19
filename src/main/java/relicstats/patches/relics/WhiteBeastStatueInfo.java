package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.WhiteBeast;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import relicstats.StatsInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WhiteBeastStatueInfo extends StatsInfo {

    private static double[] chances = new double[11];
    private static double potions = 0.0;

    private static String statId = getLocId(WhiteBeast.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        StringBuilder builder = new StringBuilder(description[0]);
        builder.append(new DecimalFormat("#.###").format(potions));
        return builder.toString();
    }

    @Override
    public void resetStats() {
        potions = 0.0;
        chances = new double[11];
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Double> stats = new ArrayList<Double>(12);
        for (double item : chances) {
            stats.add(item);
        }
        stats.add(potions);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        resetStats();
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < 12; i++) {
                if (i > jsonArray.size()) {
                    break;
                } else if (i < 11) {
                    chances[i] = jsonArray.get(i).getAsDouble();
                } else {
                    potions = jsonArray.get(i).getAsDouble();
                }
            }
        }
    }

    public static void captureCurrentChances() {
        int chance = 40;
        chance += AbstractRoom.blizzardPotionMod;
        int bucket = chance / 10;
        chances = new double[11];
        chances[bucket] = 1.0;
    }

    public static void updateProbabilities() {
        double[] newChances = new double[11];
        for (int i = 0; i < 11; i++) {
            double upChance = 0.1 * (10 - i);
            double downChance = 0.1 * i;
            if (i != 0) {
                newChances[i-1] += chances[i] * downChance;
            }
            if (i != 10) {
                newChances[i+1] += chances[i] * upChance;
            }
        }
        chances = newChances;
    }

    public static void addExpectedPotionAmount() {
        double expected = 0.0;
        for (int i = 0; i < 11; i++) {
            expected += (0.1 * i) * chances[i];
        }
        potions += expected;
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "addPotionToRewards",
            paramtypez = {}
    )
    public static class PotionRewardPatch {

        @SpirePrefixPatch
        public static void prefix(AbstractRoom _instance) {

            if (AbstractDungeon.getCurrRoom().rewardAllowed && AbstractDungeon.player.hasRelic(WhiteBeast.ID)) {
                addExpectedPotionAmount();
            }
            updateProbabilities();
        }

    }

    @SpirePatch(
            clz = WhiteBeast.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class WhiteBeastPickupPatch {
        // Thanks to kiooeht and Gk for advice/reference code for this one
        // Basically, we need to make sure we have the correct probability distribution on pickup.
        public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            CtClass superclass = ctClass.getSuperclass();
            CtMethod supermethod = superclass.getDeclaredMethod("onEquip");

            CtMethod method = CtNewMethod.delegator(supermethod, ctClass);
            try {
                ctClass.addMethod(method);
            } catch (DuplicateMemberException e) {
                method = ctClass.getDeclaredMethod("onEquip");
            }
            method.insertAfter("relicstats.patches.relics.WhiteBeastStatueInfo.captureCurrentChances();");
        }

    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class DungeonTransitionPatch {

        public static void Postfix() {
            captureCurrentChances();
        }

    }

}
