package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CharonsAshes;
import relicstats.AmountIncreaseCallback;
import relicstats.CombatStatsInfo;
import relicstats.actions.AoeDamageFollowupAction;
import relicstats.actions.PreAoeDamageAction;

@SpirePatch(
        clz = CharonsAshes.class,
        method = "onExhaust"
)
public class CharonsAshesInfo extends CombatStatsInfo implements AmountIncreaseCallback {

    private static String statId = getLocId(CharonsAshes.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static PreAoeDamageAction preAction;
    private static CharonsAshesInfo INSTANCE = new CharonsAshesInfo();

    private CharonsAshesInfo() {}

    public static CharonsAshesInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePrefixPatch
    public static void prefix(CharonsAshes _instance, AbstractCard c) {
        preAction = new PreAoeDamageAction();
        AbstractDungeon.actionManager.addToBottom(preAction);
    }

    @SpirePostfixPatch
    public static void postfix(CharonsAshes _instance, AbstractCard c) {
        CharonsAshesInfo info = getInstance();
        AoeDamageFollowupAction postAction = new AoeDamageFollowupAction(info, CharonsAshesInfo.preAction);
        AbstractDungeon.actionManager.addToBottom(postAction);
    }

    @Override
    public void increaseAmount(int amount) {
        this.amount += amount;
    }

}
