package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HoleSnapPlus extends MousModule {
    public HoleSnapPlus() {
        super(Mous.CATEGORY, "hole-snap", "BLACK-OUT-NOOB");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
            .name("Height")
            .defaultValue(1)
            .sliderRange(0,5)
            .build()
    );
    private final Setting<Double> verticalVelocity = sgGeneral.add(new DoubleSetting.Builder()
            .name("Vertical Velocity")
            .defaultValue(1)
            .sliderRange(0.5,10)
            .build()
    );
    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
            .name("Only On Ground")
            .defaultValue(true)
            .build()
    );

    private boolean snapping = false;

    @Override
    public void onActivate() {
        snapping = false;
    }

    @Override
    public void onDeactivate() {
        snapping = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        snapping = false;

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;

        BlockPos playerPos = mc.player.getBlockPos();

        boolean foundHole = false;
        for (int i = 0; i<height.get(); i++){
            BlockPos feetPos = playerPos.down().add(0,-i,0);
            if (isHole(feetPos.getX(), feetPos.getY(), feetPos.getZ())) {
                foundHole = true;
                break;
            }
        }
        if (!foundHole) return;

        Vec3d pos = mc.player.getPos();
        Vec3d centerPos = new Vec3d(playerPos.toCenterPos().getX(),pos.getY(),playerPos.toCenterPos().getZ());

        float yaw = (float) Rotations.getYaw(centerPos);

        float speed = .15f;

        double s = Math.sin(Math.toRadians(yaw));
        double c = Math.cos(Math.toRadians(yaw));
        double nx = speed * -1 * s;
        double nz = speed * -1 * -c;

        snapping = true;
        mc.player.setVelocity(nx,-verticalVelocity.get(),nz);
    }

    private boolean isHole(int x, int y, int z) {
        return isHoleBlock(x, y - 1, z) &&
                isHoleBlock(x + 1, y, z) &&
                isHoleBlock(x - 1, y, z) &&
                isHoleBlock(x, y, z + 1) &&
                isHoleBlock(x, y, z - 1);
    }

    private boolean isHoleBlock(int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x,y,z);
        Block block = mc.world.getBlockState(blockPos).getBlock();
        return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.CRYING_OBSIDIAN;
    }
}