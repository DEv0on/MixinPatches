package net.remodded.mixinpatches

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.remodded.mixinpatches.listener.PlayerLoginListener
import net.remodded.recore.ReCore
import org.spongepowered.api.Sponge
import org.apache.logging.log4j.LogManager

@Mod(modid = Core.MODID, version = Core.VERSION, name = Core.NAME, acceptableRemoteVersions = "*")
class Core {
    companion object {
        const val MODID = "mixinpatches"
        const val NAME = "mixinpatches"
        const val VERSION = "1.0.0"
        val logger = LogManager.getLogger("MixinPatches")
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(PlayerLoginListener)
        Sponge.getEventManager().registerListeners(ReCore.instance.pluginContainer, PlayerLoginListener)
    }
}