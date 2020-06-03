package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MercuryHourglass;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

@SpirePatch(
        clz = MercuryHourglass.class,
        method = "atTurnStart"
)
public class MercuryHourglassInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(MercuryHourglass.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static PreAoeDamageAction preAction;
    private static MercuryHourglassInfo INSTANCE = new MercuryHourglassInfo();

    private MercuryHourglassInfo() {}

    public static MercuryHourglassInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void prefix(MercuryHourglass _instance) {
        preAction = new PreAoeDamageAction();
        AbstractDungeon.actionManager.addToBottom(preAction);
    }

    @SpirePostfixPatch
    public static void postfix(MercuryHourglass _instance) {
        MercuryHourglassInfo info = getInstance();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(info, MercuryHourglassInfo.preAction);
        AbstractDungeon.actionManager.addToBottom(postAction);
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += amount;
    }
}
