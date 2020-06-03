package relicstats.patches;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import relicstats.HasCustomStats;
import relicstats.RelicStats;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseModPatch {

    public static void checkCustomRelic(AbstractRelic relic) {
        Class<?> relicClass = relic.getClass();
        try {
            if (relicClass.isInstance(HasCustomStats.class)) {
                RelicStats.registerCustomStats(relic.relicId, (HasCustomStats)relic);
            }
            Method getStatsDescription = relicClass.getMethod("getStatsDescription");
            Method getExtendedStatsDescription = relicClass.getMethod("getExtendedStatsDescription");
            Method resetStats = relicClass.getMethod("resetStats");
            Method onSaveStats = relicClass.getMethod("onSaveStats");
            Method onLoadStats = relicClass.getMethod("onLoadStats", JsonElement.class);
            HasCustomStats customStats = new HasCustomStats() {

                private void handleError(Exception e) {
                    System.out.println("Custom stats for relic failed.");
                    e.printStackTrace();
                    RelicStats.unregisterCustomStats(relic.relicId);
                }

                @Override
                public String getStatsDescription() {
                    try {
                        return (String) getStatsDescription.invoke(relic);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleError(e);
                        return "";
                    }
                }

                @Override
                public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
                    try {
                        return (String)getExtendedStatsDescription.invoke(relic, totalCombats, totalTurns);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleError(e);
                        return "";
                    }
                }

                @Override
                public void resetStats() {
                    try {
                        resetStats.invoke(relic);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleError(e);
                    }
                }

                @Override
                public JsonElement onSaveRaw() {
                    try {
                        return (JsonElement) onSaveStats.invoke(relic);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleError(e);
                        return null;
                    }
                }

                @Override
                public void onLoadRaw(JsonElement jsonElement) {
                    try {
                        onLoadStats.invoke(relic, jsonElement);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        handleError(e);
                    }
                }
            };

            RelicStats.registerCustomStats(relic.relicId, customStats);
        } catch (NoSuchMethodException e) {
        }
    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "addRelic"
    )
    public static class AddRelicPatch {

        @SpirePrefixPatch
        public static void patch(AbstractRelic relic, RelicType type) {
            checkCustomRelic(relic);
        }

    }

    @SpirePatch(
            clz = BaseMod.class,
            method = "addRelicToCustomPool"
    )
    public static class AddRelicToCustomPoolPatch {

        @SpirePrefixPatch
        public static void patch(AbstractRelic relic, AbstractCard.CardColor color) {
            checkCustomRelic(relic);
        }
    }

}
