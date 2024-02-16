package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SurroundPlus extends Module {
    public SurroundPlus(){
        super(Mous.CATEGORY, "surround-plus", "surrounds you in blocks");
    }

    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgLogic = settings.createGroup("Logic");

    private int yStart = 0;

    private final Setting<Boolean> disableOnYChange = sgLogic.add(new BoolSetting.Builder()
            .name("Disable on Y level change")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> disableOnDeath = sgLogic.add(new BoolSetting.Builder()
            .name("Disable on Death")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> alertOfBreaking = sgLogic.add(new BoolSetting.Builder()
            .name("Alert Block Mining")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgLogic.add(new BoolSetting.Builder()
            .name("Rotate Place")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotateToBreakingBlocks = sgLogic.add(new BoolSetting.Builder()
            .name("Rotate To Blocks Being Broken")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onlyPlaceIfCrouching = sgLogic.add(new BoolSetting.Builder()
            .name("Only Place If Crouching")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> pauseIfEating = sgLogic.add(new BoolSetting.Builder()
            .name("Pause If Eating")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> centerOnActivate = sgLogic.add(new BoolSetting.Builder()
            .name("Center On Activate")
            .defaultValue(true)
            .build()
    );
    private final Setting<ShapeMode> renderType = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("mode")
            .defaultValue(ShapeMode.Lines)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("Side Color")
            .defaultValue(new SettingColor(255, 170, 0, 20))
            .build()
    );
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("Line Color")
            .defaultValue(new SettingColor(255, 170, 0, 200))
            .build()
    );

    private Setting<Integer> blocksPerTick = sgLogic.add(new IntSetting.Builder()
            .name("Blocks per Tick")
            .defaultValue(2)
            .sliderRange(1, 4)
            .build()
    );

    @Override
    public void onActivate() {
        if (centerOnActivate.get()) PlayerUtils.centerPlayer();
        yStart = mc.player.getBlockY();
    }

    private List<BlockPos> holeBlocks(BlockPos playerBlockPos) {
        return List.of(playerBlockPos.add(1,0,0), playerBlockPos.add(0,0,1), playerBlockPos.add(-1,0,0), playerBlockPos.add(0,0,-1));
    }
    private List<BlockPos> queue = new ArrayList<>();

    @Override
    public void onDeactivate() {
        queue.clear();
        yStart = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (yStart != mc.player.getBlockY() && disableOnYChange.get()) {
            toggle();
            return;
        };
        if (mc.player.isDead() && disableOnDeath.get()){
            toggle();
            return;
        };

        for (BlockPos p : holeBlocks(mc.player.getBlockPos())) {
            Block block = mc.world.getBlockState(p).getBlock();
            if (!(block.equals(Blocks.OBSIDIAN) && block.equals(Blocks.BEDROCK))) {
                queue.add(p);
            }
        }

        if (!queue.isEmpty()) {
            Iterator<BlockPos> iterator = queue.iterator();
            for (int i = 0; i < blocksPerTick.get(); i++) {
                if (iterator.hasNext()) {
                    if ((mc.player.isSneaking() && onlyPlaceIfCrouching.get()) || !onlyPlaceIfCrouching.get()) {
                        if ((pauseIfEating.get() && !mc.player.isUsingItem()) || !pauseIfEating.get()) {
                            BlockPos p = iterator.next();
                            FindItemResult result = InvUtils.findInHotbar(Items.OBSIDIAN);
                            if (rotate.get()) Rotations.rotate(Rotations.getYaw(p), Rotations.getPitch(p), 100);
                            BlockUtils.place(p, result, 0);
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onRender3dEvent(Render3DEvent event) {
        for (BlockPos p : holeBlocks(mc.player.getBlockPos())) {
            event.renderer.box(new Box(p), sideColor.get(), lineColor.get(), renderType.get(),0);
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!alertOfBreaking.get()) return;
        if (event.packet instanceof BlockBreakingProgressS2CPacket packet) {
            BlockPos breaking = packet.getPos();
            System.out.println(breaking);
            for (BlockPos p : holeBlocks(mc.player.getBlockPos())) {
                if (rotateToBreakingBlocks.get()) Rotations.rotate(Rotations.getYaw(p), Rotations.getPitch(p), 99);
                if (p.equals(breaking)) warning("Your surround block is being broken at " + breaking + " by " + ((PlayerEntity) mc.world.getEntityById(packet.getEntityId()).getDisplayName()));
            }
        }
    }
}