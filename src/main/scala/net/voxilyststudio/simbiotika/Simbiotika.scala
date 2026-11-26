package net.voxilyststudio.simbiotika

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.voxilyststudio.simbiotika.attachment.ModAttachments
import net.voxilyststudio.simbiotika.compat.{CombatHooks, SuperheroCompatGate}
import net.voxilyststudio.simbiotika.registry.{ModBlocks, ModEntities, ModFluids, ModItems}
import net.voxilyststudio.simbiotika.ritual.SlayerRitualHandler

@Mod(Simbiotika.ModId)
final class Simbiotika(modBus: IEventBus) {
  ModItems.register(modBus)
  ModBlocks.register(modBus)
  ModFluids.register(modBus)
  ModEntities.register(modBus)
  ModAttachments.register(modBus)
  NeoForge.EVENT_BUS.register(new SlayerRitualHandler())
  NeoForge.EVENT_BUS.register(new CombatHooks())
  NeoForge.EVENT_BUS.addListener((event: PlayerEvent.PlayerLoggedInEvent) => {
    val player = event.getEntity
    if (player.hasData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()) &&
        player.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()).absorbedSuit)
      SuperheroCompatGate.applyAbsorbedSuitScale(player)
  })
  NeoForge.EVENT_BUS.addListener((event: PlayerTickEvent.Post) => {
    val player = event.getEntity
    if (!player.level().isClientSide && player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null)
      player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
        .removeModifier(CombatHooks.SLAYER_DAMAGE_MODIFIER)
  })
}

object Simbiotika { final val ModId = "simbiotika" }
