package net.remodded.mixinpatches.mixins.common.bloodmagic

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

@Suppress("CAST_NEVER_SUCCEEDS")
@Mixin(BMWorldSavedData::class)
class BMWorldSavedDataMixin {
    @Inject(method = ["<init>(Ljava/lang/String;)V"], at = [At("TAIL")])
    fun BMWorldSavedData(id: String, ci: CallbackInfo) {
        Core.networkData = Redis.client.getMap("BloodMagic-SoulNetwork")
        Core.worldDataInstance = this as BMWorldSavedData
    }

    @Inject(method = ["func_76184_a"], at = [At("HEAD")], cancellable = true)
    fun readFromNBT(tagCompound: NBTTagCompound, ci: CallbackInfo) {
        ci.cancel()
    }

    @Overwrite
    fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        return tagCompound
    }
}