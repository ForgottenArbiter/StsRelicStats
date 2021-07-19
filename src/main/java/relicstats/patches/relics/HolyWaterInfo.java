package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.HolyWater;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

public class HolyWaterInfo extends CombatStatsInfo {

    private static HolyWaterInfo INSTANCE = new HolyWaterInfo();
    private static String statId = getLocId(HolyWater.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static MakeTempCardInHandAction handAction;
    private static ArrayList<AbstractCard> miracles = new ArrayList<>();

    private HolyWaterInfo() {}

    public static HolyWaterInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePatch(
            clz = HolyWater.class,
            method = "atBattleStartPreDraw"
    )
    public static class HolyWaterPatch {

        @SpirePostfixPatch
        public static void postfix(HolyWater _instance) {
            ArrayList<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
            handAction = (MakeTempCardInHandAction) actions.get(actions.size() - 1);
            miracles = new ArrayList<>();
        }

    }

    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = "makeNewCard"
    )
    public static class MakeTempCardPatch {

        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard __result, MakeTempCardInHandAction _instance) {
            if (_instance == handAction) {
                miracles.add(__result);
            }
            return __result;
        }

    }

    @SpirePatch(
            clz = Miracle.class,
            method = "use"
    )
    public static class MiraclePatch {

        @SpirePostfixPatch
        public static void postfix(Miracle _instance, AbstractPlayer p, AbstractMonster m) {
            if (miracles.contains(_instance)) {
                if (_instance.upgraded) {
                    getInstance().amount += 2;
                } else {
                    getInstance().amount += 1;
                }
            }
        }

    }

}
