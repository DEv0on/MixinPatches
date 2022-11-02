@file:Mixin(SoulNetwork::class)

package net.remodded.mixinpatches.mixins.common

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import WayofTime.bloodmagic.core.data.SoulNetwork
import WayofTime.bloodmagic.util.helper.PlayerHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect
import java.lang.ref.WeakReference
import java.util.*

@Mixin(SoulNetwork::class)
class SoulNetworkMixin {

    @Shadow lateinit var playerId: UUID
    @Shadow lateinit var parent: BMWorldSavedData
    lateinit var cachedPlayer: WeakReference<EntityPlayer>

    @Overwrite
    fun getPlayer(): EntityPlayer? {
        if (cachedPlayer == null)
            cachedPlayer = WeakReference(PlayerHelper.getPlayerFromUUID(playerId))
        return cachedPlayer.get()
    }

    @Overwrite
    fun setParent(parent: BMWorldSavedData): SoulNetwork {
        return this as SoulNetwork
    }

    @Overwrite
    fun getCachedPlayer(): EntityPlayer? {
        return cachedPlayer.get()
    }

    @Overwrite
    fun getCurrentEssence(): Int {
        val data = Core.networkData.get(playerId)!!
        return Integer.parseInt(data.substring(0, data.indexOf('|')))
    }

    @Overwrite
    fun setCurrentEssence(currentEssence: Int): SoulNetwork {
        val data = Core.networkData.get(playerId)!!
        Core.networkData.put(playerId, currentEssence.toString() + data.substring(data.indexOf('|')))
        return this as SoulNetwork
    }

    @Overwrite
    fun getOrbTier(): Int {
        val data = Core.networkData.get(playerId)!!
        return data.substring(data.indexOf('|') + 1).toInt()
    }

    @Overwrite
    fun setOrbTier(orbTier: Int): SoulNetwork {
        val data = Core.networkData.get(playerId)!!
        Core.networkData.put(playerId, data.substring(0, data.indexOf('|') + 1) + orbTier)
        return this as SoulNetwork
    }

    @Overwrite
    fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound()
    }

    @Overwrite
    fun deserializeNBT(nbt: NBTTagCompound) {
    }
}

@Overwrite
fun newEmpty(uuid: UUID): SoulNetwork {
    val network: SoulNetwork = Class.forName("WayofTime.bloodmagic.core.data.SoulNetwork").newInstance() as SoulNetwork
    (network as SoulNetworkAccessor).setPlayerID(uuid)
    network.parent = Core.worldDataInstance
    if (!Core.networkData.containsKey(uuid)) Core.networkData.put(uuid, "0|0")
    return network
}

@Overwrite
fun fromNBT(tagCompound: NBTTagCompound): SoulNetwork? {
    return null
}
