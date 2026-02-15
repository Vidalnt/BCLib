package org.betterx.bclib.complexmaterials.set.wood;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;

public class WoodSlots {

    public static final MaterialSlot<WoodenComplexMaterial> FENCE = new Fence();
    public static final MaterialSlot<WoodenComplexMaterial> STRIPPED_LOG =
        new StrippedLog();
    public static final MaterialSlot<WoodenComplexMaterial> STRIPPED_BARK =
        new StrippedBark();
    public static final MaterialSlot<WoodenComplexMaterial> LOG = new Log();
    public static final MaterialSlot<WoodenComplexMaterial> BARK = new Bark();
    public static final MaterialSlot<WoodenComplexMaterial> PLANKS =
        new Planks();
    public static final MaterialSlot<WoodenComplexMaterial> SLAB = new Slab();
    public static final MaterialSlot<WoodenComplexMaterial> PRESSURE_PLATE =
        new PressurePlate();
    public static final MaterialSlot<WoodenComplexMaterial> TRAPDOOR =
        new Trapdoor();
    public static final MaterialSlot<WoodenComplexMaterial> DOOR = new Door();
    public static final Sign SIGN = new Sign();
    public static final HangingSign HANGING_SIGN = new HangingSign();

    public static final String WALL_SIGN = Sign.WALL_SUFFFIX;
    public static final String WALL_HANGING_SIGN = HangingSign.WALL_SUFFFIX;
    public static final MaterialSlot<WoodenComplexMaterial> TABURET =
        new Taburet();
    public static final MaterialSlot<WoodenComplexMaterial> CHAIR = new Chair();
    public static final MaterialSlot<WoodenComplexMaterial> BAR_STOOL =
        new BarStool();
    public static final MaterialSlot<WoodenComplexMaterial> WALL = new Wall();
}
