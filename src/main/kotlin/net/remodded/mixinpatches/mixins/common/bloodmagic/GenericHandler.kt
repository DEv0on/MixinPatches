@file:Mixin(GenericHandler::class)

package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.util.handler.event.GenericHandler
import net.minecraftforge.event.world.WorldEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(
    method = ["onWorldUnload"],
    at = [At(
        "INVOKE",
        target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;",
        ordinal = 3,
        shift = At.Shift.AFTER
    )]
)
private fun onWorldUnload(event: WorldEvent.Unload, ci: CallbackInfo) {
    GenericHandler.preventSpawnMap.remove(event.world)
    GenericHandler.forceSpawnMap.remove(event.world)
}
