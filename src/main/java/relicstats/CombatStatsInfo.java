package relicstats;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class CombatStatsInfo extends StatsInfo {

    protected int amount;

    protected static String[] description;

    public abstract String getBaseDescription();

    public String getStatsDescription() {
        return getBaseDescription() + amount;
    }

    public String getExtendedStatsDescription(int totalCombats, int totalTurns) {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(getLocId("EXTENDED")).TEXT;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getStatsDescription());
        int num_turns = totalTurns;
        if (num_turns < 1) {
            num_turns = 1;
        }
        int num_combats = totalCombats;
        if (num_combats < 1) {
            num_combats = 1;
        }
        builder.append(description[0]);
        builder.append(new DecimalFormat("#.###").format((float) (amount) / num_turns));
        builder.append(description[1]);
        builder.append(new DecimalFormat("#.###").format((float) (amount) / num_combats));
        return builder.toString();
    }

    public void resetStats() {
        amount = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(amount);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            amount = jsonArray.get(0).getAsInt();
        } else {
            resetStats();
        }
    }

}
