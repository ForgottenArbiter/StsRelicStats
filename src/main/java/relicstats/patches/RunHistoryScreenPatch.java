package relicstats.patches;

import com.badlogic.gdx.files.FileHandle;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.stats.RunData;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsSaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RunHistoryScreenPatch {

    public static HashMap<RunData, JsonElement> relicData = new HashMap<>();
    public static boolean runHistoryHasStats = false;

    @SpirePatch(
            clz = RunHistoryScreen.class,
            method = "refreshData"
    )
    public static class RefreshDataPatch {

        @SpirePrefixPatch
        public static void before(RunHistoryScreen _instance) {
            relicData = new HashMap<>();
        }

        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"file", "data"}
        )
        public static void insert(RunHistoryScreen _instance, FileHandle file, RunData data) {
            JsonObject reparsedData = new Gson().fromJson(file.readString(), JsonObject.class);
            JsonElement relicStats = null;
            for (Map.Entry<String, JsonElement> entry : reparsedData.entrySet()) {
                if (entry.getKey().equals(StatsSaver.RELIC_JSON_KEY)) {
                    relicStats = entry.getValue();
                }
            }
            relicData.put(data, relicStats);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            }
        }

    }

    @SpirePatch(
            clz = RunHistoryScreen.class,
            method = "reloadWithRunData"
    )
    public static class ReloadWithRunDataPatch {

        @SpirePrefixPatch
        public static void patch(RunHistoryScreen _instance, RunData runData) {
            runHistoryHasStats = false;
            if (relicData.containsKey(runData)) {
                JsonElement relicStats = relicData.get(runData);
                if (relicStats != null) {
                    try {
                        StatsSaver.loadRelics(relicStats);
                        runHistoryHasStats = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
