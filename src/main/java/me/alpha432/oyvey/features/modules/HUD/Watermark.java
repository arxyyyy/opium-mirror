package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Watermark extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Setting<Boolean> showWatermark = this.register(new Setting<>("Watermark", true));
    public Setting<watermarkMode> mode = this.register(new Setting("Mode", watermarkMode.Opiumh4ck));
    public Setting<Boolean> version = this.register(new Setting<>("Version", true));
    public Setting<Boolean> showUID = this.register(new Setting<>("UID", true));

    public Watermark() {
        super("Watermark", "", Category.HUD, true, false, false);
    }

    public enum watermarkMode {
        Opiumh4ck, Opium, wurstplusfour, Kamiblue, JakePaulHax, KingPvPClient, Nodus
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String playerName = mc.player.getName().getString();
        if (mc.player == null || !showWatermark.getValue()) return;
        String uid = "0";

        if (playerName.equals("Op1umClientLLC")) {
            uid = "0";
        }
        if (playerName.equals("heedii")) {
            uid = "1";
        }
        if (playerName.equals("FinalMemory")) {
            uid = "2";
        }
        if (playerName.equals("crystal0p1umm")) {
            uid = "1";
        }
        if (playerName.equals("ItsBookxYT")) {
            uid = "3";
        }

        TextRenderer textRenderer = mc.textRenderer;
        int watermarkX = 2;
        int watermarkY = 2;
        int textHeight = 10;
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        String watermarkText = version.getValue() ? mode.getValue() + " beta (" + OyVey.VERSION +")" : mode.getValue().toString();
        event.getContext().drawTextWithShadow(textRenderer, watermarkText, watermarkX, watermarkY, argbColor);

        if (showUID.getValue()) {

            String uidText = "UID "+uid;
            int uidY = watermarkY + textHeight;
            event.getContext().drawTextWithShadow(textRenderer, uidText, watermarkX, uidY, argbColor);
        }
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
