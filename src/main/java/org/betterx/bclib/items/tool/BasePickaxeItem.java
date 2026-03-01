package org.betterx.bclib.items.tool;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BasePickaxeItem extends Item implements ItemModelProvider {

    public BasePickaxeItem(
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

    public BasePickaxeItem(ToolMaterial material, Properties settings) {
        super(
            settings
                .component(DataComponents.TOOL, createToolComponent(material))
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .durability(material.durability())
        );
    }

    private static Tool createToolComponent(ToolMaterial material) {
        HolderSet<Block> mineableBlocks = BuiltInRegistries.BLOCK.getOrThrow(
            BlockTags.MINEABLE_WITH_PICKAXE
        );
        HolderSet<Block> incorrectBlocks = BuiltInRegistries.BLOCK.getOrThrow(
            material.incorrectBlocksForDrops()
        );

        return new Tool(
            List.of(
                Tool.Rule.minesAndDrops(mineableBlocks, material.speed()),
                Tool.Rule.overrideSpeed(incorrectBlocks, 1.0f)
            ),
            1.0f,
            1,
            true
        );
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
