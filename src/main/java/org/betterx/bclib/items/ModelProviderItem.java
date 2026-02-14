package org.betterx.bclib.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class ModelProviderItem extends Item implements ItemModelProvider {

    public ModelProviderItem(Properties settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createItemModel(resourceLocation);
    }
}
