package relicstats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import relicstats.RelicStats;

@SpirePatch(
        clz = EnableEndTurnButtonAction.class,
        method = "update"
)
public class StartTurnPatch {

    @SpirePostfixPatch
    public static void patch(EnableEndTurnButtonAction _instance) {
        RelicStats.turnCount += 1;
    }

}
