package net.voxilyststudio.simbiotika.item

import net.minecraft.world.item.{Item, SwordItem, Tiers}

final class SymbioteSlayerSwordItem(properties: Item.Properties)
    extends SwordItem(Tiers.DIAMOND, properties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F)))
