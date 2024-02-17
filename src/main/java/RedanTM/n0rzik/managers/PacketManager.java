package RedanTM.n0rzik.managers;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class PacketManager {

    int packets;
    float timer;
    int sent;

    public PacketManager() {
        MeteorClient.EVENT_BUS.subscribe(this);
        this.packets = -1;
        this.timer = 0;
        this.sent = 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRender(Render3DEvent event) {
        timer += event.frameTime;
        if (timer >= 1) {
            packets = Math.round(sent / timer);
            sent = 0;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPacket(PacketEvent.Send event) {
        sent++;
    }

    public int getSent() {return packets;}
}



