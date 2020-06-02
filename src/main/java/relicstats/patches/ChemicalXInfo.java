package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ChemicalX;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import relicstats.CombatStatsInfo;


public class ChemicalXInfo extends CombatStatsInfo {

    private static String statId = getLocId(ChemicalX.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;
    private static ChemicalXInfo INSTANCE = new ChemicalXInfo();

    private ChemicalXInfo() {
    }

    public static ChemicalXInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @SpirePatch(
            clz = ChemicalX.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class ChemicalXFlash {
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
            method.insertAfter("relicstats.patches.ChemicalXInfo.increaseAmount();");
        }

    }

    public static void increaseAmount() {
        // Just to deal with some calls to flash() that can happen while you're obtaining the relic
        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            AbstractRelic relic = AbstractDungeon.player.getRelic(ChemicalX.ID);
            if (relic.isDone) {
                getInstance().amount += 1;
            }
        }
    }


}
