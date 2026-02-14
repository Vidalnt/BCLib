package org.betterx.bclib.client.models;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.resources.Identifier;

public class CustomModelData {

    private static final Set<Identifier> TRANSPARENT_EMISSION =
        Sets.newConcurrentHashSet();

    public static void clear() {
        TRANSPARENT_EMISSION.clear();
    }

    public static void addTransparent(Identifier blockID) {
        TRANSPARENT_EMISSION.add(blockID);
    }

    public static boolean isTransparentEmissive(Identifier rawLocation) {
        String name = rawLocation
            .getPath()
            .replace("materialmaps/block/", "")
            .replace(".json", "");
        return TRANSPARENT_EMISSION.contains(
            Identifier.fromNamespaceAndPath(rawLocation.getNamespace(), name)
        );
    }
}
