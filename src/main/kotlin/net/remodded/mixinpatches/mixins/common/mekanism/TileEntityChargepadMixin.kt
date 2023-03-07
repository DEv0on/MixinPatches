package net.remodded.mixinpatches.mixins.common.mekanism

import mekanism.common.tile.TileEntityChargepad
import net.minecraft.entity.EntityLivingBase
import net.remodded.mixinpatches.utils.canPlayerInteract
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable


@Mixin(TileEntityChargepad::class)
class TileEntityChargepadMixin {

    @ModifyVariable(method = ["onUpdate"], at = At("STORE"), ordinal = 0)
    fun changeEntityList(entities: List<EntityLivingBase>): List<EntityLivingBase> {
        return entities.filter { it.canPlayerInteract() }
    }

}