package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.FTBLib
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Mixin(FTBLib::class)
class FTBLibMixin {
//    @Overwrite
//    @Mod.EventHandler
//    fun onServerStarting(event: FMLServerStartingEvent) {}
}