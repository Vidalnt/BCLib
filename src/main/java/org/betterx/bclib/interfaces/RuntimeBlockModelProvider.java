package org.betterx.bclib.interfaces;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.client.models.PatternsHelper;
import org.jetbrains.annotations.Nullable;

//TODO: @Deprecated(forRemoval = true)
public interface RuntimeBlockModelProvider extends ItemModelProvider {
    @Environment(EnvType.CLIENT)
    default @Nullable BlockModel getBlockModel(
        Identifier resourceLocation,
        BlockState blockState
    ) {
        Optional<String> pattern = PatternsHelper.createBlockSimple(
            resourceLocation
        );
        return ModelsHelper.fromPattern(pattern);
    }

    static Identifier remapIdentifier(
        Identifier stateId,
        BlockState blockState
    ) {
        return remapIdentifier(stateId, blockState, "");
    }

    static Identifier remapIdentifier(
        Identifier stateId,
        BlockState blockState,
        String pathAddOn
    ) {
        return Identifier.fromNamespaceAndPath(
            stateId.getNamespace(),
            "block/" + stateId.getPath() + pathAddOn
        );
    }

    @Environment(EnvType.CLIENT)
    default MultiVariant getModelVariant(
        Identifier stateId,
        BlockState blockState,
        Map<Identifier, MultiVariant> modelCache
    ) {
        var modelId = remapIdentifier(stateId, blockState);
        registerBlockModel(stateId, modelId, blockState, modelCache);
        return ModelsHelper.createBlockSimple(modelId);
    }

    @Environment(EnvType.CLIENT)
    default void registerBlockModel(
        Identifier stateId,
        Identifier modelId,
        BlockState blockState,
        Map<Identifier, MultiVariant> modelCache
    ) {
        //        if (!modelCache.containsKey(modelId)) {
        //            BlockModel model = getBlockModel(stateId, blockState);
        //            if (model != null) {
        //                modelCache.put(modelId, model);
        //            } else {
        //                BCLib.LOGGER.warn("Error loading model: {}", modelId);
        //            }
        //        }
    }
}
