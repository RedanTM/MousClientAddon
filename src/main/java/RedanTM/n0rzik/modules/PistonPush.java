package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PistonPush extends MousModule {
    public PistonPush(){
        super(Mous.CATEGORY, "piston-push", "pushes enemy out of safe hole");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("Range")
            .defaultValue(1)
            .sliderRange(0.5,5)
            .build()
    );

    private final List<BlockPos> pistonRow = List.of(new BlockPos(0,1,1), new BlockPos(1,1,0), new BlockPos(-1,1,0), new BlockPos(0,1,-1));

    private List<BlockPos> queue = new ArrayList<>();

    @EventHandler
    private void onTick(TickEvent.Pre event){
        boolean fakePlayer = false;
        PlayerEntity p = TargetUtils.getPlayerTarget(range.get(), SortPriority.LowestDistance);
        FakePlayerEntity fakePlayerEntity = null;

        if (p == null) {
            if (!FakePlayerManager.getFakePlayers().isEmpty()) {
                for (FakePlayerEntity fp : FakePlayerManager.getFakePlayers()) {
                    if (fp.isInRange(mc.player, range.get())) fakePlayer = true;
                    fakePlayerEntity = fp;
                }
            }
            error("No Target");
            toggle();
            return;
        }

        FindItemResult piston = InvUtils.findInHotbar(Items.PISTON, Items.STICKY_PISTON);
        FindItemResult redstone = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);

        if (!redstone.found() || !piston.found()) {
            error("Missing Required Items in Hotbar. toggling");
            toggle();
            return;
        }

        BlockPos enemyPos = null;

        if (fakePlayer) {
            enemyPos = fakePlayerEntity.getBlockPos();
        } else {
            if (p != null && p.getBlockPos() != null) {
                enemyPos = p.getBlockPos();
            } else {
                error("No Target");
                toggle();
                return;
            }
        }

        if (enemyPos == null) {
            error("No Target");
            toggle();
            return;
        }

        List<Pair<BlockPos, BlockPos>> pistonBlockPoses = new ArrayList<>();

        // Piston Row

        for (BlockPos i : pistonRow) {
            BlockPos appended = enemyPos.add(i);
            BlockPos appendedappended = appended.add(i).add(0,-1,0);
            if (!mc.world.getBlockState(appended).isAir()) return;
            if (mc.world.getBlockState(appendedappended).isAir()) pistonBlockPoses.add(new Pair<>(appended, appendedappended));
        }

        if (pistonRow.isEmpty()) {
            error("No Places to Place Piston. Toggling");
            toggle();
            return;
        }

        Pair<BlockPos, BlockPos> selectedPair = pistonBlockPoses.get(0);

        Direction dir = Direction.getFacing(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        int yaw = 0;
        switch (dir) {
            case NORTH -> yaw = 180;
            case SOUTH -> yaw = 0;
            case WEST -> yaw = 90;
            case EAST -> yaw = -90;
            default -> yaw = 0;
        }

        Rotations.rotate(yaw ,Rotations.getPitch(selectedPair.getLeft()));

        BlockUtils.place(selectedPair.getLeft(), piston, false, 5);
        BlockUtils.place(selectedPair.getRight(), redstone, 5);

        toggle();

    }
}