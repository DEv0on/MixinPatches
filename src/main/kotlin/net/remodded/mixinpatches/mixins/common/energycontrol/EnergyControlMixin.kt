@file:Mixin(EnergyControl::class)

package net.remodded.mixinpatches.mixins.common.energycontrol

import com.zuxelus.energycontrol.EnergyControl
import net.minecraft.entity.player.EntityPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Shadow
private lateinit var altPressed: Map<EntityPlayer, Boolean>

@Inject(method = ["<clinit>"], at = [At("TAIL")])
private fun injected(ci: CallbackInfo) {
    altPressed = WeakHashMap()
}
