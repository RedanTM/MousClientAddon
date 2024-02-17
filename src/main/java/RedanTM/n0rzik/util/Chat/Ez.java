package RedanTM.n0rzik.util.Chat;

import RedanTM.n0rzik.modules.EzPop;
import RedanTM.n0rzik.modules.BedAuraPlus;
import RedanTM.n0rzik.util.Surround.HelperSurround;
import RedanTM.n0rzik.util.Discord.DiscordStatus;
import RedanTM.n0rzik.util.Discord.StringHelper;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.starscript.utils.StarscriptError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ez {
    private static final Random RANDOM = new Random();
    public static List<String> currentTargets = new ArrayList<>();

    public static void sendAutoEz(String playerName) {
        increaseKC();
        MeteorStarscript.ss.set("killed", playerName);
        EzPop popCounter = Modules.get().get(EzPop.class);
        if (popCounter.ezScripts.isEmpty()) {
            ChatUtils.warning("Your auto ez message list is empty!");
            return;
        }

        var script = popCounter.ezScripts.get(RANDOM.nextInt(popCounter.ezScripts.size()));

        try {
            StringBuilder stringBuilder = new StringBuilder(MeteorStarscript.ss.run(script).toString());
            if (popCounter.killStr.get()) stringBuilder.append(" | Killstreak: ").append(DiscordStatus.killStreak);
            if (popCounter.suffix.get() && popCounter.suffixScript != null) stringBuilder.append(MeteorStarscript.ss.run(popCounter.suffixScript).toString());

            String ezMessage = stringBuilder.toString();
            ChatUtils.sendPlayerMsg(ezMessage);
            if (popCounter.pmEz.get()) HelperSurround.messagePlayer(playerName, StringHelper.stripName(playerName, ezMessage));
        } catch (StarscriptError error) {
            MeteorStarscript.printChatError(error);
        }
    }

    public static void increaseKC() {
        DiscordStatus.kills++;
        DiscordStatus.killStreak++;
    }

    public static void updateTargets() {
        currentTargets.clear();
        ArrayList<Module> modules = new ArrayList<>();
        modules.add(Modules.get().get(CrystalAura.class));
        modules.add(Modules.get().get(KillAura.class));
        modules.add(Modules.get().get(BedAuraPlus.class));
        for (Module module : modules) currentTargets.add(module.getInfoString());
    }
}