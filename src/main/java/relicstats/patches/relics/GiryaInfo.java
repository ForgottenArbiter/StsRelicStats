package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.Girya;
import com.megacrit.cardcrawl.vfx.campfire.CampfireLiftEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CampfireLiftEffect.class,
        method = "update"
)
public class GiryaInfo extends StatsInfo {

    private static int counter;
    private static String statId = getLocId(Girya.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], counter);
    }

    @Override
    public void resetStats() {
        counter = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(counter);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            counter = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void insert(CampfireLiftEffect _instance) {
        counter += 1;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

    public boolean showStats() {
        return !CardCrawlGame.isInARun();
    }

}
