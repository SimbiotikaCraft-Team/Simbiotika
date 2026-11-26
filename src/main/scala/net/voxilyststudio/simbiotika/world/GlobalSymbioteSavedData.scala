package net.voxilyststudio.simbiotika.world

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.server.level.ServerLevel
import scala.jdk.CollectionConverters.*

/** World-wide persistent ledger. Symbiote UUIDs are unique keys; kit issuance is capped at five. */
final class GlobalSymbioteSavedData extends SavedData {
  private val carriers = scala.collection.mutable.Map.empty[String, String]
  private var kitsIssued = 0

  def claim(symbioteId: String, carrierId: String): Boolean = synchronized {
    if (symbioteId.isBlank || carrierId.isBlank || carriers.contains(symbioteId)) false
    else { carriers(symbioteId) = carrierId; setDirty(); true }
  }
  def transfer(symbioteId: String, expectedCarrierId: String, nextCarrierId: String): Boolean = synchronized {
    if (carriers.get(symbioteId).contains(expectedCarrierId)) {
      carriers(symbioteId) = nextCarrierId; setDirty(); true
    } else false
  }
  def release(symbioteId: String, expectedCarrierId: String): Boolean = synchronized {
    if (carriers.get(symbioteId).contains(expectedCarrierId)) {
      carriers.remove(symbioteId); setDirty(); true
    } else false
  }
  def currentCarrier(symbioteId: String): Option[String] = synchronized { carriers.get(symbioteId) }
  def symbioteCount: Int = synchronized { carriers.size }
  def issuedKits: Int = synchronized { kitsIssued }
  def issueSlayerKit(): Boolean = synchronized {
    if (kitsIssued >= GlobalSymbioteSavedData.MaxSlayerKits) false
    else { kitsIssued += 1; setDirty(); true }
  }

  override def save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag = {
    val entries = new CompoundTag()
    carriers.foreach { case (id, carrier) => entries.putString(id, carrier) }
    tag.put("carriers", entries)
    tag.putInt("slayer_kits_issued", kitsIssued)
    tag
  }
}

object GlobalSymbioteSavedData {
  final val DataName = "simbiotika_global"
  final val MaxSlayerKits = 5
  val Factory = new SavedData.Factory[GlobalSymbioteSavedData](
    () => new GlobalSymbioteSavedData(),
    (tag: CompoundTag, _: HolderLookup.Provider) => {
      val data = new GlobalSymbioteSavedData()
      val entries = tag.getCompound("carriers")
      entries.getAllKeys.asScala.foreach(key => data.carriers(key) = entries.getString(key))
      data.kitsIssued = math.max(0, math.min(MaxSlayerKits, tag.getInt("slayer_kits_issued")))
      data
    }
  )
  def get(level: ServerLevel): GlobalSymbioteSavedData =
    level.getServer.overworld().getDataStorage.computeIfAbsent(Factory, DataName)
}
