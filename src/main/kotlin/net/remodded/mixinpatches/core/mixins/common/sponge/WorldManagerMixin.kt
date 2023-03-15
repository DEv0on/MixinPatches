@file:Mixin(WorldManager::class)

package net.remodded.mixinpatches.core.mixins.common.sponge

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.remodded.recore.world.SlimeWorldServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.common.bridge.world.WorldServerBridge
import org.spongepowered.common.world.WorldManager


@Inject(method = ["getClientDimensionId"], at = [At("HEAD")], cancellable = true)
private fun getClientDimensionId(player: EntityPlayerMP, world: World, ci: CallbackInfoReturnable<Int>) {
    if (world !is SlimeWorldServer) return

    ci.returnValue = (world as WorldServerBridge).`bridge$getDimensionId`()
}