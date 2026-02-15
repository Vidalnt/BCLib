package org.betterx.bclib.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Half;
import org.betterx.bclib.api.v3.datagen.DropSelfLootProvider;
import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.BehaviourHelper;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.behaviours.interfaces.BehaviourWood;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.item.api.ItemTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

public abstract class BaseTrapdoorBlock
    extends TrapDoorBlock
    implements
        RenderLayerProvider,
        BlockModelProvider,
        BlockTagProvider,
        ItemTagProvider,
        DropSelfLootProvider<BaseTrapdoorBlock>
{

    protected BaseTrapdoorBlock(
        BlockBehaviour.Properties properties,
        BlockSetType type
    ) {
        super(type, properties);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    @Override
    public void registerBlockTags(
        Identifier location,
        TagBootstrapContext<Block> context
    ) {
        context.add(this, BlockTags.TRAPDOORS);
    }

    @Override
    public void registerItemTags(
        Identifier location,
        ItemTagBootstrapContext context
    ) {
        context.add(this, ItemTags.TRAPDOORS);
    }

    private VariantMutator xRotationForState(
        boolean isTop,
        boolean isOpen,
        Direction dir
    ) {
        return (isTop && isOpen)
            ? BlockModelGenerators.X_ROT_270
            : isTop
                ? BlockModelGenerators.X_ROT_180
                : isOpen
                    ? BlockModelGenerators.X_ROT_90
                    : BlockModelGenerators.NOP;
    }

    private VariantMutator yRotationForState(
        boolean isTop,
        boolean isOpen,
        Direction dir
    ) {
        VariantMutator y = BlockModelGenerators.NOP;
        switch (dir) {
            case EAST:
                y = (isTop && isOpen)
                    ? BlockModelGenerators.Y_ROT_270
                    : BlockModelGenerators.Y_ROT_90;
                break;
            case NORTH:
                if (isTop && isOpen) y = BlockModelGenerators.Y_ROT_180;
                break;
            case SOUTH:
                y = (isTop && isOpen)
                    ? BlockModelGenerators.NOP
                    : BlockModelGenerators.Y_ROT_180;
                break;
            case WEST:
                y = (isTop && isOpen)
                    ? BlockModelGenerators.Y_ROT_90
                    : BlockModelGenerators.Y_ROT_270;
                break;
            default:
                break;
        }
        return y;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void provideBlockModels(WoverBlockModelGenerators generator) {
        final var id = TextureMapping.getBlockTexture(this);
        final var mapping = new TextureMapping()
            .put(TextureSlot.TEXTURE, id)
            .put(
                TextureSlot.SIDE,
                Identifier.fromNamespaceAndPath(
                    id.getNamespace(),
                    id.getPath().replace("_trapdoor", "")
                ).withSuffix("_door_side")
            );

        final var model = BCLModels.TRAPDOOR.create(
            this,
            mapping,
            generator.modelOutput()
        );

        final var props = PropertyDispatch.initial(HALF, OPEN, FACING);
        final Direction[] directions = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
        };
        final boolean[] open = { true, false };
        final Half[] halfs = { Half.TOP, Half.BOTTOM };
        for (Direction dir : directions) {
            for (Half half : halfs) {
                for (boolean isOpen : open) {
                    props.select(
                        half,
                        isOpen,
                        dir,
                        BlockModelGenerators.plainVariant(model)
                            .with(
                                xRotationForState(half == Half.TOP, isOpen, dir)
                            )
                            .with(
                                yRotationForState(half == Half.TOP, isOpen, dir)
                            )
                    );
                }
            }
        }

        generator.acceptBlockState(
            MultiVariantGenerator.dispatch(this).with(props)
        );
    }

    public static class Wood
        extends BaseTrapdoorBlock
        implements BehaviourWood
    {

        public Wood(Block source, BlockSetType type, boolean flammable) {
            this(
                BehaviourBuilders.createTrapDoor(
                    source.defaultMapColor(),
                    flammable
                ).sound(SoundType.WOOD),
                type
            );
        }

        public Wood(Properties properties, BlockSetType type) {
            super(properties, type);
        }

        @Override
        public void registerBlockTags(
            Identifier location,
            TagBootstrapContext<Block> context
        ) {
            context.add(this, BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS);
        }

        @Override
        public void registerItemTags(
            Identifier location,
            ItemTagBootstrapContext context
        ) {
            context.add(this, ItemTags.TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        }
    }

    public static class Stone
        extends BaseTrapdoorBlock
        implements BehaviourStone
    {

        public Stone(Block source, BlockSetType type) {
            this(
                BehaviourBuilders.createTrapDoor(
                    source.defaultMapColor(),
                    false
                ).sound(SoundType.STONE),
                type
            );
        }

        public Stone(Properties properties, BlockSetType type) {
            super(properties, type);
        }
    }

    public static class Metal
        extends BaseTrapdoorBlock
        implements BehaviourMetal
    {

        public Metal(Block source, BlockSetType type) {
            this(
                BehaviourBuilders.createTrapDoor(
                    source.defaultMapColor(),
                    false
                ).sound(SoundType.METAL),
                type
            );
        }

        public Metal(Properties properties, BlockSetType type) {
            super(properties, type);
        }
    }

    public static BaseTrapdoorBlock from(
        Block source,
        BlockSetType type,
        boolean flammable
    ) {
        return BehaviourHelper.from(
            source,
            type,
            (s, t) -> new Wood(s, t, flammable),
            Stone::new,
            Metal::new
        );
    }
}
