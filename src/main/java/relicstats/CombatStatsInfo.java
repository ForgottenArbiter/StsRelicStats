package relicstats;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;

public abstract class CombatStatsInfo extends StatsInfo {

    protected int amount;
    protected int combatObtained;
    protected int turnObtained;

    protected static String[] description;

    public abstract String getBaseDescription();

    public String getStatsDescription() {
        return getBaseDescription() + amount;
    }

    public String getExtendedStatsDescription() {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(getLocId("EXTENDED")).TEXT;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getStatsDescription());
        int num_turns = (RelicStats.turnCount - turnObtained);
        if (num_turns < 1) {
            num_turns = 1;
        }
        int num_combats = (RelicStats.battleCount - combatObtained);
        if (num_combats < 1) {
            num_combats = 1;
        }
        builder.append(description[0]);
        builder.append((float) (amount) / num_turns);
        builder.append(description[1]);
        builder.append((float) (amount) / num_combats);
        return builder.toString();
    }

    public void resetStats() {
        amount = 0;
        combatObtained = -1;
        turnObtained = -1;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(amount);
        stats.add(combatObtained);
        stats.add(turnObtained);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            amount = jsonArray.get(0).getAsInt();
            combatObtained = jsonArray.get(1).getAsInt();
            turnObtained = jsonArray.get(2).getAsInt();
        } else {
            resetStats();
        }
    }

    @Override
    public void onCombatStartForStats() {
        if (combatObtained == -1) {
            combatObtained = RelicStats.battleCount;
            turnObtained = RelicStats.turnCount;
        }
    }

}
