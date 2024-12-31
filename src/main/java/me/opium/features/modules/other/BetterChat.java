package me.opium.features.modules.other;

import me.opium.features.modules.Module;
import me.opium.features.settings.Setting;

public class BetterChat extends Module {
    public Setting<Boolean> timestamp = this.register(new Setting<>("Timestamp", true));

    public BetterChat(){
        super("BetterChat","",Category.MISC,true,false,false);
    }




}
