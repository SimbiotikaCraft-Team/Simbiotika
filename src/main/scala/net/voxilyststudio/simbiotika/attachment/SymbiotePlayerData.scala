package net.voxilyststudio.simbiotika.attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.{ByteBufCodecs, StreamCodec}

final case class SymbiotePlayerData(symbioteId: String, morphed: Boolean, absorbedSuit: Boolean)
object SymbiotePlayerData {
  val Empty: SymbiotePlayerData = SymbiotePlayerData("", false, false)
  val CODEC: Codec[SymbiotePlayerData] = RecordCodecBuilder.create(instance => instance.group(
    Codec.STRING.fieldOf("symbiote_id").forGetter((d: SymbiotePlayerData) => d.symbioteId),
    Codec.BOOL.fieldOf("morphed").forGetter((d: SymbiotePlayerData) => d.morphed),
    Codec.BOOL.fieldOf("absorbed_suit").forGetter((d: SymbiotePlayerData) => d.absorbedSuit)
  ).apply(instance, (id: String, morphed: java.lang.Boolean, absorbed: java.lang.Boolean) =>
    SymbiotePlayerData(id, morphed.booleanValue(), absorbed.booleanValue())))
  val STREAM_CODEC: StreamCodec[RegistryFriendlyByteBuf, SymbiotePlayerData] = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8, (d: SymbiotePlayerData) => d.symbioteId,
    ByteBufCodecs.BOOL, (d: SymbiotePlayerData) => d.morphed,
    ByteBufCodecs.BOOL, (d: SymbiotePlayerData) => d.absorbedSuit,
    (id: String, morphed: java.lang.Boolean, absorbed: java.lang.Boolean) =>
      SymbiotePlayerData(id, morphed.booleanValue(), absorbed.booleanValue())
  )
}
