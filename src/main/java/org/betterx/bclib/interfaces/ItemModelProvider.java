package org.betterx.bclib.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import org.betterx.bclib.client.models.ModelsHelper;

public interface ItemModelProvider {
    @Environment(EnvType.CLIENT)
    default BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createItemModel(resourceLocation);
    }
}
