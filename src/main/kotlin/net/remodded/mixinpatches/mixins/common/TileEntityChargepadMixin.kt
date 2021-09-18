package net.remodded.mixinpatches.mixins.common

import mekanism.common.tile.TileEntityChargepad
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable


@Mixin(TileEntityChargepad::class)
class TileEntityChargepadMixin {

    @ModifyVariable(method = ["onUpdate"], at = At("STORE"), ordinal = 0)
    fun changeEntityList(entities: List<EntityLivingBase>): List<EntityLivingBase> {
        return entities.filter { isPlayerAuthorized(it) }
    }

    private fun isPlayerAuthorized(ent: EntityLivingBase): Boolean {
        if (ent !is EntityPlayer) return false
        if (IslandProtection.canPlayerInteract(ent as Player))
            return true
        return false
    }
}