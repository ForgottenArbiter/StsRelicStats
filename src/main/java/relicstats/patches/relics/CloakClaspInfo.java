package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.CloakClasp;
import com.megacrit.cardcrawl.relics.Orichalcum;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = CloakClasp.class,
        method = "onPlayerEndTurn"
)
public class CloakClaspInfo extends CombatStatsInfo {

    private static CloakClaspInfo INSTANCE = new CloakClaspInfo();
    private static String statId = getLocId(CloakClasp.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private CloakClaspInfo() {
    }

    public static CloakClaspInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePatch(
            clz = CloakClasp.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class CloakClaspFlash {
        // Thanks to kiooeht and Gk for advice/reference code for this one
        public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            CtClass superclass = ctClass.getSuperclass();
            CtMethod supermethod = superclass.getDeclaredMethod("flash");

            CtMethod method = CtNewMethod.delegator(supermethod, ctClass);
            try {
                ctClass.addMethod(method);
            } catch (DuplicateMemberException e) {
                method = ctClass.getDeclaredMethod("flash");
            }
            method.insertAfter("relicstats.patches.relics.CloakClaspInfo.increaseAmount();");
        }

    }

    public static void increaseAmount() {
        // Just to deal with some calls to flash() that can happen while you're obtaining the relic
        if (AbstractDungeon.player.hasRelic(CloakClasp.ID)) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(CloakClasp.ID);
            if (relic.isDone) {
                getInstance().amount += AbstractDungeon.player.hand.group.size();
            }
        }
    }


}
