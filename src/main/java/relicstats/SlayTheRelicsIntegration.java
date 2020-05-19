package relicstats;


import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;

import java.util.ArrayList;

@SpireInitializer
public class SlayTheRelicsIntegration {

    // =============== API for SlayTheRelics for displaying tooltips on Twitch =================
    //
    //
    // These two properties are read by another mod that sends their contents over to a Twitch extension called
    // SlayTheRelics and they are displayed alongside other tooltips on stream
    public static ArrayList<Hitbox> slayTheRelicsHitboxes = new ArrayList<>();
    public static ArrayList<ArrayList<PowerTip>> slayTheRelicsPowerTips = new ArrayList<>();

    public static void initialize() {

    }

    public static void clear() {
        slayTheRelicsHitboxes.clear();
        slayTheRelicsPowerTips.clear();
    }

    public static void renderTipHitbox(Hitbox hb, ArrayList<PowerTip> tips) {
        slayTheRelicsHitboxes.add(hb);
        slayTheRelicsPowerTips.add(tips);
    }
}