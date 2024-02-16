package RedanTM.n0rzik;

import RedanTM.n0rzik.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class Mous extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("MousClientAddon");
    public static final HudGroup HUD_GROUP = new HudGroup("MousClientAddon");

    @Override
    public void onInitialize() {
        LOG.info("Initializing MousClientAddon");

        // Modules
        Modules.get().add(new Clip());
        Modules.get().add(new JoinMessage());
        Modules.get().add(new PortalGodMode());
        Modules.get().add(new HoleSnap());
        Modules.get().add(new NoClearChat());
        Modules.get().add(new PistonPush());
        Modules.get().add(new SurroundPlus());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}