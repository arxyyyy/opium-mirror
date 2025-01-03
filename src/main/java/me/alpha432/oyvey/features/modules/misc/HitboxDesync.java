package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.BlockPosX;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HitboxDesync extends Module {
    public HitboxDesync() {
        super("HitboxDesync","",Category.MISC,true,false,false);
    }
    private static final double MAGIC_OFFSET = .200009968835369999878673424677777777777761;

    @Override
    public void onUpdate() {
        Direction f = mc.player.getHorizontalFacing();
        Box bb = mc.player.getBoundingBox();
        Vec3d center = bb.getCenter();
        Vec3d offset = new Vec3d(f.getUnitVector());

        Vec3d fin = merge(new BlockPosX(center).toCenterPos().add(0, -0.5,0).add(offset.multiply(MAGIC_OFFSET)), f);
        mc.player.setPosition(
                fin.x == 0 ? mc.player.getX() : fin.x,
                mc.player.getY(),
                fin.z == 0 ? mc.player.getZ() : fin.z);
        disable();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    private Vec3d merge(Vec3d a, Direction facing) {
        return new Vec3d(a.x * Math.abs(facing.getOffsetX()), a.y * Math.abs(facing.getOffsetY()), a.z * Math.abs(facing.getOffsetZ()));
    }
}
