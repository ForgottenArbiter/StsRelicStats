package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.GremlinHorn;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.AmountAdjustmentCallback;
import relicstats.CombatStatsInfo;
import relicstats.RelicStats;
import relicstats.actions.CardDrawFollowupAction;
import relicstats.actions.PreCardDrawAction;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SpirePatch(
        clz = GremlinHorn.class,
        method = "onMonsterDeath"
)
public class GremlinHornInfo extends CombatStatsInfo implements AmountAdjustmentCallback {

    private int cards = 0;
    private int energy = 0;
    public static int methodCount = 0;
    private static GremlinHornInfo INSTANCE;
    private static String statId = getLocId(GremlinHorn.ID);
    private static String[] description = null;

    private int cardsBefore;

    private GremlinHornInfo () {}

    public static GremlinHornInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GremlinHornInfo();
        }
        return INSTANCE;
    }

    public String getBaseDescription() {
        return "";
    }

    @Override
    public String getStatsDescription() {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
        }
        return description[0] +
                cards +
                description[1] +
                energy;
    }

    @Override
    public String getExtendedStatsDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append(getStatsDescription());
        int num_combats = (RelicStats.battleCount - combatObtained);
        if (num_combats < 1) {
            num_combats = 1;
        }
        builder.append(description[2]);
        builder.append(new DecimalFormat("#.###").format((float) (cards) / num_combats));
        builder.append(description[3]);
        builder.append(new DecimalFormat("#.###").format((float) (energy) / num_combats));
        return builder.toString();
    }

    @Override
    public void resetStats() {
        cards = 0;
        energy = 0;
        combatObtained = -1;
        turnObtained = -1;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(cards);
        stats.add(energy);
        stats.add(combatObtained);
        stats.add(turnObtained);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            cards = jsonArray.get(0).getAsInt();
            energy = jsonArray.get(1).getAsInt();
            combatObtained = jsonArray.get(2).getAsInt();
            turnObtained = jsonArray.get(3).getAsInt();
        } else {
            resetStats();
        }
    }

    public static void doBefore() {
        AbstractDungeon.actionManager.addToBottom(new PreCardDrawAction(GremlinHornInfo.getInstance()));
    }

    public static void doAfter() {
        AbstractDungeon.actionManager.addToBottom(new CardDrawFollowupAction(GremlinHornInfo.getInstance()));
    }

    @SpireInstrumentPatch()
    public static ExprEditor patch()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(GremlinHorn.class.getName()) && m.getMethodName().equals("addToBot")) {
                    methodCount += 1;
                    if (methodCount == 2) {
                        m.replace("{relicstats.patches.relics.GremlinHornInfo.doBefore(); $_ = $proceed($$);}");
                    } else if (methodCount == 3) {
                        m.replace("{$_ = $proceed($$); relicstats.patches.relics.GremlinHornInfo.doAfter();}");
                    }
                }
            }
        };
    }

    @Override
    public void registerStartingAmount(int startingAmount) {
        cardsBefore = startingAmount;
    }

    @Override
    public void registerEndingAmount(int endingAmount) {
        cards += endingAmount - cardsBefore;
        energy += 1;
    }
}
