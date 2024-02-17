package RedanTM.n0rzik.modules;

import RedanTM.n0rzik.Mous;
import RedanTM.n0rzik.MousModule;
import RedanTM.n0rzik.managers.Managers;
import RedanTM.n0rzik.util.crystalaura.*;
import RedanTM.n0rzik.timer.IntTimerList;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.DamageUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AutoCrystalRewrite extends MousModule {
    public AutoCrystalRewrite() {super(Mous.CATEGORY, "Auto Crystal Rewrite", "Breaks and places crystals automatically (but better).");}
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlace = settings.createGroup("Place");
    private final SettingGroup sgExplode = settings.createGroup("Explode");
    private final SettingGroup sgMisc = settings.createGroup("Misc");
    private final SettingGroup sgDamage = settings.createGroup("Damage");
    private final SettingGroup sgRotate = settings.createGroup("Rotate");
    private final SettingGroup sgExtrapolation = settings.createGroup("Extrapolation");
    private final SettingGroup sgRaytrace = settings.createGroup("Raytrace");
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgDebug = settings.createGroup("Debug");

    //  General Page
    private final Setting<Boolean> place = sgGeneral.add(new BoolSetting.Builder()
        .name("Place")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> explode = sgGeneral.add(new BoolSetting.Builder()
        .name("Explode")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> pauseEat = sgGeneral.add(new BoolSetting.Builder()
        .name("Pause Eat")
        .description(".")
        .defaultValue(true)
        .build()
    );

    //  Place Page
    private final Setting<Boolean> oldVerPlacements = sgPlace.add(new BoolSetting.Builder()
        .name("1.12 Placements")
        .description(".")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> instantPlace = sgPlace.add(new BoolSetting.Builder()
        .name("Instant Non-Blocked")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> instant = sgPlace.add(new BoolSetting.Builder()
        .name("Instant Place")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> placeSpeed = sgPlace.add(new DoubleSetting.Builder()
        .name("Place Speed")
        .description(".")
        .defaultValue(10)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> slowDamage = sgPlace.add(new DoubleSetting.Builder()
        .name("Slow Damage")
        .description(".")
        .defaultValue(3)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> slowSpeed = sgPlace.add(new DoubleSetting.Builder()
        .name("Slow Speed")
        .description(".")
        .defaultValue(2)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> placeRange = sgPlace.add(new DoubleSetting.Builder()
        .name("Place Range")
        .description(".")
        .defaultValue(5)
        .range(0, 10)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<RangeMode> placeRangeMode = sgPlace.add(new EnumSetting.Builder<RangeMode>()
        .name("Place Range Mode")
        .description(".")
        .defaultValue(RangeMode.Closest)
        .build()
    );
    private final Setting<Double> placeHeight = sgPlace.add(new DoubleSetting.Builder()
        .name("Place Height")
        .description(".")
        .defaultValue(-0.5)
        .sliderRange(-1, 2)
        .visible(() -> placeRangeMode.get().equals(RangeMode.Normal))
        .build()
    );
    private final Setting<Double> closestPlaceWidth = sgPlace.add(new DoubleSetting.Builder()
        .name("Closest Place Box Size Width")
        .description(".")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> placeRangeMode.get().equals(RangeMode.Closest))
        .build()
    );
    private final Setting<Double> closestPlaceHeight = sgPlace.add(new DoubleSetting.Builder()
        .name("Closest Place Box Height")
        .description(".")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> placeRangeMode.get().equals(RangeMode.Closest))
        .build()
    );
    private final Setting<Boolean> placeSwing = sgPlace.add(new BoolSetting.Builder()
        .name("Place Swing")
        .description(".")
        .defaultValue(false)
        .build()
    );
    private final Setting<SwingMode> placeSwingMode = sgPlace.add(new EnumSetting.Builder<SwingMode>()
        .name("Place Swing Mode")
        .description(".")
        .defaultValue(SwingMode.Full)
        .build()
    );
    private final Setting<Boolean> clearSend = sgPlace.add(new BoolSetting.Builder()
        .name("Clear Send")
        .description(".")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> clearReceive = sgPlace.add(new BoolSetting.Builder()
        .name("Clear Receive")
        .description(".")
        .defaultValue(true)
        .build()
    );

    //  Explode Page
    private final Setting<Double> expRange = sgExplode.add(new DoubleSetting.Builder()
        .name("Explode Range")
        .description(".")
        .defaultValue(5)
        .range(0, 10)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<Boolean> instantExp = sgExplode.add(new BoolSetting.Builder()
        .name("Instant Explode")
        .description(".")
        .defaultValue(false)
        .build()
    );
    private final Setting<Double> expSpeed = sgExplode.add(new DoubleSetting.Builder()
        .name("Explode Speed")
        .description(".")
        .defaultValue(2)
        .range(0.01, 20)
        .sliderRange(0.01, 20)
        .build()
    );
    private final Setting<RangeMode> expRangeMode = sgExplode.add(new EnumSetting.Builder<RangeMode>()
        .name("Explode Range Mode")
        .description(".")
        .defaultValue(RangeMode.Normal)
        .build()
    );
    private final Setting<Double> closestExpWidth = sgExplode.add(new DoubleSetting.Builder()
        .name("Closest Place Box Size Width")
        .description(".")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> expRangeMode.get().equals(RangeMode.Closest))
        .build()
    );
    private final Setting<Double> closestExpHeight = sgExplode.add(new DoubleSetting.Builder()
        .name("Closest Place Box Height")
        .description(".")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 3)
        .visible(() -> expRangeMode.get().equals(RangeMode.Closest))
        .build()
    );
    private final Setting<Double> expHeight = sgExplode.add(new DoubleSetting.Builder()
        .name("Explode Height")
        .description(".")
        .defaultValue(0.5)
        .sliderRange(-1, 2)
        .visible(() -> expRangeMode.get().equals(RangeMode.Normal))
        .build()
    );
    private final Setting<Boolean> setDead = sgExplode.add(new BoolSetting.Builder()
        .name("Set Dead")
        .description(".")
        .defaultValue(false)
        .build()
    );
    private final Setting<Double> setDeadDelay = sgExplode.add(new DoubleSetting.Builder()
        .name("Set Dead Delay")
        .description(".")
        .defaultValue(0.05)
        .range(0, 1)
        .sliderRange(0, 1)
        .visible(setDead::get)
        .build()
    );
    private final Setting<Boolean> explodeSwing = sgExplode.add(new BoolSetting.Builder()
        .name("Explode Swing")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<SwingMode> explodeSwingMode = sgExplode.add(new EnumSetting.Builder<SwingMode>()
        .name("Explode Swing Mode")
        .description(".")
        .defaultValue(SwingMode.Full)
        .build()
    );

    //  Damage Page
    private final Setting<DmgCheckMode> dmgCheckMode = sgDamage.add(new EnumSetting.Builder<DmgCheckMode>()
        .name("Dmg Check Mode")
        .description(".")
        .defaultValue(DmgCheckMode.Balanced)
        .build()
    );
    private final Setting<Double> minPlace = sgDamage.add(new DoubleSetting.Builder()
        .name("Min Place")
        .description(".")
        .defaultValue(4)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> maxSelfPlace = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Self Place")
        .description(".")
        .defaultValue(8)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> maxSelfPlaceRatio = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Self Place Ratio")
        .description(".")
        .defaultValue(0.3)
        .range(0, 5)
        .sliderRange(0, 5)
        .build()
    );
    private final Setting<Double> maxFriendPlace = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Friend Place")
        .description(".")
        .defaultValue(8)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> maxFriendPlaceRatio = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Friend Place Ratio")
        .description(".")
        .defaultValue(0.5)
        .range(0, 5)
        .sliderRange(0, 5)
        .build()
    );
    private final Setting<Double> minExplode = sgDamage.add(new DoubleSetting.Builder()
        .name("Min Explode")
        .description(".")
        .defaultValue(2.5)
        .range(0, 2)
        .sliderRange(0, 2)
        .build()
    );
    private final Setting<Double> maxSelfExp = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Self Explode")
        .description(".")
        .defaultValue(9)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> maxSelfExpRatio = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Self Explode Ratio")
        .description(".")
        .defaultValue(0.4)
        .range(0, 5)
        .sliderRange(0, 5)
        .build()
    );
    private final Setting<Double> maxFriendExp = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Friend Explode")
        .description(".")
        .defaultValue(12)
        .range(0, 20)
        .sliderRange(0, 20)
        .build()
    );
    private final Setting<Double> maxFriendExpRatio = sgDamage.add(new DoubleSetting.Builder()
        .name("Max Friend Explode Ratio")
        .description(".")
        .defaultValue(0.5)
        .range(0, 5)
        .sliderRange(0, 5)
        .build()
    );
    private final Setting<Double> forcePop = sgDamage.add(new DoubleSetting.Builder()
        .name("Force Pop")
        .description(".")
        .defaultValue(2)
        .range(0, 10)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<Double> antiFriendPop = sgDamage.add(new DoubleSetting.Builder()
        .name("Anti Friend Pop")
        .description(".")
        .defaultValue(2)
        .range(0, 10)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<Double> antiSelfPop = sgDamage.add(new DoubleSetting.Builder()
        .name("Anti Self Pop")
        .description(".")
        .defaultValue(2)
        .range(0, 10)
        .sliderRange(0, 10)
        .build()
    );

    //  Render Page
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("Render")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<RenderMode> renderMode = sgRender.add(new EnumSetting.Builder<RenderMode>()
        .name("Render Mode")
        .description(".")
        .defaultValue(RenderMode.MousClient)
        .build()
    );
    private final Setting<Double> animationSpeed = sgRender.add(new DoubleSetting.Builder()
        .name("Animation Speed")
        .description(".")
        .defaultValue(1)
        .sliderRange(0, 10)
        .build()
    );
    private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
        .name("Color")
        .description("U blind?")
        .defaultValue(new SettingColor(255, 0, 0, 150))
        .build()
    );

    //  Debug Page
    private final Setting<Boolean> debugRange = sgDebug.add(new BoolSetting.Builder()
        .name("Debug Range")
        .description(".")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> debugRangeX = sgDebug.add(new DoubleSetting.Builder()
        .name("Range Pos X")
        .description(".")
        .defaultValue(0)
        .sliderRange(-1000, 1000)
        .build()
    );
    private final Setting<Double> debugRangeY = sgDebug.add(new DoubleSetting.Builder()
        .name("Range Pos Y")
        .description(".")
        .defaultValue(0)
        .sliderRange(-1000, 1000)
        .build()
    );
    private final Setting<Double> debugRangeZ = sgDebug.add(new DoubleSetting.Builder()
        .name("Range Pos Z")
        .description(".")
        .defaultValue(0)
        .sliderRange(-1000, 1000)
        .build()
    );
    private final Setting<Double> debugRangeHeight1 = sgDebug.add(new DoubleSetting.Builder()
        .name("Debug Range Height 1")
        .description(".")
        .defaultValue(0)
        .sliderRange(-2, 2)
        .build()
    );
    private final Setting<Double> debugRangeHeight2 = sgDebug.add(new DoubleSetting.Builder()
        .name("Debug Range Height 2")
        .description(".")
        .defaultValue(0)
        .sliderRange(-2, 2)
        .build()
    );
    private final Setting<Double> debugRangeHeight3 = sgDebug.add(new DoubleSetting.Builder()
        .name("Debug Range Height 3")
        .description(".")
        .defaultValue(0)
        .sliderRange(-2, 2)
        .build()
    );
    private final Setting<Double> debugRangeHeight4 = sgDebug.add(new DoubleSetting.Builder()
        .name("Debug Range Height 4")
        .description(".")
        .defaultValue(0)
        .sliderRange(-2, 2)
        .build()
    );
    private final Setting<Double> debugRangeHeight5 = sgDebug.add(new DoubleSetting.Builder()
        .name("Debug Range Height 5")
        .description(".")
        .defaultValue(0)
        .sliderRange(-2, 2)
        .build()
    );

    double highestEnemy = 0;
    double enemyHP = 0;
    double highestFriend = 0;
    double friendHP = 0;
    double self = 0;
    double placeTimer = 0;
    double delayTimer = 0;
    BlockPos placePos = null;
    IntTimerList attacked = new IntTimerList(false);

    Map<String, List<Vec3d>> motions = new HashMap<>();
    List<Box> blocked = new ArrayList<>();
    Map<Vec3d, double[][]> dmgCache = new HashMap<>();
    BlockPos lastPos = null;
    Map<BlockPos, Double> earthMap = new HashMap<>();

    //BlackOut Render
    Vec3d renderPos = null;
    double renderProgress = 0;


    public enum DmgCheckMode {
        Suicidal,
        Balanced,
        Safe
    }
    public enum SwingMode {
        Client,
        Packet,
        Full
    }
    public enum RangeMode {
        Normal,
        Closest
    }
    public enum RenderMode {
        MousClient
    }

    @Override
    public void onActivate() {
        super.onActivate();
        earthMap.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onTickPre(TickEvent.Pre event) {
        if (mc.player != null && mc.world != null) {
            if (debugRange.get()) {
                ChatUtils.sendMsg(Text.of(debugRangeHeight(1) + "  " + dist(debugRangePos(1)) + "\n" +
                    debugRangeHeight(2) + "  " + dist(debugRangePos(2)) + "\n" +
                    debugRangeHeight(3) + "  " + dist(debugRangePos(3)) + "\n" +
                    debugRangeHeight(4) + "  " + dist(debugRangePos(4)) + "\n" +
                    debugRangeHeight(5) + "  " + dist(debugRangePos(5))));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onRender3D(Render3DEvent event) {
        double d = event.frameTime;
        attacked.update(d);
        placeTimer = Math.max(placeTimer - d * getSpeed(), 0);
        update();

        //Rendering
        switch (renderMode.get()) {
            case MousClient -> {
                if (placePos != null && !pausedCheck()) {
                    renderProgress = Math.min(1, renderProgress + event.frameTime);
                    renderPos = smoothMove(renderPos, new Vec3d(placePos.getX(), placePos.getY(), placePos.getZ()), event.frameTime * animationSpeed.get());
                } else {
                    renderProgress = Math.max(0, renderProgress - event.frameTime);
                }
                if (renderPos != null) {
                    Box box = new Box(renderPos.getX() + 0.5 - renderProgress / 2, renderPos.getY() - 0.5 - renderProgress / 2, renderPos.getZ() + 0.5 - renderProgress / 2,
                        renderPos.getX()  + 0.5 + renderProgress / 2, renderPos.getY() - 0.5 + renderProgress / 2, renderPos.getZ() + 0.5 + renderProgress / 2);
                    event.renderer.box(box, new Color(color.get().r, color.get().g, color.get().b, Math.round(color.get().a / 5f)), color.get(), ShapeMode.Both, 0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onReceive(PacketEvent.Receive event) {
        if (event.packet instanceof ExplosionS2CPacket) {
            if (clearReceive.get()) {blocked.clear();}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRender(Render3DEvent event) {
        if (mc.player != null && mc.world != null) {

        }
    }

    // Other stuff

    void update() {
        dmgCache.clear();
        placePos = place.get() ? getPlacePos((int) Math.ceil(placeRange.get())) : null;
        Entity expEntity = null;
        double[] value = null;
        Hand handToUse = getHand(Items.END_CRYSTAL);
        if (!pausedCheck() && handToUse != null && explode.get()) {
            for (Entity en : mc.world.getEntities()) {
                if (en.getType().equals(EntityType.END_CRYSTAL)) {
                    if (canExplode(en.getPos())) {
                        double[] dmg = getDmg(en.getPos())[0];
                        if ((expEntity == null || value == null) ||
                            (dmgCheckMode.get().equals(DmgCheckMode.Suicidal) && dmg[0] > value[0]) ||
                            (dmgCheckMode.get().equals(DmgCheckMode.Balanced) && dmg[2] / dmg[0] < value[2] / dmg[0]) ||
                            (dmgCheckMode.get().equals(DmgCheckMode.Safe) && dmg[2] < value[2])) {
                            expEntity = en;
                            value = dmg;
                        }
                    }
                }
            }
        }
        if (expEntity != null) {
            if (!isAttacked(expEntity.getId())) {
                explode(expEntity.getId(), expEntity, expEntity.getPos());
            }
        }
        if (placePos != null) {
            if (handToUse != null) {
                if (!placePos.equals(lastPos)) {
                    lastPos = placePos;
                    placeTimer = 0;
                }
            }
        } else {
            lastPos = null;
        }
    }

    boolean isBlocked(BlockPos pos) {
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        for (Box bb : blocked) {
            if (bb.intersects(box)) {
                return true;
            }
        }
        return false;
    }

    boolean isAttacked(int id) {
        return attacked.contains(id);
    }

    void explode(int id, Entity en, Vec3d vec) {
        if (en != null) {
            attackEntity(en, true, true, true);
        } else {
            attackID(id, vec, true, true, true);
        }
    }

    void attackID(int id, Vec3d pos, boolean checkSD, boolean swing, boolean confirm) {
        Hand handToUse = getHand(Items.END_CRYSTAL);
        if (handToUse != null && !pausedCheck()) {
            EndCrystalEntity en = new EndCrystalEntity(mc.world, pos.x, pos.y, pos.z);
            en.setId(id);
            attackEntity(en, checkSD, swing, confirm);
        }
    }

    void attackEntity(Entity en, boolean checkSD, boolean swing, boolean confirm) {
        if (mc.player != null) {
            Hand handToUse = getHand(Items.END_CRYSTAL);
            if (handToUse != null) {
                attacked.add(en.getId(), expSpeed.get());
                delayTimer = 0;
                mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(en, mc.player.isSneaking()));
                if (swing && explodeSwing.get()) {swing(handToUse, explodeSwingMode.get());}
                if (confirm && clearSend.get()) {blocked.clear();}
                if (setDead.get() && checkSD) {
                    Managers.DELAY.add(() -> setEntityDead(en), setDeadDelay.get());
                }
            }
        }
    }

    boolean canExplode(Vec3d vec) {
        if (!inExplodeRange(expRangePos(vec))) {
            return false;
        }
        double[][] result = getDmg(vec);
        if (!explodeDamageCheck(result[0], result[1])) {
            return false;
        }
        return true;
    }

    Hand getHand(Item item) {
        if (Managers.HOLDING.isHolding(item)) {
            return Hand.MAIN_HAND;
        }
        if (mc.player.getOffHandStack().getItem().equals(item)) {
            return Hand.OFF_HAND;
        }
        return null;
    }

    void swing(Hand hand, SwingMode mode) {
        if (mode == SwingMode.Full || mode == SwingMode.Packet) {
            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
        }
        if (mode == SwingMode.Full || mode == SwingMode.Client) {
            mc.player.swingHand(hand);
        }
    }

    boolean pausedCheck() {
        if (mc.player != null) {
            return pauseEat.get() && (mc.player.isUsingItem() && mc.player.isHolding(Items.ENCHANTED_GOLDEN_APPLE));
        }
        return true;
    }

    void setEntityDead(Entity en) {
        en.remove(Entity.RemovalReason.KILLED);
    }

    BlockPos getPlacePos(int r) {
        BlockPos bestPos = null;
        double[] highest = null;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = mc.player.getBlockPos().add(x, y, z);
                    // Checks if crystal can be placed
                    if (!air(pos) || !(!oldVerPlacements.get() || air(pos.up())) || !inPlaceRange(placeRangePos(pos)) ||
                        !crystalBlock(pos.down()) || !inExplodeRange(expRangePos(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)))) {continue;}

                    // Calculates damages and healths
                    double[][] result = getDmg(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));

                    // Checks if damages are valid
                    if (!placeDamageCheck(result[0], result[1], highest)) {continue;}

                    // Checks if placement is blocked by other entities
                    Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
                    if (EntityUtils.intersectsWithEntity(box, entity -> !(entity.getType().equals(EntityType.END_CRYSTAL) && canExplode(entity.getPos())))) {continue;}
                    bestPos = pos;
                    highest = result[0];
                }
            }
        }
        return bestPos;
    }

    boolean placeDamageCheck(double[] dmg, double[] health, double[] highest) {
        //  0 = enemy, 1 = friend, 2 = self

        //  Dmg Check
        if (highest != null) {
            if (dmgCheckMode.get().equals(DmgCheckMode.Suicidal) && dmg[0] < highest[0]) {return false;}
            if (dmgCheckMode.get().equals(DmgCheckMode.Balanced) && dmg[2] / dmg[0] > highest[2] / highest[0]) {return false;}
            if (dmgCheckMode.get().equals(DmgCheckMode.Safe) && dmg[2] > highest[2]) {return false;}
        }

        //  Force/anti-pop check
        double playerHP = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (playerHP >= 0 && dmg[2] * antiSelfPop.get() >= playerHP) {return false;}
        if (health[1] >= 0 && dmg[1] * antiFriendPop.get() >= health[1]) {return false;}
        if (health[0] >= 0 && dmg[0] * forcePop.get() >= health[0]) {return true;}

        //  Min Damage
        if (dmg[0] < minPlace.get()) {return false;}

        //  Max Damage
        if (dmg[1] > maxFriendPlace.get()) {return false;}
        if (dmg[1] / dmg[0] > maxFriendPlaceRatio.get()) {return false;}
        if (dmg[2] > maxSelfPlace.get()) {return false;}
        if (dmg[2] / dmg[0] > maxSelfPlaceRatio.get()) {return false;}
        return true;
    }

    boolean explodeDamageCheck(double[] dmg, double[] health) {
        //  0 = enemy, 1 = friend, 2 = self

        //  Force/anti-pop check
        double playerHP = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (playerHP >= 0 && dmg[2] * forcePop.get() >= playerHP) {return false;}
        if (health[1] >= 0 && dmg[1] * antiFriendPop.get() >= health[1]) {return false;}
        if (health[0] >= 0 && dmg[0] * forcePop.get() >= health[0]) {return true;}

        //  Min Damage
        if (dmg[0] < minExplode.get()) {return false;}

        //  Max Damage
        if (dmg[1] > maxFriendExp.get()) {return false;}
        if (dmg[1] / dmg[0] > maxFriendExpRatio.get()) {return false;}
        if (dmg[2] > maxSelfExp.get()) {return false;}
        if (dmg[2] / dmg[0] > maxSelfExpRatio.get()) {return false;}
        return true;
    }

    double[][] getDmg(Vec3d vec) {
        if (dmgCache.containsKey(vec)) {return dmgCache.get(vec);}
        highestEnemy = -1;
        highestFriend = -1;
        self = -1;
        enemyHP = -1;
        friendHP = -1;
        mc.world.getPlayers().forEach(player -> {
            if (player.getHealth() <= 0 || player.isSpectator() || player == mc.player) {return;}
            double dmg = DamageUtils.crystalDamage(player, vec);
            double hp = player.getHealth() + player.getAbsorptionAmount();

            //  friend
            if (Friends.get().isFriend(player)) {
                if (dmg > highestFriend) {
                    highestFriend = dmg;
                    friendHP = hp;
                }
            }
            //  enemy
            else if (dmg > highestEnemy) {
                highestEnemy = dmg;
                enemyHP = hp;
            }
        });
        self = DamageUtils.crystalDamage(mc.player, vec);
        double[][] result = new double[][]{new double[] {highestEnemy, highestFriend, self}, new double[]{enemyHP, friendHP}};
        dmgCache.put(vec, result);
        return result;
    }

    boolean air(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }
    boolean crystalBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) ||
            mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK);
    }
    boolean inPlaceRange(Vec3d vec) {
        return dist(mc.player.getEyePos().add(-vec.x, -vec.y, -vec.z)) <= placeRange.get();
    }
    boolean inExplodeRange(Vec3d vec) {
        return dist(mc.player.getEyePos().add(-vec.x, -vec.y, -vec.z)) <= expRange.get();
    }
    double dist(Vec3d distances) {
        return Math.sqrt(distances.x * distances.x + distances.y * distances.y + distances.z * distances.z);
    }
    Vec3d expRangePos(Vec3d vec) {
        return expRangeMode.get().equals(RangeMode.Closest) ? Pos.getClosest(mc.player.getEyePos(), vec,
            closestExpWidth.get(), closestExpHeight.get()) : vec.add(0, expHeight.get(), 0);
    }
    Vec3d placeRangePos(BlockPos pos) {
        return placeRangeMode.get().equals(RangeMode.Closest) ? Pos.getClosest(mc.player.getEyePos(), new Vec3d(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5),
            closestPlaceWidth.get(), closestPlaceHeight.get()) :
        new Vec3d(pos.getX() + 0.5, pos.getY() + placeHeight.get(), pos.getZ() + 0.5);
    }
    Vec3d debugRangePos(int id) {
        return mc.player.getEyePos().add(-debugRangeX.get() - 0.5, -debugRangeY.get() - debugRangeHeight(id), -debugRangeZ.get() - 0.5);
    }
    double debugRangeHeight(int id) {return id == 1 ? debugRangeHeight1.get() : id == 2 ? debugRangeHeight2.get() : id == 3 ? debugRangeHeight3.get() : id == 4 ? debugRangeHeight4.get() : debugRangeHeight5.get();}
    double getSpeed() {
        return shouldSlow() ? slowSpeed.get() : placeSpeed.get();
    }
    boolean shouldSlow() {return placePos != null && getDmg(new Vec3d(placePos.getX() + 0.5, placePos.getY(), placePos.getZ() + 0.5))[0][0] <= slowDamage.get();}

    Vec3d smoothMove(Vec3d current, Vec3d target, double delta) {
        if (current == null) {return target;}
        double absX = Math.abs(current.x - target.x);
        double absY = Math.abs(current.y - target.y);
        double absZ = Math.abs(current.z - target.z);

        double x = absX * delta;
        double y = absY * delta;
        double z = absZ * delta;
        return new Vec3d(current.x > target.x ? Math.max(target.x, current.x - x) : Math.min(target.x, current.x + x),
            current.y > target.y ? Math.max(target.y, current.y - y) : Math.min(target.y, current.y + y),
            current.z > target.z ? Math.max(target.z, current.z - z) : Math.min(target.z, current.z + z));
    }
}
