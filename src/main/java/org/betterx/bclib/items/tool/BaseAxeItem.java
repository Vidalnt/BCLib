package org.betterx.bclib.items.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BaseAxeItem extends AxeItem implements ItemModelProvider {

    public BaseAxeItem(
        ToolMaterial material,
        float attackDamage,
        float attackSpeed,
        Properties settings
    ) {
        super(material, attackDamage, attackSpeed, settings);
    }

    public BaseAxeItem(ToolMaterial material, Properties settings) {
        this(material, 6.0f, -3.2f, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createHandheldItem(resourceLocation);
    }
}
