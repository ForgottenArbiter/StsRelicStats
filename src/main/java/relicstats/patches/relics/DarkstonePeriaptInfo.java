package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.DarkstonePeriapt;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = DarkstonePeriapt.class,
        method = "onObtainCard"
)
public class DarkstonePeriaptInfo extends StatsInfo {

    private static int hp;
    private static String statId = getLocId(DarkstonePeriapt.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int HP_GAIN = (int) ReflectionHacks.getPrivateStatic(DarkstonePeriapt.class, "HP_AMT");

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], hp);
    }

    @Override
    public void resetStats() {
        hp = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(hp);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            hp = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator=DarkstonePeriaptInfo.Locator.class
    )
    public static void prefix(DarkstonePeriapt _instance) {
        hp += HP_GAIN;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "increaseMaxHp");
            return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }


}
