package RedanTM.n0rzik;

import RedanTM.n0rzik.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class Mous extends MeteorAddon {
    public static final Category CATEGORY = new Category("MousClient");

    @Override
    public void onInitialize() {
        Modules.get().add(new AntiBot()); //petr1shka
        Modules.get().add(new AutoCrystalRewrite()); //n0rzik
        Modules.get().add(new AutoSwim()); //petr1shka
        Modules.get().add(new BedAuraPlus()); //n0rzik
        Modules.get().add(new BurrowNotification()); //petr1shka
        Modules.get().add(new ChatEmotes()); //petr1shka
        Modules.get().add(new ChatPrefixPlus()); //petr1shka
        Modules.get().add(new EzPop()); //petr1shka
        Modules.get().add(new FastFall()); //petr1shka
        Modules.get().add(new HoleSnapPlus()); //n0rzik
        Modules.get().add(new JoinMessage()); //n0rzik
        Modules.get().add(new NoClearChat()); //n0rzik
        Modules.get().add(new PingSpoofer()); //petr1shka
        Modules.get().add(new PistonPush()); //n0rzik
        Modules.get().add(new RPC()); //n0rzik
        Modules.get().add(new ScaffoldPlus()); //petr1shka
        Modules.get().add(new SurroundPlus()); //n0rzik
        Modules.get().add(new SelfTrapPlus()); //n0rzik
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "RedanTM.n0rzik";
    }
}
