package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TwistedFunnel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = TwistedFunnel.class,
        method = "atBattleStart"
)
public class TwistedFunnelInfo extends StatsInfo {

    private static int poisonApplied;
    private static int artifactRemoved;
    private static String statId = getLocId(TwistedFunnel.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public void resetStats() {
        poisonApplied = 0;
        artifactRemoved = 0;
    }

    @Override
    public String getStatsDescription() {
        return String.format("%s%d%s%d", description[0], poisonApplied, description[1], artifactRemoved);
    }

    @Override
    public JsonElement onSaveRaw() {
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(poisonApplied);
        stats.add(artifactRemoved);
        Gson gson = new Gson();
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            poisonApplied = jsonArray.get(0).getAsInt();
            artifactRemoved = jsonArray.get(1).getAsInt();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void prefix(TwistedFunnel _instance) {
        // check, how many artifact removing relics activate before this one
        int artifactAlreadyRemoved = 0;
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r.relicId.equals("Bag of Marbles") || r.relicId.equals("Red Mask")) {
                artifactAlreadyRemoved += 1;
            }
            if (r.relicId.equals("TwistedFunnel")) {
                break;
            }
        }
        for (final AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo.hasPower("Artifact") && mo.getPower("Artifact").amount > artifactAlreadyRemoved) {
                artifactRemoved += 1;
            }
            else {
                poisonApplied += 4;
            }
        }
    }
}
