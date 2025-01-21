package we.devs.opium.client.modules.miscellaneous;

import com.google.common.eventbus.Subscribe;
import we.devs.opium.api.events.DeathEvent;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;

import java.util.Random;

/**
 * @author cpv
 */
@RegisterModule(name = "AutoEZ", description = "LEL", category = Module.Category.MISCELLANEOUS)
public class ModuleAutoEZ extends Module {
    private final String[] EZ = new String[] {
            "<player> dumped on by OpiumHack lel pooron!",
            "<player> DESTROYED by OpiumHack LOL!!!",
            "EZZZ <player> pooron owned by OpiumHack!",
            "OpiumHack owns you <player>!",
            "EZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ <player> -OpiumHack",
            "LOL <player> DUMB DOG DIES TO OPIUMHACK LOL!! EZ!!!"
    };

    @Subscribe
    public void onDeath(DeathEvent event) {
        if (mc.player == null) {
            return;
        }
        Random random = new Random();
        String msg = EZ[random.nextInt(EZ.length - 1)];
        mc.player.networkHandler.sendChatMessage(msg.replace("<opp>", event.getEntity().getName().getString()));
    }
}
