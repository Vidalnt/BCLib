package org.betterx.bclib.complexmaterials;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.betterx.bclib.BCLib;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.items.boat.BoatTypeOverride;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.TagManager;
import org.jetbrains.annotations.Nullable;

public class WoodenComplexMaterial
    extends ComplexMaterialSet<WoodenComplexMaterial>
{

    public static final Identifier MATERIAL_ID = BCLib.makeID(
        "wooden_material"
    );

    public static final String BLOCK_STRIPPED_BARK =
        WoodSlots.STRIPPED_BARK.suffix;
    public static final String BLOCK_STRIPPED_LOG =
        WoodSlots.STRIPPED_LOG.suffix;
    public static final String BLOCK_PRESSURE_PLATE =
        WoodSlots.PRESSURE_PLATE.suffix;
    public static final String BLOCK_TRAPDOOR = WoodSlots.TRAPDOOR.suffix;
    public static final String BLOCK_PLANKS = WoodSlots.PLANKS.suffix;
    public static final String BLOCK_FENCE = WoodSlots.FENCE.suffix;
    public static final String BLOCK_BARK = WoodSlots.BARK.suffix;
    public static final String BLOCK_DOOR = WoodSlots.DOOR.suffix;
    public static final String BLOCK_SIGN = WoodSlots.SIGN.suffix;
    public static final String BLOCK_WALL_SIGN = WoodSlots.WALL_SIGN;
    public static final String BLOCK_SLAB = WoodSlots.SLAB.suffix;
    public static final String BLOCK_LOG = WoodSlots.LOG.suffix;

    public static final String TAG_LOGS = "logs";

    public final MapColor planksColor;
    public final MapColor woodColor;

    @Nullable
    protected BoatTypeOverride boatType;

    public final BCLWoodTypeWrapper woodType;
    protected Block clothMaterial;

    public WoodenComplexMaterial(
        ModCore modCore,
        String baseName,
        String receipGroupPrefix,
        MapColor woodColor,
        MapColor planksColor
    ) {
        super(modCore, baseName, receipGroupPrefix);
        this.planksColor = planksColor;
        this.woodColor = woodColor;
        this.woodType = createWoodTypeBuilder().build();
    }

    @Deprecated(forRemoval = true)
    public WoodenComplexMaterial(
        ModCore modCore,
        String baseName,
        String receipGroupPrefix,
        MapColor woodColor,
        MapColor planksColor,
        BoatTypeOverride boatType
    ) {
        this(modCore, baseName, receipGroupPrefix, woodColor, planksColor);
        this.setBoatType(boatType);
    }

    public <W extends WoodenComplexMaterial> W setBoatType(
        BoatTypeOverride boatType
    ) {
        this.boatType = boatType;
        return (W) this;
    }

    public <W extends WoodenComplexMaterial> W setFurnitureCloth(
        Block clothMaterial
    ) {
        this.clothMaterial = clothMaterial;
        return (W) this;
    }

    public Block furnitureCloth() {
        return clothMaterial;
    }

    public Block furnitureFrame() {
        return getBlock(WoodSlots.PLANKS);
    }

    protected BCLWoodTypeWrapper.Builder createWoodTypeBuilder() {
        return BCLWoodTypeWrapper.create(C, getBaseName()).setColor(
            planksColor
        );
    }

    @Override
    protected BlockBehaviour.Properties getBlockSettings() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).mapColor(
            planksColor
        );
    }

    @Override
    public Identifier getMaterialID() {
        return MATERIAL_ID;
    }

    @Override
    protected void initTags() {
        addBlockTag(TagManager.BLOCKS.makeTag(C, getBaseName() + "_logs"));
        addItemTag(TagManager.ITEMS.makeTag(C, getBaseName() + "_logs"));
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
            WoodSlots.STRIPPED_LOG,
            WoodSlots.STRIPPED_BARK,
            WoodSlots.LOG,
            WoodSlots.BARK,
            WoodSlots.PLANKS,
            WoodSlots.SLAB,
            WoodSlots.FENCE,
            WoodSlots.PRESSURE_PLATE,
            WoodSlots.TRAPDOOR,
            WoodSlots.DOOR,
            WoodSlots.SIGN
        );
    }

    @Override
    protected void initFlammable(FlammableBlockRegistry registry) {
        final Consumer<Block> addFlammableHardWood = (Block block) ->
            registry.add(block, 5, 5);
        getBlocks().forEach(block -> {
            registry.add(block, 5, 20);
        });

        ifBlockPresent(WoodSlots.LOG, addFlammableHardWood);
        ifBlockPresent(WoodSlots.BARK, addFlammableHardWood);
        ifBlockPresent(WoodSlots.STRIPPED_LOG, addFlammableHardWood);
        ifBlockPresent(WoodSlots.STRIPPED_BARK, addFlammableHardWood);
    }

    public final void initBoatType() {
        if (getBoatType() == null) {
            boatType = supplyBoatType();
        }
    }

    protected BoatTypeOverride supplyBoatType() {
        return BoatTypeOverride.create(
            C,
            getBaseName(),
            getBlock(WoodSlots.PLANKS)
        );
    }

    public BoatTypeOverride getBoatType() {
        return boatType;
    }
}
