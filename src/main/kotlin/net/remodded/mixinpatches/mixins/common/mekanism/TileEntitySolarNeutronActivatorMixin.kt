package net.remodded.mixinpatches.mixins.common.mekanism

import mekanism.common.tile.TileEntitySolarNeutronActivator
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable


@Mixin(TileEntitySolarNeutronActivator::class)
class TileEntitySolarNeutronActivatorMixin {

    @ModifyVariable(method = ["onUpdate()V"], at = At("STORE"), ordinal = 0)
    fun onUpdateSun(seesSun: Boolean): Boolean {
        return true
    }
}