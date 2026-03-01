package org.betterx.bclib.items.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BaseShovelItem extends ShovelItem implements ItemModelProvider {

    public BaseShovelItem(
        ToolMaterial material,
        float attackDamage,
        float attackSpeed,
        Properties settings
    ) {
        super(material, attackDamage, attackSpeed, settings);
    }

    public BaseShovelItem(ToolMaterial material, Properties settings) {
        this(material, 1.5f, -3.0f, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createHandheldItem(resourceLocation);
    }
}
