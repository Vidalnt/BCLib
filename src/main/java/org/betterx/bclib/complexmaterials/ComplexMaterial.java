package org.betterx.bclib.complexmaterials;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.betterx.bclib.complexmaterials.entry.BlockEntry;
import org.betterx.bclib.complexmaterials.entry.ItemEntry;
import org.betterx.bclib.complexmaterials.entry.MaterialSlot;
import org.betterx.bclib.complexmaterials.entry.RecipeEntry;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.jetbrains.annotations.Nullable;

public abstract class ComplexMaterial {

    private static final Map<Identifier, List<RecipeEntry>> RECIPE_ENTRIES =
        Maps.newHashMap();
    private static final Map<Identifier, List<BlockEntry>> BLOCK_ENTRIES =
        Maps.newHashMap();
    private static final Map<Identifier, List<ItemEntry>> ITEM_ENTRIES =
        Maps.newHashMap();
    private static final List<ComplexMaterial> MATERIALS = Lists.newArrayList();

    private final List<RecipeEntry> defaultRecipeEntries = Lists.newArrayList();
    private final List<BlockEntry> defaultBlockEntries = Lists.newArrayList();
    private final List<ItemEntry> defaultItemEntries = Lists.newArrayList();

    private final Map<String, TagKey<Block>> blockTags = Maps.newHashMap();
    private final Map<String, TagKey<Item>> itemTags = Maps.newHashMap();
    private final Map<String, Block> blocks = Maps.newHashMap();
    private final Map<String, Item> items = Maps.newHashMap();

    protected final String baseName;
    public final ModCore C;
    protected final String receipGroupPrefix;

    public ComplexMaterial(
        ModCore modID,
        String baseName,
        String receipGroupPrefix
    ) {
        this.baseName = baseName;
        this.C = modID;
        this.receipGroupPrefix = receipGroupPrefix;
        MATERIALS.add(this);
    }

    public ComplexMaterial init(
        BlockRegistry blocksRegistry,
        ItemRegistry itemsRegistry
    ) {
        initTags();

        final BlockBehaviour.Properties blockSettings = getBlockSettings();
        final Item.Properties itemSettings = getItemSettings(itemsRegistry);
        initDefault(blockSettings, itemSettings);

        getBlockEntries().forEach(entry -> {
            Block block = entry.init(this, blockSettings, blocksRegistry);
            blocks.put(entry.getSuffix(), block);
        });

        getItemEntries().forEach(entry -> {
            Item item = entry.init(this, itemSettings, itemsRegistry);
            items.put(entry.getSuffix(), item);
        });

        initFlammable(FlammableBlockRegistry.getDefaultInstance());
        return this;
    }

    private void provideRecipes(RecipeBuilder.Context context) {
        initDefaultRecipes();

        getRecipeEntries().forEach(entry -> {
            entry.init(context, this);
        });
    }

    public static void provideAllRecipes(
        RecipeBuilder.Context context,
        ModCore modCore
    ) {
        MATERIALS.stream()
            .filter(m -> m.C == modCore)
            .forEach(material -> material.provideRecipes(context));
    }

    protected abstract void initDefault(
        BlockBehaviour.Properties blockSettings,
        Item.Properties itemSettings
    );

    protected void initTags() {}

    protected void initDefaultRecipes() {}

    protected void initFlammable(FlammableBlockRegistry registry) {}

    protected void addBlockTag(TagKey<Block> tag) {
        String key = tag.location().getPath().replace(getBaseName() + "_", "");
        blockTags.put(key, tag);
    }

    protected void addItemTag(TagKey<Item> tag) {
        String key = tag.location().getPath().replace(getBaseName() + "_", "");
        itemTags.put(key, tag);
    }

    @Nullable
    public TagKey<Block> getBlockTag(String key) {
        return blockTags.get(key);
    }

    @Nullable
    public TagKey<Item> getItemTag(String key) {
        return itemTags.get(key);
    }

    @Nullable
    public Block getBlock(String key) {
        return blocks.get(key);
    }

    @Nullable
    public Block ifBlockPresent(String key, Consumer<Block> runIfPresent) {
        final Block block = blocks.get(key);
        if (block != null) {
            runIfPresent.accept(block);
        }
        return block;
    }

    @Nullable
    public <M extends ComplexMaterial> Block ifBlockPresent(
        MaterialSlot<M> slot,
        Consumer<Block> runIfPresent
    ) {
        final Block block = blocks.get(slot.suffix);
        if (block != null) {
            runIfPresent.accept(block);
        }
        return block;
    }

