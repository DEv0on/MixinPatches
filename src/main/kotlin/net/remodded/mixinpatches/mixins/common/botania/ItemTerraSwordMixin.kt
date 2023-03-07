package net.remodded.mixinpatches.mixins.common.botania

import net.minecraft.entity.player.EntityPlayer
import net.remodded.mixinpatches.utils.canPlayerInteract
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraSword

@Mixin(ItemTerraSword::class, remap = false)
class ItemTerraSwordMixin {

    @Inject(method = ["trySpawnBurst"], at = [At("HEAD")], cancellable = true)
    fun trySpawnBurst(player: EntityPlayer, cl: CallbackInfo) {
        if (!player.canPlayerInteract())
            cl.cancel()
    }
}