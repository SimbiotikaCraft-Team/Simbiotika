package net.voxilyststudio.simbiotika.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.{Entity, EntityType, MoverType}
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.server.level.ServerLevel
import net.voxilyststudio.simbiotika.registry.ModEntities
import net.voxilyststudio.simbiotika.registry.ModItems
import net.voxilyststudio.simbiotika.world.GlobalSymbioteSavedData

final class SymbioteFragmentEntity(entityType: EntityType[? <: SymbioteFragmentEntity], initialLevel: Level)
    extends Entity(entityType, initialLevel) with ItemSupplier {
  private var symbioteId = ""
  private var previousCarrierId = ""
  private var swordHits = 0
  setNoGravity(true)
  override def getItem: ItemStack = new ItemStack(ModItems.BLACK_GOO_BUCKET.get())
  override def defineSynchedData(builder: SynchedEntityData.Builder): Unit = ()
  override def tick(): Unit = {
    super.tick()
    if (level().isClientSide) return
    val motion = net.minecraft.world.phys.Vec3(0.0, 0.035, 0.0)
    setDeltaMovement(motion)
    move(MoverType.SELF, motion)
  }
  override def isPickable: Boolean = isAlive
  override def isInvulnerableTo(source: DamageSource): Boolean = !SymbioteFragmentEntity.isSlayerHit(source)
  override def hurt(source: DamageSource, amount: Float): Boolean = {
    if (level().isClientSide || !SymbioteFragmentEntity.isSlayerHit(source)) return false
    swordHits += 1
    if (swordHits >= 5) {
      level() match {
        case server: ServerLevel if symbioteId.nonEmpty =>
          GlobalSymbioteSavedData.get(server).release(symbioteId, getUUID.toString)
        case _ => ()
      }
      discard()
    }
    true
  }
  def setSymbioteId(id: String, carrierId: String): Unit = { symbioteId = id; previousCarrierId = carrierId }
  def hitCount: Int = swordHits
  override def addAdditionalSaveData(tag: CompoundTag): Unit = {
    tag.putString("symbiote_id", symbioteId); tag.putString("previous_carrier", previousCarrierId); tag.putInt("sword_hits", swordHits)
  }
  override def readAdditionalSaveData(tag: CompoundTag): Unit = {
    symbioteId = tag.getString("symbiote_id"); previousCarrierId = tag.getString("previous_carrier")
    swordHits = math.max(0, math.min(4, tag.getInt("sword_hits")))
  }
}
object SymbioteFragmentEntity {
  def spawnFromCarrier(level: ServerLevel, x: Double, y: Double, z: Double,
                       symbioteId: String, previousCarrierId: String): Option[SymbioteFragmentEntity] = {
    val entity = ModEntities.SYMBIOTE_FRAGMENT.get().create(level)
    if (entity == null) return None
    entity.setPos(x, y, z)
    entity.setSymbioteId(symbioteId, previousCarrierId)
    val ledger = GlobalSymbioteSavedData.get(level)
    if (!ledger.transfer(symbioteId, previousCarrierId, entity.getUUID.toString)) None
    else if (level.addFreshEntity(entity)) Some(entity)
    else { ledger.transfer(symbioteId, entity.getUUID.toString, previousCarrierId); None }
  }
  private def isSlayerHit(source: DamageSource): Boolean = {
    val attacker = source.getEntity
    attacker != null && attacker.isInstanceOf[net.minecraft.world.entity.LivingEntity] &&
      attacker.asInstanceOf[net.minecraft.world.entity.LivingEntity].getMainHandItem.is(ModItems.SLAYER_SWORD.get())
  }
}
