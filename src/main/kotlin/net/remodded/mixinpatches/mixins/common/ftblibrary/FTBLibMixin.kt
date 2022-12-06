package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.FTBLib
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.remodded.mixinpatches.utils.ftblibrary.SyncHandler
import net.remodded.recore.database.Redis.client
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(FTBLib::class)
class FTBLibMixin {

    @Inject(method = ["onServerStarting"], at = [At("TAIL")])
    fun onServerStarting(event: FMLServerStartingEvent, callbackInfo: CallbackInfo) {
        client.getTopic("FTB_UPDATE").addListener(String::class.java, SyncHandler())
    }
}