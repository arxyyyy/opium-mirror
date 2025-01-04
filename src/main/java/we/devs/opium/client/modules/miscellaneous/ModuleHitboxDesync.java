package we.devs.opium.client.modules.miscellaneous;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.BlockPosX;

@RegisterModule(
        name = "HitboxDesync",
        description = "Desyncs your hitbox.",
        category = Module.Category.MISCELLANEOUS
)
public class ModuleHitboxDesync extends Module {

    private static final double MAGIC_OFFSET = 0.200009968835369999878673424677777777777761;

    @Override
    public void onUpdate() {
        Direction facing = mc.player.getHorizontalFacing();
        Box boundingBox = mc.player.getBoundingBox();
        Vec3d center = boundingBox.getCenter();
        Vec3d offset = new Vec3d(facing.getUnitVector());

        Vec3d finalPosition = calculateFinalPosition(center, offset, facing);

        mc.player.setPosition(
                finalPosition.x == 0 ? mc.player.getX() : finalPosition.x,
                mc.player.getY(),
                finalPosition.z == 0 ? mc.player.getZ() : finalPosition.z
        );

        disable(true);
    }

    private Vec3d calculateFinalPosition(Vec3d center, Vec3d offset, Direction facing) {
        Vec3d adjustedCenter = new BlockPosX(center)
                .toCenterPos()
                .add(0, -0.5, 0)
                .add(offset.multiply(MAGIC_OFFSET));

        return merge(adjustedCenter, facing);
    }

    private Vec3d merge(Vec3d vector, Direction facing) {
        return new Vec3d(
                vector.x * Math.abs(facing.getOffsetX()),
                vector.y * Math.abs(facing.getOffsetY()),
                vector.z * Math.abs(facing.getOffsetZ())
        );
    }
}