    @Nullable
    public <M extends ComplexMaterial> Block getBlock(MaterialSlot<M> key) {
        return blocks.get(key.suffix);
    }

    @Nullable
    public <M extends ComplexMaterial> BlockItem getBlockItem(
        MaterialSlot<M> key
    ) {
        return getBlockItem(key.suffix);
    }

    @Nullable
    public BlockItem getBlockItem(String key) {
        final Block bl = blocks.get(key);
        return bl != null && bl.asItem() instanceof BlockItem bi ? bi : null;
    }

    @Nullable
    public Item getItem(String key) {
        return items.get(key);
    }

    @Nullable
    public <M extends ComplexMaterial> Item getItem(MaterialSlot<M> slot) {
        return items.get(slot.suffix);
    }

    @Nullable
    public Item ifItemPresent(String key, Consumer<Item> runIfPresent) {
        final Item item = items.get(key);
        if (item != null) {
            runIfPresent.accept(item);
        }
        return item;
    }

    @Nullable
    public <M extends ComplexMaterial> Item ifItemPresent(
        MaterialSlot<M> slot,
        Consumer<Item> runIfPresent
    ) {
        final Item item = items.get(slot.suffix);
        if (item != null) {
            runIfPresent.accept(item);
        }
        return item;
    }

    protected abstract BlockBehaviour.Properties getBlockSettings();

    protected Item.Properties getItemSettings(ItemRegistry registry) {
        return registry.createDefaultItemSettings();
    }

    private Collection<BlockEntry> getBlockEntries() {
        List<BlockEntry> result = Lists.newArrayList(defaultBlockEntries);
        List<BlockEntry> entries = BLOCK_ENTRIES.get(this.getMaterialID());
        if (entries != null) {
            result.addAll(entries);
        }
        return result;
    }

    private Collection<ItemEntry> getItemEntries() {
        List<ItemEntry> result = Lists.newArrayList(defaultItemEntries);
        List<ItemEntry> entries = ITEM_ENTRIES.get(this.getMaterialID());
        if (entries != null) {
            result.addAll(entries);
        }
        return result;
    }

    private Collection<RecipeEntry> getRecipeEntries() {
        List<RecipeEntry> result = Lists.newArrayList(defaultRecipeEntries);
        List<RecipeEntry> entries = RECIPE_ENTRIES.get(this.getMaterialID());
        if (entries != null) {
            result.addAll(entries);
        }
        return result;
    }

    public String getBaseName() {
        return baseName;
    }

    public abstract Identifier getMaterialID();

    public Collection<Block> getBlocks() {
        return blocks.values();
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    protected void addBlockEntry(BlockEntry entry) {
        defaultBlockEntries.add(entry);
    }

    protected void replaceOrAddBlockEntry(BlockEntry entry) {
        int pos = defaultBlockEntries.indexOf(entry);
        if (pos >= 0) defaultBlockEntries.remove(entry);

        addBlockEntry(entry);
    }

    protected void addItemEntry(ItemEntry entry) {
        defaultItemEntries.add(entry);
    }

    protected void addRecipeEntry(RecipeEntry entry) {
        defaultRecipeEntries.add(entry);
    }

    public static void addBlockEntry(
        Identifier materialName,
        BlockEntry entry
    ) {
        List<BlockEntry> entries = BLOCK_ENTRIES.get(materialName);
        if (entries == null) {
            entries = Lists.newArrayList();
            BLOCK_ENTRIES.put(materialName, entries);
        }
        entries.add(entry);
    }

    public static void addItemEntry(Identifier materialName, ItemEntry entry) {
        List<ItemEntry> entries = ITEM_ENTRIES.get(materialName);
        if (entries == null) {
            entries = Lists.newArrayList();
            ITEM_ENTRIES.put(materialName, entries);
        }
        entries.add(entry);
    }

    public static void addRecipeEntry(
        Identifier materialName,
        RecipeEntry entry
    ) {
        List<RecipeEntry> entries = RECIPE_ENTRIES.get(materialName);
        if (entries == null) {
            entries = Lists.newArrayList();
            RECIPE_ENTRIES.put(materialName, entries);
        }
        entries.add(entry);
    }

    public static Collection<ComplexMaterial> getAllMaterials() {
        return MATERIALS;
    }
}
