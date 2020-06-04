package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = CampfireSleepEffect.class,
        method = SpirePatch.CONSTRUCTOR
)
public class RegalPillowInfo extends StatsInfo {

    private static int healing;
    private static String statId = getLocId(RegalPillow.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], healing);
    }

    @Override
    public void resetStats() {
        healing = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(healing);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            healing = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator = RegalPillowInfo.Locator.class
    )
    public static void insert(CampfireSleepEffect _instance) {
        int healAmount = (int)ReflectionHacks.getPrivate(_instance, CampfireSleepEffect.class, "healAmount");
        AbstractPlayer player = AbstractDungeon.player;
        int hpAfterBaseHeal = Math.min(player.currentHealth + healAmount, player.maxHealth);
        int remainingHpToHeal = player.maxHealth - hpAfterBaseHeal;
        int healedByRegalPillow = Math.min(remainingHpToHeal, 15);
        healing += healedByRegalPillow;
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(CampfireSleepEffect.class, "healAmount");
            int[] results = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(results, 2, 3);
        }
    }

}
