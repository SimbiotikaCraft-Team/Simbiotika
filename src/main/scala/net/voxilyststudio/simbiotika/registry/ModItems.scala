package net.voxilyststudio.simbiotika.registry

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.{ArmorItem, ArmorMaterials, BucketItem, Item, Items}
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.voxilyststudio.simbiotika.Simbiotika
import net.voxilyststudio.simbiotika.item.SymbioteSlayerSwordItem

object ModItems {
  private val ITEMS = DeferredRegister.create(Registries.ITEM, Simbiotika.ModId)
  val BLACK_GOO_BUCKET = ITEMS.register("black_goo_bucket", () =>
    new BucketItem(ModFluids.BLACK_GOO_SOURCE.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)))
  val SLAYER_SWORD = ITEMS.register("symbiote_slayer_sword", () => new SymbioteSlayerSwordItem(new Item.Properties().durability(1561)))
  val SLAYER_HELMET = ITEMS.register("slayer_helmet", () => new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties()))
  val SLAYER_CHESTPLATE = ITEMS.register("slayer_chestplate", () => new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties()))
  val SLAYER_LEGGINGS = ITEMS.register("slayer_leggings", () => new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties()))
  val SLAYER_BOOTS = ITEMS.register("slayer_boots", () => new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties()))
  def register(bus: IEventBus): Unit = ITEMS.register(bus)
}
