package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.Formatting;

import static me.alpha432.oyvey.features.commands.Command.sendMessage;


public class AutoRespawn extends Module {
    public static AutoRespawn INSTANCE;
    public Setting<Boolean> deathCoords = this.register(new Setting<>("Coords", true));
    public AutoRespawn(){
        super("AutoRespawn","",Category.MISC,true,false,false);
        INSTANCE = this;
    }
    @Override
    public void onTick() {
            if (mc.currentScreen instanceof DeathScreen) {
                mc.player.requestRespawn();
                mc.setScreen(null);
                if (deathCoords.getValue())
                    sendMessage(Formatting.WHITE + "You died at " + "X:" + (int) mc.player.getX() + " " + "Y:" + (int) mc.player.getY() + " " + "Z:" + (int) mc.player.getZ());
        }

    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }


}




