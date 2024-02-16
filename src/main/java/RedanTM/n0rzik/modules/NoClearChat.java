package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;


public class NoClearChat extends Module {
    public NoClearChat() {
        super(Mous.CATEGORY, "no-clear-chat", "Disables clearchat");
    }
    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        if (event.getMessage().getString().equals(" ")) {
            event.cancel();
        }
    }
}