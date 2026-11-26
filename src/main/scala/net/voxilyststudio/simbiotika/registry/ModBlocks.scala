package net.voxilyststudio.simbiotika.registry

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.{LiquidBlock, Block}
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.voxilyststudio.simbiotika.Simbiotika

object ModBlocks {
  private val BLOCKS = DeferredRegister.create(Registries.BLOCK, Simbiotika.ModId)
  val BLACK_GOO_BLOCK = BLOCKS.register("black_goo", () => new LiquidBlock(
    ModFluids.BLACK_GOO_SOURCE.get(), BlockBehaviour.Properties.of().noCollission().strength(100.0F).replaceable()))
  def register(bus: IEventBus): Unit = BLOCKS.register(bus)
}
