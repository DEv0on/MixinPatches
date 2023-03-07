@file:Mixin(MekanismUtils::class)

package net.remodded.mixinpatches.mixins.common

import mekanism.common.util.MekanismUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.remodded.mixinpatches.utils.canPlayerInteract
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(method = ["openEntityGui"], at = [At("HEAD")], cancellable = true)
private fun openEntityGui(player: EntityPlayer, entity: Entity, guiID: Int, ci: CallbackInfo) {
    if (player.canPlayerInteract()) return

    player.closeScreen()
    ci.cancel()
}

@Inject(method = ["openItemGui"], at = [At("HEAD")], cancellable = true)
private fun openItemGui(player: EntityPlayer, hand: EnumHand, guiID: Int, ci: CallbackInfo) {
    if (player.canPlayerInteract()) return

    player.closeScreen()
    ci.cancel()
}

