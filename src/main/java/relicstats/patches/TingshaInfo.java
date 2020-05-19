package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Tingsha;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

@SpirePatch(
        clz = Tingsha.class,
        method = "onManualDiscard"
)
public class TingshaInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(Tingsha.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static PreAoeDamageAction preAction;
    private static TingshaInfo INSTANCE = new TingshaInfo();

    private TingshaInfo() {}

    public static TingshaInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void prefix(Tingsha _instance) {
        preAction = new PreAoeDamageAction();
        AbstractDungeon.actionManager.addToBottom(preAction);
    }

    @SpirePostfixPatch
    public static void postfix(Tingsha _instance) {
        TingshaInfo info = getInstance();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(info, TingshaInfo.preAction);
        AbstractDungeon.actionManager.addToBottom(postAction);
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += amount;
    }

}
