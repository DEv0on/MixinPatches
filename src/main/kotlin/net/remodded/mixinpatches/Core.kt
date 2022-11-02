package net.remodded.mixinpatches

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.remodded.recore.ReCore
import org.spongepowered.api.Sponge
import org.apache.logging.log4j.LogManager
import org.redisson.api.RMap
import java.util.*

@Mod(modid = Core.MODID, version = Core.VERSION, name = Core.NAME, acceptableRemoteVersions = "*")
class Core {
    companion object {
        const val MODID = "mixinpatches"
        const val NAME = "mixinpatches"
        const val VERSION = "1.0.0"
        val logger = LogManager.getLogger("MixinPatches")
        lateinit var worldDataInstance: BMWorldSavedData
        lateinit var networkData: RMap<UUID, String>
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
    }
}