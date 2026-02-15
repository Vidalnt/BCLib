package org.betterx.bclib.items.boat;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import org.betterx.bclib.interfaces.ItemModelProvider;

public class BaseBoatItem
    extends BoatItem
    implements CustomBoatTypeOverride, ItemModelProvider
{

    BoatTypeOverride customType;

    public BaseBoatItem(
        boolean hasChest,
        BoatTypeOverride type,
        Item.Properties properties
    ) {
        super(
            hasChest ? EntityType.OAK_CHEST_BOAT : EntityType.OAK_BOAT,
            properties
        );
        bcl_setCustomType(type);
    }

    @Override
    public void bcl_setCustomType(BoatTypeOverride type) {
        customType = type;
    }

    @Override
    public BoatTypeOverride bcl_getCustomType() {
        return customType;
    }
}
