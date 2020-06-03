package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.PeacePipe;
import com.megacrit.cardcrawl.vfx.campfire.CampfireTokeEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CampfireTokeEffect.class,
        method = "update"
)
public class PeacePipeInfo extends StatsInfo {

    private static int tokes;
    private static String statId = getLocId(PeacePipe.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], tokes);
    }

    @Override
    public void resetStats() {
        tokes = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(tokes);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            tokes = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator = PeacePipeInfo.Locator.class
    )
    public static void insert(CampfireTokeEffect _instance) {
        tokes += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(CampfireTokeEffect.class, "isDone");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
