package dev.opium.mod.modules.impl.misc;

import dev.opium.api.utils.math.Timer;
import dev.opium.mod.modules.Module;
import dev.opium.mod.modules.settings.impl.BooleanSetting;
import dev.opium.mod.modules.settings.impl.SliderSetting;
import dev.opium.mod.modules.settings.impl.StringSetting;
import net.minecraft.client.network.PlayerListEntry;

import java.util.*;

public class Spammer extends Module {
    public enum Type {
        Bot,
        Custom,
        AutoSex
    }
    private final StringSetting message = add(new StringSetting("Message", "这是私人0pium客户 群"));
    private final SliderSetting randoms =
            add(new SliderSetting("Random", 3, 0, 20,1));
    private final SliderSetting delay =
            add(new SliderSetting("Delay", 5, 0, 60,0.1).setSuffix("s"));
    public final BooleanSetting tellMode =
            add(new BooleanSetting("RandomMsg", false));
    public final BooleanSetting checkSelf =
            add(new BooleanSetting("CheckSelf", false));

    public Spammer() {
        super("Spammer", Category.Misc);
        setChinese("自动刷屏");
    }

    @Override
    public void onLogout() {
        disable();
    }

    Random random = new Random();
    Timer timer = new Timer();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public void onUpdate() {
        if (!timer.passedS(delay.getValue())) return;
        timer.reset();
        String randomString = generateRandomString(randoms.getValueInt());
        if (!randomString.isEmpty()) {
            randomString = " " + randomString;
        }
        if (tellMode.getValue()) {
            Collection<PlayerListEntry> players = mc.getNetworkHandler().getPlayerList();
            List<PlayerListEntry> list = new ArrayList<>(players);
            int size = list.size();
            if (size == 0) {
                return;
            }
            PlayerListEntry playerListEntry = list.get(random.nextInt(size));
            while (checkSelf.getValue() && Objects.equals(playerListEntry.getProfile().getName(), mc.player.getGameProfile().getName())) {
                playerListEntry = list.get(random.nextInt(size));
            }
            mc.getNetworkHandler().sendChatCommand("tell " + playerListEntry.getProfile().getName() + " " + message.getValue() + randomString);
        } else {
            mc.getNetworkHandler().sendChatMessage(message.getValue() + randomString);
        }
    }

    private String generateRandomString(int LENGTH) {
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}