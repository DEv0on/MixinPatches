@file:Mixin(PlayerSacrificeHelper::class)
package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.core.RegistrarBloodMagic
import WayofTime.bloodmagic.event.SacrificeKnifeUsedEvent
import WayofTime.bloodmagic.util.helper.PlayerSacrificeHelper
import net.minecraft.potion.Potion
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyConstant
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Shadow
private var soulFrayDuration: Int = 0

@Inject(method = ["<clinit>"], at = [At("TAIL")])
private fun injected(ci: CallbackInfo) {
    soulFrayDuration = 6000
}