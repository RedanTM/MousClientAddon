package RedanTM.n0rzik.util.Module;

import RedanTM.n0rzik.modules.*;

public class PriorityUtils {
    // Tell me a better way to do this pls
    public static int get(Object module) {
        if (module instanceof AutoCrystalRewrite) {return 10;}
        if (module instanceof BedAuraPlus) {return 8;}
        if (module instanceof SelfTrapPlus) {return 1;}
        if (module instanceof SurroundPlus) {return 0;}

        return 100;
    }
}
