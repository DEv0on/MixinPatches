package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import net.minecraft.nbt.NBTTagCompound
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(ForgeTeam::class)
class ForgeTeamMixin {
    @Inject(method = ["serializeNBT"], at = [At("RETURN")])
    fun serializeNBT(cir: CallbackInfoReturnable<NBTTagCompound>) {
        cir.returnValue.removeTag("Players")
    }
}