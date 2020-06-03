package relicstats;

import basemod.abstracts.CustomSavableRaw;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RelicObtainStats implements CustomSavableRaw {

    public static HashMap<String, Integer> floorObtained = new HashMap<>();
    public static HashMap<String, Integer> battleObtained = new HashMap<>();
    public static HashMap<String, Integer> turnObtained = new HashMap<>();

    public static void obtainRelic(String relic, int floor, int battle, int turn) {
        if (!hasRelic(relic)) {
            floorObtained.put(relic, floor);
            battleObtained.put(relic, battle);
            turnObtained.put(relic, turn);
        }
    }

    public static boolean hasRelic(String relic) {
        return floorObtained.containsKey(relic);
    }

    public static int getFloor(String relic) {
        return floorObtained.getOrDefault(relic, 0);
    }

    public static int getBattle(String relic) {
        return battleObtained.getOrDefault(relic, 0);
    }

    public static int getTurn(String relic) {
        return turnObtained.getOrDefault(relic, 0);
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<HashMap<String, Integer>> data = new ArrayList<>();
        data.add(floorObtained);
        data.add(battleObtained);
        data.add(turnObtained);
        return gson.toJsonTree(data);
    }

    public static void reset() {
        floorObtained = new HashMap<String, Integer>();
        battleObtained = new HashMap<String, Integer>();
        turnObtained = new HashMap<String, Integer>();
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        reset();
        if (jsonElement != null) {
            JsonArray array = jsonElement.getAsJsonArray();
            JsonObject floorMap = array.get(0).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : floorMap.entrySet()) {
                floorObtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
            JsonObject battleMap = array.get(1).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : battleMap.entrySet()) {
                battleObtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
            JsonObject turnMap = array.get(2).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : turnMap.entrySet()) {
                turnObtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }
    }
}
