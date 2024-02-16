package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class PortalGodMode extends Module {
    private List<TeleportConfirmC2SPacket> packets;

    // Constructor

    public PortalGodMode() {
        super(Mous.CATEGORY, "portal-god-mode", "Makes you invincible after leaving a nether portal.");
    }
    @Override
    public void onActivate() {
        this.packets = new ArrayList<>();
    }
    @Override
    public void onDeactivate() {
        if (!this.packets.isEmpty()) {
            mc.getNetworkHandler().sendPacket(this.packets.get(this.packets.size() - 1));
        }
    }
    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof TeleportConfirmC2SPacket packet) {
            this.packets.add(packet);
            event.setCancelled(true);
        }
    }
}
