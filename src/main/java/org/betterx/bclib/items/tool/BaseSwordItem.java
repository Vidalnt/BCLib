package org.betterx.bclib.items.tool;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BaseSwordItem extends Item implements ItemModelProvider {

    public BaseSwordItem(ToolMaterial material, Properties settings) {
        super(
            settings
                .component(DataComponents.WEAPON, new Weapon(1))
                .component(DataComponents.TOOL, createToolComponent(material))
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .durability(material.durability())
        );
    }

    public BaseSwordItem(
        ToolMaterial material,
        int attackDamage,
        float attackSpeed,
        Properties settings
    ) {
        this(
            material,
            settings.attributes(
                createAttributes(material, attackDamage, attackSpeed)
            )
        );
    }

    private static Tool createToolComponent(ToolMaterial material) {
        return new Tool(List.of(), 1.5f, 2, true);
    }

    private static ItemAttributeModifiers createAttributes(
        ToolMaterial material,
        int attackDamage,
        float attackSpeed
    ) {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    Item.BASE_ATTACK_DAMAGE_ID,
                    (double) (material.attackDamageBonus() + attackDamage),
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    Item.BASE_ATTACK_SPEED_ID,
                    (double) attackSpeed,
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(Identifier resourceLocation) {
        return ModelsHelper.createHandheldItem(resourceLocation);
    }
}
