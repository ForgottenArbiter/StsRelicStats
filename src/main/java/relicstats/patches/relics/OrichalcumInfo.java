package relicstats.patches.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.relics.InkBottle;
import com.megacrit.cardcrawl.relics.Orichalcum;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;

@SpirePatch(
        clz = Orichalcum.class,
        method = "onPlayerEndTurn"
)
public class OrichalcumInfo extends CombatStatsInfo {

    private static OrichalcumInfo INSTANCE;
    private static String statId = getLocId(Orichalcum.ID);
    private static String[] description = null;
    private static int BLOCK = 0;

    private OrichalcumInfo() {
    }

    public static OrichalcumInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrichalcumInfo();
            BLOCK = (int) ReflectionHacks.getPrivateStatic(Orichalcum.class, "BLOCK_AMT");
        }
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        if (description == null) {
            description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
        }
        return description[0];
    }

    @SpirePatch(
            clz = Orichalcum.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class OrichalcumFlash {
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
            method.insertAfter("relicstats.patches.relics.OrichalcumInfo.increaseAmount();");
        }

    }

    public static void increaseAmount() {
        // Just to deal with some calls to flash() that can happen while you're obtaining the relic
        if (AbstractDungeon.player.hasRelic(Orichalcum.ID)) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(Orichalcum.ID);
            if (relic.isDone) {
                getInstance().amount += BLOCK;
            }
        }
    }
}
