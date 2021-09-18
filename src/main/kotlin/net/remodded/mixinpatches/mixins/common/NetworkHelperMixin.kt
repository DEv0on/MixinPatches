@file:Mixin(NetworkHelper::class)

package net.remodded.mixinpatches.mixins.common

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import WayofTime.bloodmagic.core.data.SoulNetwork
import WayofTime.bloodmagic.util.helper.NetworkHelper
import net.minecraftforge.fml.server.FMLServerHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import java.util.*

@Overwrite
fun getSoulNetwork(uuid: String): SoulNetwork {
    val player = FMLServerHandler.instance().server.playerList.getPlayerByUUID(UUID.fromString(uuid))
    if (player == null) throw IllegalArgumentException("Player is not online")

    val storage = player.world.perWorldStorage
    var instance: BMWorldSavedData? = storage.getOrLoadData(BMWorldSavedData::class.java, BMWorldSavedData.ID) as BMWorldSavedData?

    if (instance == null) {
        instance = BMWorldSavedData()
        storage.setData(BMWorldSavedData.ID, instance)
    }

    return instance.getNetwork(UUID.fromString(uuid))
}
