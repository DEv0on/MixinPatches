@file:Suppress("CAST_NEVER_SUCCEEDS", "SENSELESS_COMPARISON")
@file:Mixin(SoulNetwork::class)

package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import WayofTime.bloodmagic.core.data.SoulNetwork
import WayofTime.bloodmagic.util.helper.PlayerHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.gen.Invoker
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.lang.ref.WeakReference
import java.util.*

@Mixin(SoulNetwork::class)
class SoulNetworkMixin {

    @Shadow lateinit var playerId: UUID
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
        val data = Core.networkData[playerId]!!
        return Integer.parseInt(data.substring(0, data.indexOf('|')))
    }

    @Overwrite
    fun setCurrentEssence(currentEssence: Int): SoulNetwork {
        val data = Core.networkData[playerId]!!
        Core.networkData[playerId] = currentEssence.toString() + data.substring(data.indexOf('|'))
        return this as SoulNetwork
    }

    @Overwrite
    fun getOrbTier(): Int {
        val data = Core.networkData[playerId]!!
        return data.substring(data.indexOf('|') + 1).toInt()
    }

    @Overwrite
    fun setOrbTier(orbTier: Int): SoulNetwork {
        val data = Core.networkData[playerId]!!
        Core.networkData[playerId] = data.substring(0, data.indexOf('|') + 1) + orbTier
        return this as SoulNetwork
    }

    @Inject(method = ["serializeNBT"], at = [At("HEAD")], cancellable = true)
    fun serializeNBT(callbackInfoReturnable: CallbackInfoReturnable<NBTTagCompound>) {
        callbackInfoReturnable.returnValue = null
        callbackInfoReturnable.cancel()
    }

    @Inject(method = ["deserializeNBT"], at = [At("HEAD")], cancellable = true)
    fun deserializeNBT(nbt: NBTTagCompound, callbackInfo: CallbackInfo) {
        callbackInfo.cancel()
    }
}

@Overwrite
fun newEmpty(uuid: UUID): SoulNetwork {
    val network: SoulNetwork = Class.forName("WayofTime.bloodmagic.core.data.SoulNetwork").newInstance() as SoulNetwork
    (network as SoulNetworkAccessor).setPlayerId(uuid)
    network.parent = Core.worldDataInstance
    if (!Core.networkData.containsKey(uuid)) Core.networkData[uuid] = "0|0"
    return network
}

@Inject(method = ["fromNBT"], at = [At("HEAD")], cancellable = true)
private fun fromNBT(tagCompound: NBTTagCompound, callbackInfoReturnable: CallbackInfoReturnable<SoulNetwork>) {
    callbackInfoReturnable.returnValue = null
    callbackInfoReturnable.cancel()
}
