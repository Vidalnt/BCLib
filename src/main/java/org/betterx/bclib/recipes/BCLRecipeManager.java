package org.betterx.bclib.recipes;

import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.betterx.bclib.BCLib;
import org.betterx.wover.config.api.DatapackConfigs;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class BCLRecipeManager {

    public static final Identifier RECIPES_CONFIG_FILE = BCLib.C.id(
        "recipes.json"
    );

    public static <
        C extends RecipeInput,
        S extends RecipeSerializer<T>,
        T extends Recipe<C>
    > S registerSerializer(String modID, String id, S serializer) {
        return Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            modID + ":" + id,
            serializer
        );
    }

    public static <C extends RecipeInput, T extends Recipe<C>> RecipeType<
        T
    > registerType(String modID, String type) {
        Identifier recipeTypeId = Identifier.fromNamespaceAndPath(modID, type);
        return Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            recipeTypeId,
            new RecipeType<T>() {
                public String toString() {
                    return type;
                }
            }
        );
    }

    public static RecipeBookCategory registerCategory(Identifier location) {
        return Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            location,
            new RecipeBookCategory()
        );
    }

    public static boolean exists(ItemLike item) {
        if (item instanceof Block) {
            return (
                BuiltInRegistries.BLOCK.getKey((Block) item) !=
                BuiltInRegistries.BLOCK.getDefaultKey()
            );
        } else {
            return (
                item != Items.AIR &&
                BuiltInRegistries.ITEM.getKey(item.asItem()) !=
                BuiltInRegistries.ITEM.getDefaultKey()
            );
        }
    }

    private static final HashSet<Identifier> disabledRecipes = new HashSet<>();

    private static void clearRecipeConfig() {
        disabledRecipes.clear();
    }

    private static void processRecipeConfig(
        @NotNull Identifier sourceId,
        @NotNull JsonObject root
    ) {
        if (root.has("disable")) {
            root
                .getAsJsonArray("disable")
                .asList()
                .stream()
                .map(el -> Identifier.tryParse(el.getAsString()))
                .filter(id -> id != null)
                .forEach(disabledRecipes::add);
        }
    }

    @ApiStatus.Internal
    public static RecipeMap removeDisabledRecipes(
        ResourceManager manager,
        RecipeMap loadedRecipes
    ) {
        List<RecipeHolder<?>> recipeHolders = new LinkedList<>(
            loadedRecipes.values()
        );
        clearRecipeConfig();
        DatapackConfigs.instance().runForResource(
            manager,
            RECIPES_CONFIG_FILE,
            BCLRecipeManager::processRecipeConfig
        );

        for (Identifier id : disabledRecipes) {
            BCLib.LOGGER.verbose("Disabling Recipe: {}", id);

            recipeHolders.removeIf(holder ->
                holder.id().identifier().equals(id)
            );
        }

        return RecipeMap.create(recipeHolders);
    }
}
