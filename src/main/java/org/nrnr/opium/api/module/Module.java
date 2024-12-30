package org.nrnr.opium.api.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.ConfigContainer;
import org.nrnr.opium.api.config.Serializable;
import org.nrnr.opium.impl.module.render.ShadersModule;
import org.nrnr.opium.util.Globals;
import org.nrnr.opium.util.chat.ChatUtil;

/**
 * General client feature that will appear in the ClickGui. Module have a
 * unique name which is also used as the module identifier. Modules are
 * grouped by {@link ModuleCategory}.
 *
 * <p>Modules are {@link Serializable} and hold {@link Config}
 * in a {@link ConfigContainer}. Modules can be configured in the ClickGui
 * or through the use of Commands in the chat.</p>
 *
 * @author chronos
 * @see ToggleModule
 * @see ConcurrentModule
 * @since 1.0
 */
public class Module extends ConfigContainer implements Globals {
    //
    public static final String MODULE_ID_FORMAT = "%s-module";
    // Concise module description, displayed in the ClickGui to help users
    // understand the functionality of the module.
    private final String desc;
    // Modules are grouped into categories for easy navigation in the
    // ClickGui. Modules with ModuleCategory.TEST category are not available
    // to the user.
    private final ModuleCategory category;

    /**
     * @param name     The unique module identifier
     * @param desc     The module description
     * @param category The module category
     */
    public Module(String name, String desc, ModuleCategory category) {
        super(name);
        this.desc = desc;
        this.category = category;
    }

    /**
     * @param message
     */
    protected void sendModuleMessage(String message) {
        ChatUtil.clientSendMessageRaw("§s[%s]§f %s", name, message);
    }

    public static void render3D(MatrixStack matrixStack) {
        matrixStack.push();
        Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
        if (ShadersModule.INSTANCE.isEnabled()) {
            ShadersModule.INSTANCE.onRender3D(matrixStack, MinecraftClient.getInstance().getTickDelta());
        }
        matrixStack.pop();
    }

    /**
     * @param message
     * @param params
     */
    protected void sendModuleMessage(String message, Object... params) {
        sendModuleMessage(String.format(message, params));
    }

    /**
     * @return
     */
    @Override
    public String getId() {
        return String.format(MODULE_ID_FORMAT, name.toLowerCase());
    }



    /**
     * @return
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Returns the {@link ModuleCategory} of the module.
     *
     * @return The category of the module
     * @see ModuleCategory
     */
    public ModuleCategory getCategory() {
        return category;
    }

    /**
     * @return
     */
    public String getModuleData() {
        return "ARRAYLIST_INFO";
    }


}
