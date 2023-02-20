@file:Mixin(CommonPlayerTickHandler::class)

package net.remodded.mixinpatches.mixins.common

import mekanism.common.CommonPlayerTickHandler
import net.minecraft.entity.player.EntityPlayer
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Inject(method = ["isFlamethrowerOn"], at = [At("HEAD")], cancellable = true)
private fun isFlamethrowerOn(player: EntityPlayer, cir: CallbackInfoReturnable<Boolean>) {
    if (IslandProtection.canPlayerInteract(player as Player)) return

    cir.returnValue = false
    cir.cancel()
}