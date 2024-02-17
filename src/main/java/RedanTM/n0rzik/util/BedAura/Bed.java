package RedanTM.n0rzik.util.BedAura;

import RedanTM.n0rzik.util.Surround.Automation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bed {

    public static ArrayList<Vec3i> selfTrapPositions = new ArrayList<Vec3i>() {{
        add(new Vec3i(1, 1, 0));
        add(new Vec3i(-1, 1, 0));
        add(new Vec3i(0, 1, 1));
        add(new Vec3i(0, 1, -1));
    }};

    public static BlockPos getSelfTrapBlock(PlayerEntity p, Boolean escapePrevention) {
        BlockPos tpos = p.getBlockPos();
        List<BlockPos> selfTrapBlocks = new ArrayList<>();
        if (!escapePrevention && Automation.isTrapBlock(tpos.up(2))) return tpos.up(2);
        for (Vec3i stp : selfTrapPositions) {
            BlockPos stb = tpos.add( stp.getX(), stp.getY(), stp.getZ());
            if (Automation.isTrapBlock(stb)) selfTrapBlocks.add(stb);
        }
        if (selfTrapBlocks.isEmpty()) return null;
        return selfTrapBlocks.get(new Random().nextInt(selfTrapBlocks.size()));
    }
}