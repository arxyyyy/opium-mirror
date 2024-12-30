package org.nrnr.opium.impl.module.client;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.module.ConcurrentModule;
import org.nrnr.opium.api.module.ModuleCategory;

/**
 * @author chronos
 * @since 1.0
 */
public class ChatModule extends ConcurrentModule {
    //
    Config<Boolean> debugConfig = new BooleanConfig("ChatDebug", "Allows client debug messages to be printed in the chat", false);

    /**
     *
     */
    public ChatModule() {
        super("Chat", "Manages the client chat", ModuleCategory.CLIENT);
    }
}
