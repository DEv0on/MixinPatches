package net.remodded.mixinpatches.core.mixins.common.minecraft

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(EntityItem::class, remap = false)
class EntityItemMixin {

    @Shadow
    var lifespan: Int = 0

    @Inject(method = ["<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"], at = [At("TAIL")])
    fun init(worldIn: World, x: Double, y: Double, z: Double, stack: ItemStack, ci: CallbackInfo) {
        if (stack.tagCompound != null && stack.tagCompound!!.hasKey("lifespan")) {
            lifespan = stack.tagCompound!!.getInteger("lifespan")
            stack.tagCompound!!.removeTag("lifespan")

            if (stack.tagCompound!!.isEmpty)
                stack.tagCompound = null
        }
    }
}
