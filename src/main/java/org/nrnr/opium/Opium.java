package org.nrnr.opium;

import org.nrnr.opium.api.Identifiable;
import org.nrnr.opium.api.event.handler.EventBus;
import org.nrnr.opium.api.event.handler.EventHandler;
import org.nrnr.opium.api.file.ClientConfiguration;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.init.Modules;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Opium {
    // Client logger.
    // Client Event handler (aka Event bus) which handles event dispatching
    // and listening for client events.
    public static EventHandler EVENT_HANDLER;
    // Client configuration handler. This master saves/loads the client
    // configuration files which have been saved locally.
    public static ClientConfiguration CONFIG;
    // Client shutdown hooks which will run once when the MinecraftClient
    // game instance is shutdown.
    public static ShutdownHook SHUTDOWN;
    //
    public static boolean isFontLoaded = false;

    public static Executor EXECUTOR;

    public static void init() {


        EXECUTOR = Executors.newFixedThreadPool(1);
        EVENT_HANDLER = new EventBus();
        Managers.init();
        Modules.init();
        CONFIG = new ClientConfiguration();
        Managers.postInit();
        SHUTDOWN = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(SHUTDOWN);
        CONFIG.loadClient();
    }



    public static void info(String message) {
    }

    /**
     * @param message
     * @param params
     */
    public static void info(String message, Object... params) {
    }

    public static void info(Identifiable feature, String message) {
    }

    /**
     * @param feature
     * @param message
     * @param params
     */
    public static void info(Identifiable feature, String message,
                            Object... params) {
    }


    public static void error(String message) {
        ;
    }

    /**
     * @param message
     */
    public static void error(String message, Object... params) {
        ;
    }


    public static void error(Identifiable feature, String message) {
    }

    /**
     * @param feature
     * @param message
     * @param params
     */
    public static void error(Identifiable feature, String message,
                             Object... params) {
    }
}
