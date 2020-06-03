package relicstats.patches.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.defect.DarkImpulseAction;
import com.megacrit.cardcrawl.actions.defect.ImpulseAction;
import com.megacrit.cardcrawl.actions.defect.TriggerEndOfTurnOrbsAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.relics.GoldPlatedCables;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import relicstats.CombatStatsInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class GoldPlatedCablesInfo extends CombatStatsInfo {

    private static GoldPlatedCablesInfo INSTANCE = new GoldPlatedCablesInfo();
    private static String statId = getLocId(GoldPlatedCables.ID);
    private static String[] description = CardCrawlGame.languagePack.getUIString(statId).TEXT;

    private static int lightning = 0;
    private static int frost = 0;
    private static int dark = 0;
    private static int plasma = 0;

    private GoldPlatedCablesInfo() {
    }

    public static GoldPlatedCablesInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBaseDescription() {
        return description[0];
    }

    @Override
    public String getStatsDescription() {
        StringBuilder to_return = new StringBuilder(getBaseDescription());
        to_return.append(amount);
        to_return.append(description[1]);
        to_return.append(lightning);
        to_return.append(description[2]);
        to_return.append(frost);
        to_return.append(description[3]);
        to_return.append(dark);
        to_return.append(description[4]);
        to_return.append(plasma);
        return to_return.toString();
    }

    @Override
    public void resetStats() {
        super.resetStats();
        lightning = 0;
        frost = 0;
        dark = 0;
        plasma = 0;
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnOrbs"
    )
    public static class AbstractPlayerCablesPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert(AbstractPlayer _instance) {
            if (AbstractDungeon.player.orbs.isEmpty()) {
                return;
            }
            AbstractOrb orb = _instance.orbs.get(0);
            if (orb instanceof EmptyOrbSlot) {
                return;
            }
            if (orb instanceof Dark) {
                dark += 1;
                getInstance().amount += 1;
            } else if (orb instanceof Plasma) {
                plasma += 1;
                getInstance().amount += 1;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onStartOfTurn");
                int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
                return Arrays.copyOfRange(matches, 1, 2);
            }
        }

    }

    @SpirePatch(
            clz = TriggerEndOfTurnOrbsAction.class,
            method = "update"
    )
    public static class TriggerEndOfTurnOrbsActionCablesPatch {

        @SpireInsertPatch(
                locator = TriggerEndOfTurnOrbsActionCablesPatch.Locator.class
        )
        public static void insert(TriggerEndOfTurnOrbsAction _instance) {
            if (AbstractDungeon.player.orbs.isEmpty()) {
                return;
            }
            AbstractOrb orb = AbstractDungeon.player.orbs.get(0);
            if (orb instanceof EmptyOrbSlot) {
                return;
            }
            if (orb instanceof Frost) {
                frost += 1;
                getInstance().amount += 1;
            } else if (orb instanceof Lightning) {
                lightning += 1;
                getInstance().amount += 1;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onEndOfTurn");
                int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
                return Arrays.copyOfRange(matches, 1, 2);
            }
        }

    }


    @SpirePatch(
            clz = ImpulseAction.class,
            method = "update"
    )
    public static class ImpulseActionCablesPatch {

        @SpireInsertPatch(
                locator = ImpulseActionCablesPatch.Locator.class
        )
        public static void insert(ImpulseAction _instance) {
            if (AbstractDungeon.player.orbs.isEmpty()) {
                return;
            }
            AbstractOrb orb = AbstractDungeon.player.orbs.get(0);
            if (orb instanceof EmptyOrbSlot) {
                return;
            }
            getInstance().amount += 1;
            if (orb instanceof Frost) {
                frost += 1;
            } else if (orb instanceof Lightning) {
                lightning += 1;
            } else if (orb instanceof Dark) {
                dark += 1;
            } else if (orb instanceof Plasma) {
                plasma += 1;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onEndOfTurn");
                int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
                return Arrays.copyOfRange(matches, 1, 2);
            }
        }

    }


    @SpirePatch(
            clz = DarkImpulseAction.class,
            method = "update"
    )
    public static class DarkImpulseActionCablesPatch {

        @SpireInsertPatch(
                locator = DarkImpulseActionCablesPatch.Locator.class
        )
        public static void insert(DarkImpulseAction _instance) {
            if (AbstractDungeon.player.orbs.isEmpty()) {
                return;
            }
            AbstractOrb orb = AbstractDungeon.player.orbs.get(0);
            if (orb instanceof Dark) {
                dark += 1;
                getInstance().amount += 1;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onEndOfTurn");
                int[] matches = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
                return Arrays.copyOfRange(matches, 1, 2);
            }
        }

    }
}
