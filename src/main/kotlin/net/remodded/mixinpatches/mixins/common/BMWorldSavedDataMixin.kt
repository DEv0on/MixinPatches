package net.remodded.mixinpatches.mixins.common

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.Core
import net.remodded.recore.database.Redis
import org.redisson.api.RMap
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Mixin(BMWorldSavedData::class)
class BMWorldSavedDataMixin {


    @Inject(method = ["<init>(Ljava/lang/String;)V"], at = [At("TAIL")])
    fun BMWorldSavedData(id: String, ci: CallbackInfo) {
        Core.networkData = Redis.client.getMap("BloodMagic-SoulNetwork")
        Core.worldDataInstance = this as BMWorldSavedData
    }


//    @Redirect(method = ["readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"], at = At("HEAD"))
    @Inject(method = ["func_76184_a"], at = [At("HEAD")], cancellable = true)
    fun func_76184_a(tagCompound: NBTTagCompound, ci: CallbackInfo) {
        ci.cancel()
        return
    }

//    @Redirect(method = ["writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;"], at = At("HEAD"))
    @Overwrite
    fun func_189551_b(tagCompound: NBTTagCompound): NBTTagCompound {
        return tagCompound
    }
}