package com.example.addon.modules;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;


public class Clip extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> horizontalRange = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("Horizontal auto clip range.")
        .defaultValue(10)
        .sliderRange(2, 10)
        .build()
    );

    public AutoClip() {
        super(Addon.CATEGORY, "clip", "Automatically clips through blocks if you collide");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.horizontalCollision) return;
        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        for (int i = 2; i < horizontalRange.get(); i++) {
            Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw()).normalize();
            Vec3d pos = new Vec3d(playerPos.getX() + forward.x * i, playerPos.getY(), playerPos.getZ() + forward.z * i);

            Block f1 = checkBlock(pos.getX(), pos.getY(), pos.getZ());
            Block fu = checkBlock(pos.getX(), pos.getY() + 1, pos.getZ());

            if (f1 == Blocks.AIR && fu == Blocks.AIR) {
                mc.player.setPosition(pos);
                break;
            }
        }
    }
    private Block checkBlock(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(new Vec3i((int) x, (int) y, (int) z))).getBlock();
    }
}
