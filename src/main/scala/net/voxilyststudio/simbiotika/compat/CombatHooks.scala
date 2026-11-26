package net.voxilyststudio.simbiotika.compat

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.{AttributeModifier, Attributes}
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.voxilyststudio.simbiotika.registry.ModItems

final class CombatHooks {
  @SubscribeEvent def onAttack(event: AttackEntityEvent): Unit = {
    val player = event.getEntity
    if (player.getMainHandItem.is(ModItems.SLAYER_SWORD.get()) && SuperheroCompatGate.isSymbioteCarrier(event.getTarget)) {
      val attack = player.getAttribute(Attributes.ATTACK_DAMAGE)
      if (attack != null) attack.addOrUpdateTransientModifier(new AttributeModifier(
        CombatHooks.SLAYER_DAMAGE_MODIFIER, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
    }
  }
  @SubscribeEvent def clearTemporaryDamage(event: PlayerTickEvent.Post): Unit = {
    val player: Player = event.getEntity
    val attribute = player.getAttribute(Attributes.ATTACK_DAMAGE)
    if (attribute != null) attribute.removeModifier(CombatHooks.SLAYER_DAMAGE_MODIFIER)
  }
}
object CombatHooks {
  val SLAYER_DAMAGE_MODIFIER: ResourceLocation = ResourceLocation.fromNamespaceAndPath("simbiotika", "slayer_bonus_damage")
}
