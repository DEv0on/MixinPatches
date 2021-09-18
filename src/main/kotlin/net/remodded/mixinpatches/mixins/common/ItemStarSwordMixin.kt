package net.remodded.mixinpatches.mixins.common

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
import vazkii.botania.common.core.handler.ModSounds
import vazkii.botania.common.core.helper.Vector3
import vazkii.botania.common.entity.EntityFallingStar
import vazkii.botania.common.item.equipment.tool.ItemStarSword
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword
import kotlin.math.abs

@Mixin(ItemStarSword::class)
abstract class ItemStarSwordMixin : ItemManasteelSword() {

    @Overwrite
    override fun onUpdate(par1ItemStack: ItemStack, world: World, par3Entity: Entity, par4: Int, par5: Boolean) {
        if (par3Entity !is EntityPlayer) return

        if (!IslandProtection.canPlayerInteract(par3Entity as Player))
            return

        super.onUpdate(par1ItemStack, world, par3Entity, par4, par5)

        val haste: PotionEffect? = par3Entity.getActivePotionEffect(MobEffects.HASTE)
        val check = if (haste == null) 0.16666667f else if (haste.amplifier == 1) 0.5f else 0.4f
        if (par3Entity.heldItemMainhand == par1ItemStack && par3Entity.swingProgress == check && !world.isRemote) {
            val pos = ToolCommons.raytraceFromEntity(world, par3Entity, true, 48.0)
            if (pos != null) {
                var posVec: Vector3 = Vector3.fromBlockPos(pos.blockPos)
                var motVec = Vector3((0.5 * Math.random() - 0.25) * 18, 24.0, (0.5 * Math.random() - 0.25) * 18)
                posVec = posVec.add(motVec)
                motVec = motVec.normalize().negate().multiply(1.5)
                val star = EntityFallingStar(world, par3Entity)
                star.setPosition(posVec.x, posVec.y, posVec.z)
                star.motionX = motVec.x
                star.motionY = motVec.y
                star.motionZ = motVec.z
                world.spawnEntity(star)
                if (!world.isRaining
                    && abs(world.worldTime - 18000) < 1800 && Math.random() < 0.125
                ) {
                    val bonusStar = EntityFallingStar(world, par3Entity)
                    bonusStar.setPosition(posVec.x, posVec.y, posVec.z)
                    bonusStar.motionX = motVec.x + Math.random() - 0.5
                    bonusStar.motionY = motVec.y + Math.random() - 0.5
                    bonusStar.motionZ = motVec.z + Math.random() - 0.5
                    world.spawnEntity(bonusStar)
                }
                ToolCommons.damageItem(par1ItemStack, 1, par3Entity, MANA_PER_DAMAGE)
                world.playSound(
                    null,
                    par3Entity.posX,
                    par3Entity.posY,
                    par3Entity.posZ,
                    ModSounds.starcaller,
                    SoundCategory.PLAYERS,
                    0.4f,
                    1.4f
                )
            }
        }
    }
}