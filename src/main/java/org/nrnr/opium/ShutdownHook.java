package org.nrnr.opium;

import org.nrnr.opium.api.file.ClientConfiguration;

import static org.nrnr.opium.RPC.stopRPC;

/**
 * @author chronos
 * @since 1.0
 */
public class ShutdownHook extends Thread {
    /**
     *
     */
    public ShutdownHook() {
        setName("Neverdies-ShutdownHook");
    }

    /**
     * This runs when the game is shutdown and saves the
     * {@link ClientConfiguration} files.
     *
     * @see ClientConfiguration#saveClient()
     */
    @Override
    public void run() {
        stopRPC();
        Opium.info("Saving configurations and shutting down!");
        Opium.CONFIG.saveClient();
    }
}
