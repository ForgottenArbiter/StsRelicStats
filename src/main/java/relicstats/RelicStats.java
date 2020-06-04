package relicstats;

import basemod.*;
import basemod.abstracts.CustomSavableRaw;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import relicstats.patches.relics.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

@SpireInitializer
public class RelicStats implements RelicGetSubscriber, StartGameSubscriber, PostUpdateSubscriber, PostInitializeSubscriber, PreStartGameSubscriber, EditStringsSubscriber, OnStartBattleSubscriber, CustomSavableRaw {

    private static final Logger logger = LogManager.getLogger(RelicStats.class.getName());
    private static HashMap<String, HasCustomStats> statsInfoHashMap = new HashMap<>();
    private static String floorObtainedString;
    public static String statsHeader;
    public static int turnCount;
    public static int battleCount;

    private static String EXTENDED_STATS_OPTION = "extendedStats";
    private static String TWITCH_OPTION = "slayTheRelics";
    private static String FLOOR_STATS_OPTION = "floorStats";
    private static SpireConfig statsConfig;

    public static RelicStats mod;

    public RelicStats(){
        BaseMod.subscribe(this);

        try {
            Properties defaults = new Properties();
            defaults.put(EXTENDED_STATS_OPTION, Boolean.toString(true));
            defaults.put(TWITCH_OPTION, Boolean.toString(false));
            defaults.put(FLOOR_STATS_OPTION, Boolean.toString(false));
            statsConfig = new SpireConfig("Relic Stats", "config", defaults);
            statsConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void initialize() {
        mod = new RelicStats();
    }

    public static void registerCustomStats(String relicId, HasCustomStats customStats) {
        statsInfoHashMap.put(relicId, customStats);
        String saveKey = String.format("stats_%s", relicId);
        BaseMod.addSaveField(saveKey, statsInfoHashMap.get(relicId));
    }

    public static void unregisterCustomStats(String relicId) {
        statsInfoHashMap.remove(relicId);
    }

    public void receivePostInitialize() {
        statsHeader = CardCrawlGame.languagePack.getUIString("STATS:HEADER").TEXT[0];
        floorObtainedString = CardCrawlGame.languagePack.getUIString("STATS:EXTENDED").TEXT[2];
        registerCustomStats(SneckoEye.ID, new SneckoInfo());
        registerCustomStats(CeramicFish.ID, new CeramicFishInfo());
        registerCustomStats(MawBank.ID, new MawBankInfo());
        registerCustomStats(ToyOrnithopter.ID, new ToyOrnithopterInfo());
        registerCustomStats(BurningBlood.ID, new BurningBlackBloodInfo());
        registerCustomStats(BlackBlood.ID, new BurningBlackBloodInfo());
        registerCustomStats(MealTicket.ID, new MealTicketInfo());
        registerCustomStats(SsserpentHead.ID, new SsserpentHeadInfo());
        registerCustomStats(GoldenIdol.ID, new GoldenIdolInfo());
        registerCustomStats(BloodyIdol.ID, new BloodyIdolInfo());
        registerCustomStats(FaceOfCleric.ID, new FaceOfClericInfo());
        registerCustomStats(BlackStar.ID, new BlackStarInfo());
        registerCustomStats(BloodVial.ID, new BloodVialInfo());
        registerCustomStats(JuzuBracelet.ID, new JuzuBraceletInfo());
        registerCustomStats(Ectoplasm.ID, new EctoplasmInfo());
        registerCustomStats(Sozu.ID, new SozuInfo());
        registerCustomStats(Boot.ID, BootInfo.getInstance());
        registerCustomStats(PreservedInsect.ID, new PreservedInsectInfo());
        registerCustomStats(MeatOnTheBone.ID, new MeatOnTheBoneInfo());
        registerCustomStats(Pantograph.ID, new PantographInfo());
        registerCustomStats(Torii.ID, new ToriiInfo());
        registerCustomStats(TungstenRod.ID, new TungstenRodInfo());
        registerCustomStats(MercuryHourglass.ID, MercuryHourglassInfo.getInstance());
        registerCustomStats(StoneCalendar.ID, StoneCalendarInfo.getInstance());
        registerCustomStats(DarkstonePeriapt.ID, new DarkstonePeriaptInfo());
        registerCustomStats(SingingBowl.ID, new SingingBowlInfo());
        registerCustomStats(Shuriken.ID, ShurikenInfo.getInstance());
        registerCustomStats(Kunai.ID, KunaiInfo.getInstance());
        registerCustomStats(OrnamentalFan.ID, OrnamentalFanInfo.getInstance());
        registerCustomStats(MarkOfTheBloom.ID, new MarkOfTheBloomInfo());
        registerCustomStats(ArtOfWar.ID, ArtOfWarInfo.getInstance());
        registerCustomStats(Nunchaku.ID, NunchakuInfo.getInstance());
        registerCustomStats(SneckoSkull.ID, SneckoSkullInfo.getInstance());
        registerCustomStats(LetterOpener.ID, LetterOpenerInfo.getInstance());
        registerCustomStats(Sundial.ID, SundialInfo.getInstance());
        registerCustomStats(CharonsAshes.ID, CharonsAshesInfo.getInstance());
        registerCustomStats(CloakClasp.ID, CloakClaspInfo.getInstance());
        registerCustomStats(Pocketwatch.ID, PocketwatchInfo.getInstance());
        registerCustomStats(Tingsha.ID, TingshaInfo.getInstance());
        registerCustomStats(ToughBandages.ID, ToughBandagesInfo.getInstance());
        registerCustomStats(VioletLotus.ID, VioletLotusInfo.getInstance());
        registerCustomStats(Abacus.ID, AbacusInfo.getInstance());
        registerCustomStats(DeadBranch.ID, DeadBranchInfo.getInstance());
        registerCustomStats(UnceasingTop.ID, UnceasingTopInfo.getInstance());
        registerCustomStats(FrozenCore.ID, FrozenCoreInfo.getInstance());
        registerCustomStats(HoveringKite.ID, HoveringKiteInfo.getInstance());
        registerCustomStats(RunicCube.ID, RunicCubeInfo.getInstance());
        registerCustomStats(StrangeSpoon.ID, StrangeSpoonInfo.getInstance());
        registerCustomStats(NlothsGift.ID, new NlothsGiftInfo());
        registerCustomStats(SelfFormingClay.ID, SelfFormingClayInfo.getInstance());
        registerCustomStats(Necronomicon.ID, NecronomiconInfo.getInstance());
        registerCustomStats(MedicalKit.ID, MedicalKitInfo.getInstance());
        registerCustomStats(BlueCandle.ID, BlueCandleInfo.getInstance());
        registerCustomStats(Orichalcum.ID, OrichalcumInfo.getInstance());
        registerCustomStats(PenNib.ID, PenNibInfo.getInstance());
        registerCustomStats(SmilingMask.ID, new SmilingMaskInfo());
        registerCustomStats(GremlinHorn.ID, GremlinHornInfo.getInstance());
        registerCustomStats(InkBottle.ID, InkBottleInfo.getInstance());
        registerCustomStats(BirdFacedUrn.ID, BirdFacedUrnInfo.getInstance());
        registerCustomStats(ChampionsBelt.ID, ChampionsBeltInfo.getInstance());
        registerCustomStats(Ginger.ID, GingerInfo.getInstance());
        registerCustomStats(Turnip.ID, TurnipInfo.getInstance());
        registerCustomStats(PrayerWheel.ID, new PrayerWheelInfo());
        registerCustomStats(Inserter.ID, InserterInfo.getInstance());
        registerCustomStats(HandDrill.ID, HandDrillInfo.getInstance());
        registerCustomStats(OrangePellets.ID, OrangePelletsInfo.getInstance());
        registerCustomStats(EternalFeather.ID, new EternalFeatherInfo());
        registerCustomStats(WarpedTongs.ID, WarpedTongsInfo.getInstance());
        registerCustomStats(Shovel.ID, new ShovelInfo());
        registerCustomStats(CentennialPuzzle.ID, CentennialPuzzleInfo.getInstance());
        registerCustomStats(HappyFlower.ID, HappyFlowerInfo.getInstance());
        registerCustomStats(DreamCatcher.ID, new DreamCatcherInfo());
        registerCustomStats(PeacePipe.ID, new PeacePipeInfo());
        registerCustomStats(LizardTail.ID, new LizardTailInfo());
        registerCustomStats(QuestionCard.ID, new QuestionCardInfo());
        registerCustomStats(TinyChest.ID, new TinyChestInfo());
        registerCustomStats(BustedCrown.ID, new BustedCrownInfo());
        registerCustomStats(CursedKey.ID, new CursedKeyInfo());
        registerCustomStats(ChemicalX.ID, ChemicalXInfo.getInstance());
        registerCustomStats(Duality.ID, DualityInfo.getInstance());
        registerCustomStats(GoldPlatedCables.ID, GoldPlatedCablesInfo.getInstance());
        registerCustomStats(EmotionChip.ID, EmotionChipInfo.getInstance());
        registerCustomStats(Melange.ID, MelangeInfo.getInstance());
        registerCustomStats(GoldenEye.ID, GoldenEyeInfo.getInstance());
        registerCustomStats(RegalPillow.ID, new RegalPillowInfo());
        registerCustomStats(Waffle.ID, new LeesWaffleInfo());
        registerCustomStats(MummifiedHand.ID, MummifiedHandInfo.getInstance());

        System.out.println("Custom stat relics: ");
        System.out.println(Arrays.toString(statsInfoHashMap.keySet().toArray()));

        BaseMod.addSaveField("stats_master_turn_counts", this);
        BaseMod.addSaveField("relic_floor_stats", new RelicObtainStats());

        setUpOptions();
    }

    public static HasCustomStats getCustomStats(String relicId) {
        return statsInfoHashMap.get(relicId);
    }

    public static boolean getExtendedStatsOption() {
        if (statsConfig == null) {
            return false;
        }
        return statsConfig.getBool(EXTENDED_STATS_OPTION);
    }

    public static boolean getTwitchIntegrationOption() {
        if (statsConfig == null) {
            return false;
        }
        return statsConfig.getBool(TWITCH_OPTION);
    }

    public static boolean getFloorStatsOtion() {
        if (statsConfig == null) {
            return false;
        }
        return statsConfig.getBool(FLOOR_STATS_OPTION);
    }

    public void receiveRelicGet(AbstractRelic relic) {
        RelicObtainStats.obtainRelic(relic.relicId, AbstractDungeon.floorNum, battleCount, turnCount);
    }

    private void setUpOptions() {
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton extendedStatsButton = new ModLabeledToggleButton(
                CardCrawlGame.languagePack.getUIString("STATS:OPTION").TEXT[0],
                350, 725, Settings.CREAM_COLOR, FontHelper.charDescFont,
                getExtendedStatsOption(), settingsPanel, modLabel -> {},
                modToggleButton -> {
                    if (statsConfig != null) {
                        statsConfig.setBool(EXTENDED_STATS_OPTION, modToggleButton.enabled);
                        try {
                            statsConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(extendedStatsButton);
        ModLabeledToggleButton slayTheRelicsButton = new ModLabeledToggleButton(
                CardCrawlGame.languagePack.getUIString("STATS:OPTION").TEXT[1],
                350, 665, Settings.CREAM_COLOR, FontHelper.charDescFont,
                getTwitchIntegrationOption(), settingsPanel, modLabel -> {},
                modToggleButton -> {
                    if (statsConfig != null) {
                        statsConfig.setBool(TWITCH_OPTION, modToggleButton.enabled);
                        try {
                            statsConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(slayTheRelicsButton);
        ModLabeledToggleButton floorStatsButton = new ModLabeledToggleButton(
                CardCrawlGame.languagePack.getUIString("STATS:OPTION").TEXT[2],
                350, 605, Settings.CREAM_COLOR, FontHelper.charDescFont,
                getFloorStatsOtion(), settingsPanel, modLabel -> {},
                modToggleButton -> {
                    if (statsConfig != null) {
                        statsConfig.setBool(FLOOR_STATS_OPTION, modToggleButton.enabled);
                        try {
                            statsConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(floorStatsButton);
        BaseMod.registerModBadge(ImageMaster.loadImage("Icon.png"),"Relic Stats", "Forgotten Arbiter", null, settingsPanel);
    }

    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, "localization/eng/descriptions.json");
    }

    public void receivePreStartGame() {
        for(HasCustomStats statsInfo : statsInfoHashMap.values()) {
            statsInfo.resetStats();
        }
        RelicObtainStats.reset();
    }

    public void receiveStartGame() {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            RelicObtainStats.obtainRelic(relic.relicId, 0, 0, 0);
        }
    }

    public static boolean hasStats(String relicId) {
        return statsInfoHashMap.containsKey(relicId);
    }

    public static boolean hasStatsMessage(String relicId) {
        if (getFloorStatsOtion() && RelicObtainStats.hasRelic(relicId)) {
            return true;
        } else {
            return hasStats(relicId);
        }
    }

    public static String getFloorObtainedDescription(String relicId) {
        if (RelicObtainStats.hasRelic(relicId)) {
            String message = floorObtainedString + RelicObtainStats.getFloor(relicId);
            if (hasStats(relicId)) {
                message += " NL ";
            }
            return message;
        } else {
            return "";
        }
    }

    public static String getStatsDescription(String relicId) {
        String prefix = "";
        if (getFloorStatsOtion()) {
            prefix = getFloorObtainedDescription(relicId);
        }
        if (!hasStats(relicId)) {
            return prefix;
        }
        if (getExtendedStatsOption()) {
            int totalCombats = battleCount - RelicObtainStats.getBattle(relicId);
            int totalTurns = turnCount - RelicObtainStats.getTurn(relicId);
            return prefix + statsInfoHashMap.get(relicId).getExtendedStatsDescription(totalCombats, totalTurns);
        } else {
            return prefix + statsInfoHashMap.get(relicId).getStatsDescription();
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        battleCount += 1;
        SelfFormingClayInfo.getInstance().onCombatStart();
    }


    @Override
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(battleCount);
        stats.add(turnCount);
        return gson.toJsonTree(stats);
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            battleCount = jsonArray.get(0).getAsInt();
            turnCount = jsonArray.get(1).getAsInt();
        } else {
            battleCount = 0;
            turnCount = 0;
        }

    }

    @SuppressWarnings("unchecked")
    public void receivePostUpdate() {
        SlayTheRelicsIntegration.clear();
        if (CardCrawlGame.isInARun() && getTwitchIntegrationOption()) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (statsInfoHashMap.containsKey(relic.relicId)) {
                    Hitbox hb = relic.hb;
                    PowerTip tip = new PowerTip(statsHeader, getStatsDescription(relic.relicId));
                    ArrayList<PowerTip> tips = (ArrayList<PowerTip>)relic.tips.clone();
                    tips.add(tip);
                    SlayTheRelicsIntegration.renderTipHitbox(hb, tips);
                }
            }
        }
    }

}
