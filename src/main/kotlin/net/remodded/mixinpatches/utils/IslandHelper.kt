package net.remodded.mixinpatches.utils

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.remodded.reisland.listeners.IslandProtection
import org.spongepowered.api.entity.living.player.Player

fun EntityLivingBase.canPlayerInteract(): Boolean {
    if (this !is EntityPlayer)
        return false
    return IslandProtection.canPlayerInteract(this as Player)
}