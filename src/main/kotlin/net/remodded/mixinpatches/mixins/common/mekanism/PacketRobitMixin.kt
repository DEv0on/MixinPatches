package net.remodded.mixinpatches.mixins.common.mekanism

import mekanism.common.PacketHandler
import mekanism.common.network.PacketRobit
import mekanism.common.network.PacketRobit.RobitMessage
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.remodded.mixinpatches.utils.canPlayerInteract
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(PacketRobit::class)
class PacketRobitMixin {
    @Inject(method = ["onMessage"], at = [At("HEAD")], cancellable = true)
    fun onMessage(message: RobitMessage, context: MessageContext, cir: CallbackInfoReturnable<IMessage>) {
        val player: EntityPlayer = PacketHandler.getPlayer(context)
        if (player.canPlayerInteract()) return

        cir.returnValue = null
        cir.cancel()
    }
}