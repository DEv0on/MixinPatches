package net.remodded.mixinpatches.mixins.common

import WayofTime.bloodmagic.core.data.SoulTicket
import WayofTime.bloodmagic.iface.IActivatable
import WayofTime.bloodmagic.item.sigil.ItemSigil
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleable
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase
import WayofTime.bloodmagic.util.helper.NetworkHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow

@Mixin(ItemSigilToggleable::class, remap = false)
abstract class ItemSigilToggleableMixin : ItemSigil(0), IActivatable {

    @Shadow
    abstract override fun setActivatedState(stack: ItemStack, activated: Boolean): ItemStack

    @Shadow
    abstract override fun getActivated(stack: ItemStack): Boolean

    @Shadow
    abstract fun onSigilUpdate(
        stack: ItemStack?,
        world: World?,
        player: EntityPlayer?,
        itemSlot: Int,
        isSelected: Boolean
    )

    @Overwrite
    override fun onUpdate(
        stack: ItemStack,
        worldIn: World,
        entityIn: Entity,
        itemSlot: Int,
        isSelected: Boolean
    ) {
        if (entityIn !is EntityPlayerMP) return

        if (!IslandProtection.canPlayerInteract(entityIn as Player)) {
            val item = stack.item as ItemSigilToggleableBase
            item.setActivatedState(stack, false)
            return
        }

        if (!worldIn.isRemote && this.getActivated(stack)) {
            if (entityIn.ticksExisted % 100 == 0 && !NetworkHelper.getSoulNetwork(this.getBinding(stack))
                    .syphonAndDamage(
                        entityIn as EntityPlayer,
                        SoulTicket.item(stack, worldIn, entityIn, this.lpUsed)
                    ).isSuccess
            ) {
                this.setActivatedState(stack, false)
            }
            this.onSigilUpdate(stack, worldIn, entityIn as EntityPlayer, itemSlot, isSelected)
        }
    }
}