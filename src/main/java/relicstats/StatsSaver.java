package relicstats;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SpirePatch(
        clz = Metrics.class,
        method = "gatherAllData"
)
public class StatsSaver {

    public static final String RELIC_JSON_KEY = "relic_stats";
    public static final String COUNTERS_JSON_KEY = "counters";
    public static final String OBTAIN_JSON_KEY = "obtain_stats";

    public static JsonElement saveRelics() {
        HashMap<String, JsonElement> savedata = new HashMap<>();
        for (AbstractRelic relic: AbstractDungeon.player.relics) {
            if (RelicStats.hasStats(relic.relicId)) {
                savedata.put(relic.relicId, RelicStats.getCustomStats(relic.relicId).onSaveRaw());
            }
        }
        savedata.put(COUNTERS_JSON_KEY, RelicStats.mod.onSaveRaw());
        savedata.put(OBTAIN_JSON_KEY, new RelicObtainStats().onSaveRaw());
        return new Gson().toJsonTree(savedata);
    }

    public static void loadRelics(JsonElement jsonElement) {
        JsonObject relicMap = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : relicMap.entrySet()) {
            if (entry.getKey().equals(COUNTERS_JSON_KEY)) {
                RelicStats.mod.onLoadRaw(entry.getValue());
            } else if (entry.getKey().equals(OBTAIN_JSON_KEY)) {
                new RelicObtainStats().onLoadRaw(entry.getValue());
            } else {
                RelicStats.getCustomStats(entry.getKey()).onLoadRaw(entry.getValue());
            }
        }
    }

    @SpirePostfixPatch
    public static void patch(Metrics _instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
        try {
            Method method = Metrics.class.getDeclaredMethod("addData", Object.class, Object.class);
            method.setAccessible(true);
            method.invoke(_instance, RELIC_JSON_KEY, saveRelics());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
