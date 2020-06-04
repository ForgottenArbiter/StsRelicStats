package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.defect.ImpulseAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.relics.EmotionChip;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = ImpulseAction.class,
        method = "update"
)
public class EmotionChipInfo extends CombatStatsInfo {

    private static EmotionChipInfo INSTANCE = new EmotionChipInfo();
    private static String statId = getLocId(EmotionChip.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private static int lightning = 0;
    private static int frost = 0;
    private static int dark = 0;
    private static int plasma = 0;

    private EmotionChipInfo() {
    }

    public static EmotionChipInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @Override
    public String getStatsDescription() {
        StringBuilder to_return = new StringBuilder(getBaseDescription());
        to_return.append(amount);
        to_return.append(description[1]);
        to_return.append(lightning);
        to_return.append(description[2]);
        to_return.append(frost);
        to_return.append(description[3]);
        to_return.append(dark);
        to_return.append(description[4]);
        to_return.append(plasma);
        return to_return.toString();
    }

    @Override
    public void resetStats() {
        super.resetStats();
        lightning = 0;
        frost = 0;
        dark = 0;
        plasma = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(amount);
        stats.add(lightning);
        stats.add(frost);
        stats.add(dark);
        stats.add(plasma);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            amount = jsonArray.get(0).getAsInt();
            lightning = jsonArray.get(1).getAsInt();
            frost = jsonArray.get(2).getAsInt();
            dark = jsonArray.get(3).getAsInt();
            plasma = jsonArray.get(4).getAsInt();
        } else {
            resetStats();
        }
    }

    @SpireInsertPatch(
            locator = EmotionChipInfo.Locator.class,
            localvars = {"o"}
    )
    public static void insert(ImpulseAction _instance, AbstractOrb o) {
        if (o instanceof EmptyOrbSlot) {
            return;
        }
        if (o instanceof Lightning) {
            lightning += 1;
            getInstance().amount += 1;
        } else if (o instanceof Frost) {
            frost += 1;
            getInstance().amount += 1;
        } else if (o instanceof Dark) {
            dark += 1;
            getInstance().amount += 1;
        } else if (o instanceof Plasma) {
            plasma += 1;
            getInstance().amount += 1;
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onStartOfTurn");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
