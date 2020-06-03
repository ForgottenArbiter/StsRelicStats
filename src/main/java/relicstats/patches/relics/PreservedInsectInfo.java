package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;
import java.util.Arrays;


@SpirePatch(
        clz = PreservedInsect.class,
        method = "atBattleStart"
)
public class PreservedInsectInfo extends StatsInfo {

    private static int damage;
    private static String statId = getLocId(PreservedInsect.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static int before;

    @Override
    public String getStatsDescription() {
        return String.format("%s%d", description[0], damage);
    }

    @Override
    public void resetStats() {
        damage = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(damage);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            damage = jsonElement.getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator = PreservedInsectInfo.Locator1.class,
            localvars = {"m"}
    )
    public static void beforeHp(PreservedInsect _instance, AbstractMonster m) {
        before = m.currentHealth;
    }

    private static class Locator1 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "currentHealth");
            int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            return Arrays.copyOfRange(matches, 1, 2);
        }
    }


    @SpireInsertPatch(
            locator = PreservedInsectInfo.Locator2.class,
            localvars = {"m"}
    )
    public static void afterHp(PreservedInsect _instance, AbstractMonster m) {
        damage += before - m.currentHealth;
    }

    private static class Locator2 extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "currentHealth");
            int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            matches[1] += 1;
            return Arrays.copyOfRange(matches, 1, 2);
        }
    }

}
