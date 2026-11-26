package net.voxilyststudio.simbiotika.compat

import net.minecraft.core.registries.{BuiltInRegistries, Registries}
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.{AttributeModifier, Attributes}
import net.minecraft.world.item.Item
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.voxilyststudio.simbiotika.attachment.{ModAttachments, SymbiotePlayerData}
import net.voxilyststudio.simbiotika.world.GlobalSymbioteSavedData

object SuperheroCompatGate {
  val SuitTag: TagKey[Item] = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("simbiotika", "superhero_suits"))
  val ScaleModifierId: ResourceLocation = ResourceLocation.fromNamespaceAndPath("simbiotika", "absorbed_superhero_suit_scale")
  private val SuitNamespaces = Set(
    "heroesexpansion", "fiskheroes", "lucraftcore", "speedsterheroes", "ironman", "spiderman", "marvel",
    "dcuniverse", "superheroes", "infinity", "project_superhuman", "palladium", "pantheon", "hero_academia",
    "omnitrix", "ben10", "kryptonian", "lanterns", "avengers", "xmen", "mutants", "webshooting", "spidey",
    "symbiotes_legacy", "superhero"
  )
  private val SuitPathKeywords = Seq("hero_suit", "spidey", "super_suit")
  private val ArmorSlots = Seq(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

  def isSuitStack(stack: net.minecraft.world.item.ItemStack): Boolean = {
    if (stack.isEmpty) false
    else if (stack.is(SuitTag)) true
    else {
      val key = BuiltInRegistries.ITEM.getKey(stack.getItem)
      SuitNamespaces.contains(key.getNamespace) && SuitPathKeywords.exists(keyword => key.getPath.contains(keyword))
    }
  }
  def isWearingSuit(entity: LivingEntity): Boolean = ArmorSlots.exists(slot => isSuitStack(entity.getItemBySlot(slot)))
  def isSymbioteCarrier(entity: net.minecraft.world.entity.Entity): Boolean = {
    val hasAttachment = entity.hasData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()) &&
      entity.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()).symbioteId.nonEmpty
    hasAttachment || BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType).getNamespace == "simbiotika"
  }
  def mayMerge(player: Player): Boolean = {
    if (!isWearingSuit(player)) true
    else { player.displayClientMessage(Component.literal("You Cant Do This"), true); false }
  }
  def merge(player: Player, symbioteId: String): Boolean = {
    if (!mayMerge(player) || player.level().isClientSide || !player.isInstanceOf[Player]) false
    else player.level() match {
      case serverLevel: ServerLevel =>
        val ledger = GlobalSymbioteSavedData.get(serverLevel)
        if (!ledger.claim(symbioteId, player.getUUID.toString)) false
        else {
          val old = if (player.hasData(ModAttachments.SYMBIOTE_PLAYER_DATA.get())) player.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()) else SymbiotePlayerData.Empty
          player.setData(ModAttachments.SYMBIOTE_PLAYER_DATA.get(), old.copy(symbioteId = symbioteId, morphed = false))
          true
        }
      case _ => false
    }
  }
  def mergeFromCarrier(player: Player, symbioteId: String, currentCarrierId: String): Boolean = {
    if (!mayMerge(player) || player.level().isClientSide) false
    else player.level() match {
      case serverLevel: ServerLevel =>
        val ledger = GlobalSymbioteSavedData.get(serverLevel)
        val nextCarrierId = player.getUUID.toString
        if (!ledger.transfer(symbioteId, currentCarrierId, nextCarrierId)) false
        else {
          val old = if (player.hasData(ModAttachments.SYMBIOTE_PLAYER_DATA.get())) player.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()) else SymbiotePlayerData.Empty
          player.setData(ModAttachments.SYMBIOTE_PLAYER_DATA.get(), old.copy(symbioteId = symbioteId, morphed = false))
          true
        }
      case _ => false
    }
  }
  def absorbSuit(player: Player): Boolean = {
    if (!isWearingSuit(player) || !player.hasData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()) || player.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get()).symbioteId.isEmpty) false
    else {
      val data = player.getData(ModAttachments.SYMBIOTE_PLAYER_DATA.get())
      player.setData(ModAttachments.SYMBIOTE_PLAYER_DATA.get(), data.copy(absorbedSuit = true))
      applyAbsorbedSuitScale(player)
      true
    }
  }
  def applyAbsorbedSuitScale(entity: LivingEntity): Unit = {
    val attribute = entity.getAttribute(Attributes.SCALE)
    if (attribute != null) attribute.addOrUpdateTransientModifier(
      new AttributeModifier(ScaleModifierId, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
  }
}
