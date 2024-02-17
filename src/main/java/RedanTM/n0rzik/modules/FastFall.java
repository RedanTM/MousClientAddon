package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FastFall extends MousModule {
    public FastFall(){
        super(Mous.CATEGORY, "fast-fall", "Falls faster");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> fallSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Fall Speed")
            .defaultValue(1)
            .sliderRange(0.5,10)
            .build()
    );

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (!(mc.player.fallDistance > .005)) return;

        BlockPos playerPos = mc.player.getBlockPos();

        Vec3d pos = mc.player.getPos();
        Vec3d centerPos = new Vec3d(playerPos.toCenterPos().getX(),pos.getY(),playerPos.toCenterPos().getZ());

        float yaw = (float) Rotations.getYaw(centerPos);

        float speed = .15f;

        double s = Math.sin(Math.toRadians(yaw));
        double c = Math.cos(Math.toRadians(yaw));
        double nx = speed * -1 * s;
        double nz = speed * -1 * -c;

        mc.player.setVelocity(nx,-fallSpeed.get(),nz);
    }
}