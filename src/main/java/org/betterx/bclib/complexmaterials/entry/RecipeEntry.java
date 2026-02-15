package org.betterx.bclib.complexmaterials.entry;

import net.minecraft.resources.Identifier;
import org.betterx.bclib.complexmaterials.ComplexMaterial;
import org.betterx.bclib.interfaces.TriConsumer;
import org.betterx.wover.recipe.api.RecipeBuilder;

public class RecipeEntry extends ComplexMaterialEntry {

    public interface RecipeConsumer
        extends
            TriConsumer<RecipeBuilder.Context, ComplexMaterial, Identifier> {}

    final RecipeConsumer initFunction;

    public RecipeEntry(String suffix, RecipeConsumer initFunction) {
        super(suffix);
        this.initFunction = initFunction;
    }

    public void init(RecipeBuilder.Context context, ComplexMaterial material) {
        initFunction.accept(
            context,
            material,
            material.C.mk(getName(material.getBaseName()))
        );
    }
}
