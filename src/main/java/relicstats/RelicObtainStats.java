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

    public static HashMap<String, Integer> floor_obtained = new HashMap<>();
    public static HashMap<String, Integer> battle_obtained = new HashMap<>();
    public static HashMap<String, Integer> turn_obtained = new HashMap<>();

    public static void obtainRelic(String relic, int floor, int battle, int turn) {
        if (!hasRelic(relic)) {
            floor_obtained.put(relic, floor);
            battle_obtained.put(relic, battle);
            turn_obtained.put(relic, turn);
        }
    }

    public static boolean hasRelic(String relic) {
        return floor_obtained.containsKey(relic);
    }

    public static int getFloor(String relic) {
        return floor_obtained.get(relic);
    }

    public static int getBattle(String relic) {
        return battle_obtained.get(relic);
    }

    public static int getTurn(String relic) {
        return turn_obtained.get(relic);
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<HashMap<String, Integer>> data = new ArrayList<>();
        data.add(floor_obtained);
        data.add(battle_obtained);
        data.add(turn_obtained);
        return gson.toJsonTree(data);
    }

    public static void reset() {
        floor_obtained = new HashMap<String, Integer>();
        battle_obtained = new HashMap<String, Integer>();
        turn_obtained = new HashMap<String, Integer>();
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        reset();
        if (jsonElement != null) {
            JsonArray array = jsonElement.getAsJsonArray();
            JsonObject floor_map = array.get(0).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : floor_map.entrySet()) {
                floor_obtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
            JsonObject battle_map = array.get(1).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : battle_map.entrySet()) {
                battle_obtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
            JsonObject turn_map = array.get(2).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : turn_map.entrySet()) {
                turn_obtained.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }
    }
}
