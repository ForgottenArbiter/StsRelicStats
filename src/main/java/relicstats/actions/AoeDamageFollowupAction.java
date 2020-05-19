package relicstats.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import relicstats.AmountIncreaseCallback;

import java.util.ArrayList;

public class AoeDamageFollowupAction extends AbstractGameAction {

    private AmountIncreaseCallback statTracker;
    private PreAoeDamageAction preAction;

    public AoeDamageFollowupAction(AmountIncreaseCallback statTracker, PreAoeDamageAction preAction) {
        this.statTracker = statTracker;
        this.preAction = preAction;
        this.actionType = ActionType.DAMAGE;  // So it's not cleared if the damage kills
    }

    @Override
    public void update() {
        ArrayList<AbstractMonster> affectedMonsters = preAction.getAffectedMonsters();
        for (AbstractMonster m : affectedMonsters) {
            statTracker.increaseAmount(m.lastDamageTaken);
        }
        isDone = true;
    }

}
