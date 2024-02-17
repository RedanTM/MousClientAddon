package RedanTM.n0rzik.util.Discord;

import RedanTM.n0rzik.modules.BurrowNotification;
import RedanTM.n0rzik.util.Chat.Ez;

public class DiscordStatus {
    public static int kills = 0;
    public static int deaths = 0;
    public static int killStreak = 0;
    public static int highscore = 0;
    public static long rpcStart = System.currentTimeMillis() / 1000L;

    public static void reset() {
        kills = 0;
        deaths = 0;
        killStreak = 0;
        highscore = 0;
        BurrowNotification.burrowedPlayers.clear();
        Ez.currentTargets.clear();
    }
}