package net.voxilyststudio.simbiotika.attachment

import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.{DeferredRegister, NeoForgeRegistries}
import net.neoforged.bus.api.IEventBus
import net.voxilyststudio.simbiotika.Simbiotika

object ModAttachments {
  private val TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Simbiotika.ModId)
  val SYMBIOTE_PLAYER_DATA = TYPES.register("symbiote_player_data", () =>
    AttachmentType.builder[SymbiotePlayerData](() => SymbiotePlayerData.Empty)
      .serialize(SymbiotePlayerData.CODEC)
      .sync(SymbiotePlayerData.STREAM_CODEC)
      .build())
  def register(bus: IEventBus): Unit = TYPES.register(bus)
}
