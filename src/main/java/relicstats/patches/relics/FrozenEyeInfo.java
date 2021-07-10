package relicstats.patches.relics;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.screens.DrawPileViewScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.StatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = DrawPileViewScreen.class,
        method = "update"
)
public class FrozenEyeInfo extends StatsInfo {

    private static float time;
    private static String statId = getLocId(FrozenEye.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    @Override
    public String getStatsDescription() {
        return String.format("%s%s", description[0], CharStat.formatHMSM(time));
    }

    @Override
    public void resetStats() {
        time = 0;
    }

    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(time);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            time = jsonElement.getAsFloat();
        } else {
            resetStats();
        }
    }

    @SpirePrefixPatch
    public static void patch(DrawPileViewScreen _instance) {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(FrozenEye.ID)) {
            time += Gdx.graphics.getDeltaTime();
        }
    }

}

