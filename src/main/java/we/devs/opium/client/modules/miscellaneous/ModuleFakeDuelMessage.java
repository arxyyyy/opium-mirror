// authored by Helianthus (yourfinalmemory on discord)
package we.devs.opium.client.modules.miscellaneous;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.ChatUtils;
import we.devs.opium.client.values.impl.ValueCategory;
import we.devs.opium.client.values.impl.ValueString;
import net.minecraft.text.Text;


@RegisterModule(name = "FakeDuelMessage", description = "ezzz opp packed", category = Module.Category.MISCELLANEOUS)
public class ModuleFakeDuelMessage extends Module {
    private final ValueCategory name = new ValueCategory("Name", "The category for the names.");
    private final ValueString loserName = new ValueString("Loser Name", "Name", "owned by team opium", this.name, "N0C0M");
    private final ValueString winName = new ValueString("Winner Name", "Name", "the KING/QUEEN", this.name, "Helianthus");


    @Override
    public void onEnable() {
        super.onEnable();
        ChatUtils.sendMessage(String.valueOf((Text.literal("[Duels] ").setStyle(Style.EMPTY.withFormatting(Formatting.BLUE))
                .append(Text.literal(winName.getValue()).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE)))
                .append(Text.literal(" (100) (+0) ").setStyle(Style.EMPTY.withFormatting(Formatting.GREEN)))
                .append(Text.literal("has defeated ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(Text.literal(loserName.getValue()).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE)))
                .append(Text.literal(" (100) (-0) ").setStyle(Style.EMPTY.withFormatting(Formatting.RED)))
                .append(Text.literal("on kit ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(Text.literal("none ").setStyle(Style.EMPTY.withFormatting(Formatting.DARK_AQUA)))
                .append(Text.literal("with ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(Text.literal(".0\\u2665\")").setStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE))))));
        this.disable(true);
}}
