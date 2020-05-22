package relicstats.patches;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.DeathScreen;
import relicstats.RelicStats;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractRelic.class,
        method = "renderTip"
)
public class AbstractRelicPatch {

    private static boolean addedTip = false;
    private static ArrayList<PowerTip> originalTips;

    public static boolean shouldShowStats() {
        return CardCrawlGame.mode == CardCrawlGame.GameMode.GAMEPLAY && AbstractDungeon.player != null;
    }

    public static void Prefix(AbstractRelic _instance, SpriteBatch sb) {
        if (RelicStats.hasStats(_instance.relicId) && shouldShowStats()) {
            addedTip = true;
            originalTips = _instance.tips;
            _instance.tips = (ArrayList<PowerTip>)_instance.tips.clone();
            _instance.tips.add(new PowerTip(RelicStats.statsHeader, RelicStats.getStatsDescription(_instance.relicId)));
        }
    }

    public static void Postfix(AbstractRelic _instance, SpriteBatch sb) {
        if (addedTip) {
            _instance.tips = originalTips;
            addedTip = false;
        }

    }
}
