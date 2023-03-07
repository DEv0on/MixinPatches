package net.remodded.mixinpatches.mixins.common.botania

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.remodded.mixinpatches.utils.canPlayerInteract
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vazkii.botania.common.item.equipment.tool.ItemStarSword

@Mixin(ItemStarSword::class)
abstract class ItemStarSwordMixin {

    @Inject(method = ["onUpdate"], at = [At("HEAD")], cancellable = true)
    fun onUpdate(par1ItemStack: ItemStack, world: World, par3Entity: Entity, par4: Int, par5: Boolean, ci: CallbackInfo) {
        if (par3Entity !is EntityPlayer || !par3Entity.canPlayerInteract())
            ci.cancel()
    }
}