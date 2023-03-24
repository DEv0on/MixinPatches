@file:Mixin(PotionEventHandlers::class)

package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.potion.PotionEventHandlers
import WayofTime.bloodmagic.util.helper.PlayerSacrificeHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.ModifyConstant

@ModifyConstant(method = ["onPlayerRespawn"], constant = [Constant(intValue = 400)])
private fun onPlayerRespawn(value: Int): Int {
    return PlayerSacrificeHelper.soulFrayDuration
}