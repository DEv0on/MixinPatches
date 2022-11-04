package net.remodded.mixinpatches.mixins.common.botania

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.SoundCategory
import net.minecraft.world.World
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vazkii.botania.common.core.handler.ModSounds
import vazkii.botania.common.core.helper.Vector3
import vazkii.botania.common.entity.EntityFallingStar
import vazkii.botania.common.item.equipment.tool.ItemStarSword
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword
import kotlin.math.abs

@Mixin(ItemStarSword::class)
abstract class ItemStarSwordMixin {

    @Inject(method = ["onUpdate"], at = [At("HEAD")], cancellable = true)
    fun onUpdate(par1ItemStack: ItemStack, world: World, par3Entity: Entity, par4: Int, par5: Boolean, ci: CallbackInfo) {
        if (par3Entity !is EntityPlayer || !IslandProtection.canPlayerInteract(par3Entity as Player))
            ci.cancel()
    }
}