package net.voxilyststudio.simbiotika.registry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Util
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.{BaseFlowingFluid, FluidType}
import net.neoforged.neoforge.registries.{DeferredRegister, NeoForgeRegistries}
import net.voxilyststudio.simbiotika.Simbiotika

object ModFluids {
  private val FLUIDS = DeferredRegister.create(Registries.FLUID, Simbiotika.ModId)
  private val TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Simbiotika.ModId)
  val BLACK_GOO_TYPE = TYPES.register("black_goo", (id: ResourceLocation) => new FluidType(
    FluidType.Properties.create()
      .descriptionId(Util.makeDescriptionId("fluid", id))
      .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
      .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
      .canSwim(false).canDrown(true).density(1200).viscosity(1800)))
  val BLACK_GOO_SOURCE = FLUIDS.register("black_goo", () => new BaseFlowingFluid.Source(PROPERTIES))
  val BLACK_GOO_FLOWING = FLUIDS.register("flowing_black_goo", () => new BaseFlowingFluid.Flowing(PROPERTIES))
  lazy val PROPERTIES: BaseFlowingFluid.Properties = new BaseFlowingFluid.Properties(
    BLACK_GOO_TYPE, BLACK_GOO_SOURCE, BLACK_GOO_FLOWING)
    .bucket(() => ModItems.BLACK_GOO_BUCKET.get())
    .block(() => ModBlocks.BLACK_GOO_BLOCK.get())
  def register(bus: IEventBus): Unit = { TYPES.register(bus); FLUIDS.register(bus) }
}
