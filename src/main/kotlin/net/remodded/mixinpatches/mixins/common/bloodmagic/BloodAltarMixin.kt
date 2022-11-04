package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.altar.BloodAltar
import net.minecraftforge.common.MinecraftForge
import net.remodded.recore.event.AltarFillEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable

@Mixin(BloodAltar::class)
class BloodAltarMixin {

    @ModifyVariable(method = ["fillMainTank(I)I"], at = At("HEAD"), ordinal = 0)
    fun fillMainTank(amount: Int): Int {
        val event = AltarFillEvent(amount)
        MinecraftForge.EVENT_BUS.post(event)
        if (event.isCanceled)
            return 0
        return event.addedAmount
    }

    @ModifyVariable(method = ["sacrificialDaggerCall(IZ)V"], at = At("HEAD"), ordinal = 0)
    fun sacrificialDaggerCall(amount: Int): Int {
        val event = AltarFillEvent(amount)
        MinecraftForge.EVENT_BUS.post(event)
        if (event.isCanceled)
            return 0
        return event.addedAmount
    }
}