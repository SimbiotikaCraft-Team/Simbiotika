package net.voxilyststudio.simbiotika.registry

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.{EntityType, MobCategory}
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.voxilyststudio.simbiotika.Simbiotika
import net.voxilyststudio.simbiotika.entity.SymbioteFragmentEntity

object ModEntities {
  private val TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Simbiotika.ModId)
  val SYMBIOTE_FRAGMENT = TYPES.register("symbiote_fragment", () => EntityType.Builder.of(
    (kind: EntityType[? <: SymbioteFragmentEntity], level: Level) => new SymbioteFragmentEntity(kind, level), MobCategory.MISC)
    .sized(0.45F, 0.45F).clientTrackingRange(8).updateInterval(2).build("simbiotika:symbiote_fragment"))
  def register(bus: IEventBus): Unit = TYPES.register(bus)
}
