package org.betterx.bclib.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.betterx.bclib.interfaces.ItemModelProvider;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.bclib.models.RecordItemModelProvider;

public class CustomModelBakery {

    private record StateModelPair(BlockState state, MultiVariant model) {}

    private final Map<Identifier, MultiVariant> models =
        Maps.newConcurrentMap();
    private final Map<Identifier, MultiVariant> itemModels =
        Maps.newConcurrentMap();
    private final Map<Block, List<StateModelPair>> blockModels =
        Maps.newConcurrentMap();

    public MultiVariant getBlockModel(Identifier location) {
        return models.get(location);
    }

    public MultiVariant getItemModel(Identifier location) {
        return itemModels.get(location);
    }

    public void registerBlockStateResolvers(
        ModelLoadingPlugin.Context pluginContext
    ) {
        for (Map.Entry<
            Block,
            List<StateModelPair>
        > e : this.blockModels.entrySet()) {
            pluginContext.registerBlockStateResolver(e.getKey(), context -> {
                e
                    .getValue()
                    .forEach(p ->
                        context.setModel(p.state, p.model.toUnbaked().asRoot())
                    );
            });
        }
    }

    public void loadCustomModels(ResourceManager resourceManager) {
        BuiltInRegistries.BLOCK.stream()
            .parallel()
            .filter(block -> block instanceof RuntimeBlockModelProvider)
            .forEach(block -> {
                Identifier blockID = BuiltInRegistries.BLOCK.getKey(block);
                Identifier storageID = Identifier.fromNamespaceAndPath(
                    blockID.getNamespace(),
                    "blockstates/" + blockID.getPath() + ".json"
                );
                if (resourceManager.getResource(storageID).isEmpty()) {
                    addBlockModel(blockID, block);
                }
                storageID = Identifier.fromNamespaceAndPath(
                    blockID.getNamespace(),
                    "models/item/" + blockID.getPath() + ".json"
                );
                if (resourceManager.getResource(storageID).isEmpty()) {
                    addItemModel(blockID, (ItemModelProvider) block);
                }
            });

        BuiltInRegistries.ITEM.stream()
            .parallel()
            .filter(
                item ->
                    item instanceof ItemModelProvider ||
                    RecordItemModelProvider.has(item)
            )
            .forEach(item -> {
                Identifier registryID = BuiltInRegistries.ITEM.getKey(item);
                Identifier storageID = Identifier.fromNamespaceAndPath(
                    registryID.getNamespace(),
                    "models/item/" + registryID.getPath() + ".json"
                );
                final ItemModelProvider provider = (item instanceof
                        ItemModelProvider)
                    ? (ItemModelProvider) item
                    : RecordItemModelProvider.get(item);

                if (resourceManager.getResource(storageID).isEmpty()) {
                    addItemModel(registryID, provider);
                }
            });
    }

    private void addBlockModel(Identifier blockID, Block block) {
        RuntimeBlockModelProvider provider = (RuntimeBlockModelProvider) block;
        ImmutableList<BlockState> states = block
            .getStateDefinition()
            .getPossibleStates();
        BlockState defaultState = block.defaultBlockState();
        MultiVariant defaultModel = provider.getModelVariant(
            blockID,
            defaultState,
            models
        );

        List<StateModelPair> stateModels = new ArrayList<>(states.size());

        states.forEach(blockState -> {
            MultiVariant model = provider.getModelVariant(
                blockID,
                blockState,
                models
            );
            models.put(blockID, model);
            stateModels.add(new StateModelPair(blockState, model));
        });

        blockModels.put(block, stateModels);
    }

    private void addItemModel(Identifier itemID, ItemModelProvider provider) {
        //        Identifier modelLocation = itemID.withSuffix("inventory");
        //
        //        if (!models.containsKey(modelLocation)) {
        //            Identifier itemModelLocation = itemID.withPrefix("item/");
        //            BlockModel model = provider.getItemModel(modelLocation);
        //            itemModels.put(modelLocation, model);
        //            itemModels.put(itemModelLocation, model);
        //        }
    }
}
