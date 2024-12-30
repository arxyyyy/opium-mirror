package org.nrnr.opium.impl.manager;

import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.impl.module.client.*;
import org.nrnr.opium.impl.module.combat.*;
import org.nrnr.opium.impl.module.combat.bedaura.BedAuraModule;
import org.nrnr.opium.impl.module.exploit.*;
import org.nrnr.opium.impl.module.legit.*;
import org.nrnr.opium.impl.module.misc.*;
import org.nrnr.opium.impl.module.movement.*;
import org.nrnr.opium.impl.module.render.*;
import org.nrnr.opium.impl.module.world.*;

import java.util.*;

/**
 * @author chronos
 * @since 1.0
 */
public class ModuleManager {
    // The client module register. Keeps a list of modules and their ids for
    // easy retrieval by id.
    private final Map<String, Module> modules =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Initializes the module register.
     */
    public ModuleManager() {
        // MAINTAIN ALPHABETICAL ORDER
        register(
                // Client
                new ServerModule(),
                new CapesModule(),
                new ClickGuiModule(),
                new ColorsModule(),
                new CustomFontModule(),
                //new DiscordClientModule(),
                new HUDModule(),
                new IRCModule(),
                new RotationsModule(),
                new SoundsModule(),
                // Combat
                new AutoDoubleHandModule(),
                new AuraModule(),
                new AutoAnchorModule(),
                new AutoArmorModule(),
                new AutoBowReleaseModule(),
                new AutoCrystalModule(),
                new AutoLogModule(),
                new AutoCityModule(),
                new AutoTotemModule(),
                new AutoTrapModule(),
                new AutoWebModule(),
                new AutoXPModule(),
               // new BackTrackModule(),
                new BedAuraModule(),
                new BurrowModule(),
                new BowAimModule(),
                new CevBreakerModule(),
                new ClickCrystalModule(),
                new LegitTotemModule(),
                new PearlMacroModule(),
                new CriticalsModule(),
                new HandBlockModule(),
                new HoleFillModule(),
                new NoHitDelayModule(),
                new ReplenishModule(),
                new QuiverModule(),
                new SelfTrapModule(),
                new SuicideModule(),
                new SurroundModule(),
                new TriggerModule(),
                // Exploit
                new AntiHungerModule(),
                new AutoCraftModule(),
                new AutoCenterModule(),
                new ChorusControlModule(),
               new ClientSpoofModule(),
                new CrasherModule(),
                new DisablerModule(),
                new ElytraSwapModule(),
                new ExtendedFireworkModule(),
                new FakeLatencyModule(),
                new FastLatencyModule(),
                new FastProjectileModule(),
                new HitboxDesyncModule(),
                new PacketCancelerModule(),
                //new PacketFlyModule(),
                new PhaseModule(),
                new PortalGodModeModule(),
                new RaytraceBypassModule(),
                new ReachModule(),
                // Misc
                new AnnouncerModule(),
                new AntiAFKModule(),
                new AntiAimModule(),
                // new AntiBookBanModule(),
                new AntiSpamModule(),
                new AutoAcceptModule(),
                new AutoEatModule(),
                new AutoEZModule(),
                new AutoFishModule(),
                new AutoReconnectModule(),
                new AutoRespawnModule(),
               // new BeaconSelectorModule(),
                new BetterScreenshotModule(),
                new BetterChatModule(),
                new ChatNotifierModule(),
                new ChestSwapModule(),
                // new ChestStealerModule(),
                new FakePlayerModule(),
                new InvCleanerModule(),
                new MiddleClickModule(),
                new NoPacketKickModule(),
                new NoSoundLagModule(),
                new PacketLoggerModule(),
                new TimerModule(),
                new TrueDurabilityModule(),
                new UnfocusedFPSModule(),
                new XCarryModule(),

                // Movement
                new AnchorModule(),
                new AntiLevitationModule(),
                new AutoWalkModule(),
                new BlinkModule(),
                new ElytraFlyModule(),
                new EntityControlModule(),
                new EntitySpeedModule(),
                new FastFallModule(),
                new FlightModule(),
                new IceSpeedModule(),
                new InstantSpeedModule(),
                new JesusModule(),
                new HoleSnapModule(),
                new LongJumpModule(),
                new NoFallModule(),
                new NoJumpDelayModule(),
                new NoSlowModule(),
                new ParkourModule(),
                new SpeedModule(),
                new SprintModule(),
                new StepModule(),
                new TickShiftModule(),
              //  new TridentFlyModule(),
                new VelocityModule(),
                new YawModule(),
                // Render
                new BlockHighlightModule(),
                new BreadcrumbsModule(),
                new BreakHighlightModule(),
                new BurrowEspModule(),
                new ChamsModule(),

                new ESPModule(),
                new ExtraTabModule(),
                new FreecamModule(),
                new FullbrightModule(),
                new HoleESPModule(),
                new CircleModule(),
                new NameProtectModule(),
                new NametagsModule(),
                new NoRenderModule(),
                //new NoRotateModule(),
                new NoLootBlowModule(),
                new NoWeatherModule(),
                new ParticlesModule(),
                new PhaseESPModule(),
                new ShadersModule(),
                new SkeletonModule(),
                new SkyboxModule(),
                new StorageESPModule(),
                new SwingModule(),
                new TooltipsModule(),
                //new TracersModule(),
                new TrueSightModule(),
                new ViewClipModule(),
                new ViewModelModule(),
                 new WaypointsModule(),
                // World
                new AntiInteractModule(),
                new AutoMineModule(),
                new AutoToolModule(),
                new AvoidModule(),
                new BlockInteractModule(),
                new FastDropModule(),
                new FastPlaceModule(),
                new MultitaskModule(),
                new NoGlitchBlocksModule(),
                new ScaffoldModule(),
                new SpeedmineModule()
        );

    }

    /**
     *
     */
    public void postInit() {
        // TODO
    }

    /**
     * @param modules
     * @see #register(Module)
     */
    private void register(Module... modules) {
        for (Module module : modules) {
            register(module);
        }
    }

    /**
     * @param module
     */
    private void register(Module module) {
        modules.put(module.getId(), module);
    }

    /**
     * @param id
     * @return
     */
    public Module getModule(String id) {
        return modules.get(id);
    }

    /**
     * @return
     */
    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }
}
