package we.devs.opium.client.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.FakePlayerEntity;
import we.devs.opium.client.modules.miscellaneous.ModuleMiddleClick;
import we.devs.opium.client.values.impl.ValueEnum;

@RegisterModule(name = "Speed", description = "Speeds your movement up.", category = Module.Category.MOVEMENT)
public class ModuleSpeed extends Module {
    ValueEnum mode = new ValueEnum("Mode", "Mode", "", modes.GrimStrafe);

    @Override
    public void onEnable() {
        if (this.mode.getValue().equals(modes.GrimStrafe)) {
            int collisions = 0;
            for (Entity entity : mc.world.getEntities()) {
                if (checkIsCollidingEntity(entity) && MathHelper.sqrt((float) mc.player.squaredDistanceTo(entity)) <= 1.5) {
                    collisions++;
                }
            }
            if (collisions > 0) {
                Vec3d velocity = mc.player.getVelocity();
                double factor = 0.08 * collisions;
                Vec2f strafe = handleStrafeMotion((float) factor);
                mc.player.setVelocity(velocity.x + strafe.x, velocity.y, velocity.z + strafe.y);
            }
        }
    }

    public enum modes {
        GrimStrafe
    }

    public boolean checkIsCollidingEntity(Entity entity) {
        return entity != null && entity != mc.player && entity instanceof LivingEntity
                && !(entity instanceof FakePlayerEntity) && !(entity instanceof ArmorStandEntity);
    }

    public Vec2f handleStrafeMotion(final float speed) {
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw);
        if (forward == 0.0f && strafe == 0.0f) {
            return Vec2f.ZERO;
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += forward > 0.0f ? -45 : 45;
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += forward > 0.0f ? 45 : -45;
                strafe = 0.0f;
            }
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        float rx = (float) Math.cos(Math.toRadians(yaw));
        float rz = (float) -Math.sin(Math.toRadians(yaw));
        return new Vec2f((forward * speed * rz) + (strafe * speed * rx),
                (forward * speed * rx) - (strafe * speed * rz));
    }

}
