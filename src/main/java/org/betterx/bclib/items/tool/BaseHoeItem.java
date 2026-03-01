package org.betterx.bclib.items.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ToolMaterial;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BaseHoeItem extends HoeItem implements ItemModelProvider {

    public BaseHoeItem(
        ToolMaterial material,
        int attackDamage,
        float attackSpeed,
        Properties settings
    ) {
        super(material, attackDamage, attackSpeed, settings);
    }

    public BaseHoeItem(ToolMaterial material, Properties settings) {
        this(material, 0, -3.0f, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createHandheldItem(resourceLocation);
    }
}
