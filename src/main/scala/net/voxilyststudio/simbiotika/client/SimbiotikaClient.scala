package net.voxilyststudio.simbiotika.client

import net.minecraft.client.renderer.entity.{EntityRendererProvider, ThrownItemRenderer}
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.voxilyststudio.simbiotika.Simbiotika
import net.voxilyststudio.simbiotika.entity.SymbioteFragmentEntity
import net.voxilyststudio.simbiotika.registry.ModEntities

@EventBusSubscriber(modid = Simbiotika.ModId, bus = EventBusSubscriber.Bus.MOD, value = Array(Dist.CLIENT))
object SimbiotikaClient {
  @SubscribeEvent def registerRenderers(event: EntityRenderersEvent.RegisterRenderers): Unit =
    event.registerEntityRenderer(ModEntities.SYMBIOTE_FRAGMENT.get(),
      (context: EntityRendererProvider.Context) => new ThrownItemRenderer[SymbioteFragmentEntity](context))
}
