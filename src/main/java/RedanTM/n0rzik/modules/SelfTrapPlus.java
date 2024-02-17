package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import RedanTM.n0rzik.util.Surround.BlockHelper;
import RedanTM.n0rzik.util.Surround.HelperSurround;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class SelfTrapPlus extends MousModule {
    public enum modes {
        AntiFacePlace,
        Full,
        Top,
        None
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<modes> mode = sgGeneral.add(new EnumSetting.Builder<modes>().name("mode").description("Which positions to place on your top half.").defaultValue(modes.Top).build());
    private final Setting<Boolean> antiCev = sgGeneral.add(new BoolSetting.Builder().name("anti-cev-breaker").description("Protect yourself from cev breaker.").defaultValue(true).build());
    private final Setting<Integer> blockPerTick = sgGeneral.add(new IntSetting.Builder().name("blocks-per-tick").description("How many block placements per tick.").defaultValue(4).sliderMin(1).sliderMax(10).build());
    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder().name("center").description("Centers you on the block you are standing on before placing.").defaultValue(true).build());
    private final Setting<Boolean> turnOff = sgGeneral.add(new BoolSetting.Builder().name("turn-off").description("Turns off after placing.").defaultValue(true).build());
    private final Setting<Boolean> toggleOnMove = sgGeneral.add(new BoolSetting.Builder().name("toggle-on-move").description("Turns off if you move (chorus, pearl phase etc).").defaultValue(true).build());
    private final Setting<Boolean> onlyInHole = sgGeneral.add(new BoolSetting.Builder().name("only-in-hole").description("Won't place unless you're in a hole").defaultValue(true).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("Sends rotation packets to the server when placing.").defaultValue(true).build());

    private final List<BlockPos> placePositions = new ArrayList<>();
    private BlockPos startPos;
    private int bpt;

    private final ArrayList<Vec3i> full = new ArrayList<Vec3i>() {{
        add(new Vec3i(0, 2, 0));
        add(new Vec3i(1, 1, 0));
        add(new Vec3i(-1, 1, 0));
        add(new Vec3i(0, 1, 1));
        add(new Vec3i(0, 1, -1));
    }};

    private final ArrayList<Vec3i> antiFacePlace = new ArrayList<Vec3i>() {{
        add(new Vec3i(1, 1, 0));
        add(new Vec3i(-1, 1, 0));
        add(new Vec3i(0, 1, 1));
        add(new Vec3i(0, 1, -1));
    }};


    public SelfTrapPlus(){
        super(Mous.CATEGORY, "self-trap-plus", "Places obsidian around your head.");
    }

    @Override
    public void onActivate() {
        if (!placePositions.isEmpty()) placePositions.clear();
        if (center.get()) PlayerUtils.centerPlayer();
        startPos = mc.player.getBlockPos();
        bpt = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        bpt = 0;
        FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (!obsidian.found()) { error("No obsidian in hotbar!"); toggle(); return; }
        if (BlockHelper.isVecComplete(getTrapDesign()) && turnOff.get()) { info("Finished self trap."); toggle(); return;}
        if (toggleOnMove.get() && startPos != mc.player.getBlockPos()) { toggle(); return; }
        if (onlyInHole.get() && !HelperSurround.isInHole(mc.player)) { toggle(); return; }
        for (Vec3i b : getTrapDesign()) {
            if (bpt >= blockPerTick.get()) return;
            BlockPos ppos = mc.player.getBlockPos();
            BlockPos bb = ppos.add(b.getX(), b.getY(), b.getZ());
            if (BlockHelper.getBlock(bb) == Blocks.AIR) {
                BlockUtils.place(bb, obsidian, rotate.get(), 100, true);
                bpt++;
            }
        }
    }

    private ArrayList<Vec3i> getTrapDesign() {
        ArrayList<Vec3i> trapDesign = new ArrayList<Vec3i>();
        switch (mode.get()) {
            case Full -> { trapDesign.addAll(full); }
            case Top -> { trapDesign.add(new Vec3i(0, 2, 0)); }
            case AntiFacePlace -> { trapDesign.addAll(antiFacePlace); }
        }
        if (antiCev.get()) { trapDesign.add(new Vec3i(0, 3, 0));}
        return trapDesign;
    }
}