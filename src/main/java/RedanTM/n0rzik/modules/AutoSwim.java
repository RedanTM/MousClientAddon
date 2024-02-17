package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;

public class AutoSwim extends MousModule {
    public AutoSwim() {
        super(Mous.CATEGORY, "auto-swim", "Automatically switches into the new swimming pose in water");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> onlyInWater = sgGeneral.add(new BoolSetting.Builder()
            .name("Only in Water")
            .defaultValue(true)
            .build()
    );

    @EventHandler
    public void onTick(TickEvent.Post tick) {
        if ((onlyInWater.get() && mc.player.isTouchingWater()) || !onlyInWater.get()) mc.player.setPose(EntityPose.SWIMMING);
    }
}