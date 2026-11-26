package net.voxilyststudio.simbiotika.ritual

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.{ItemStack, Items}
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import net.voxilyststudio.simbiotika.registry.{ModFluids, ModItems}
import net.voxilyststudio.simbiotika.world.GlobalSymbioteSavedData

final class SlayerRitualHandler {
  @SubscribeEvent def onEntityTick(event: EntityTickEvent.Post): Unit = event.getEntity match {
    case chicken: Chicken if !chicken.level().isClientSide && inBlackGoo(chicken) =>
      val level = chicken.level().asInstanceOf[ServerLevel]
      val nearby = level.getEntitiesOfClass(classOf[Player], new AABB(chicken.blockPosition()).inflate(4.0))
      val iterator = nearby.iterator()
      while (iterator.hasNext && chicken.isAlive) {
        val player = iterator.next()
        if (hasOffering(player) && GlobalSymbioteSavedData.get(level).issueSlayerKit()) {
          consumeOffering(player)
          chicken.discard()
          reward(player, level)
        }
      }
    case _ => ()
  }

  private def inBlackGoo(chicken: Chicken): Boolean = {
    val fluid = chicken.level().getFluidState(chicken.blockPosition()).getType
    fluid == ModFluids.BLACK_GOO_SOURCE.get() || fluid == ModFluids.BLACK_GOO_FLOWING.get()
  }
  private def hasOffering(player: Player): Boolean =
    player.getItemBySlot(EquipmentSlot.HEAD).is(Items.NETHERITE_HELMET) &&
    player.getItemBySlot(EquipmentSlot.CHEST).is(Items.NETHERITE_CHESTPLATE) &&
    player.getItemBySlot(EquipmentSlot.LEGS).is(Items.NETHERITE_LEGGINGS) &&
    player.getItemBySlot(EquipmentSlot.FEET).is(Items.NETHERITE_BOOTS) &&
    player.getMainHandItem.is(Items.NETHERITE_SWORD)
  private def consumeOffering(player: Player): Unit = {
    player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY)
    player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY)
    player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY)
    player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY)
    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
  }
  private def reward(player: Player, level: ServerLevel): Unit = {
    val rewards = Seq(ModItems.SLAYER_SWORD.get(), ModItems.SLAYER_HELMET.get(), ModItems.SLAYER_CHESTPLATE.get(),
      ModItems.SLAYER_LEGGINGS.get(), ModItems.SLAYER_BOOTS.get())
    rewards.foreach { item =>
      val stack = new ItemStack(item)
      if (!player.getInventory.add(stack)) {
        val drop = new ItemEntity(level, player.getX, player.getY + 0.5, player.getZ, stack)
        drop.setDefaultPickUpDelay()
        level.addFreshEntity(drop)
      }
    }
  }
}
