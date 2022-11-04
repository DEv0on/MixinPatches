package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.iface.IActivatable
import WayofTime.bloodmagic.item.sigil.ItemSigil
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ItemSigilToggleable::class, remap = false)
abstract class ItemSigilToggleableMixin {
    @Shadow
    abstract fun getActivated(stack: ItemStack): Boolean

    @Inject(method = ["onUpdate"], at = [At("HEAD")], cancellable = true)
    fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean, ci: CallbackInfo) {
        if (entityIn !is EntityPlayerMP || !getActivated(stack)) return

        if (!IslandProtection.canPlayerInteract(entityIn as Player)) {
            val item = stack.item as ItemSigilToggleableBase
            item.setActivatedState(stack, false)
            ci.cancel()
        }
    }
}