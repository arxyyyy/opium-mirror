package dev.opium.core.impl;

import dev.opium.Opium;
import dev.opium.api.utils.world.BlockUtil;
import dev.opium.api.events.eventbus.EventHandler;
import dev.opium.api.events.eventbus.EventPriority;
import dev.opium.api.events.impl.TickEvent;
import dev.opium.mod.modules.impl.render.PlaceRender;

public class ThreadManager {
    public static ClientService clientService;

    public ThreadManager() {
        Opium.EVENT_BUS.subscribe(this);
        clientService = new ClientService();
        clientService.setName("OpiumClientService");
        clientService.setDaemon(true);
        clientService.start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(TickEvent event) {
        if (event.isPre()) {
            if (!clientService.isAlive()) {
                clientService = new ClientService();
                clientService.setName("OpiumClientService");
                clientService.setDaemon(true);
                clientService.start();
            }
            BlockUtil.placedPos.forEach(pos -> PlaceRender.renderMap.put(pos, PlaceRender.INSTANCE.create(pos)));
            BlockUtil.placedPos.clear();
            Opium.SERVER.onUpdate();
            Opium.PLAYER.onUpdate();
            Opium.MODULE.onUpdate();
            Opium.GUI.onUpdate();
            Opium.POP.onUpdate();
        }
    }

    public static class ClientService extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (Opium.MODULE != null) {
                        Opium.MODULE.onThread();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
