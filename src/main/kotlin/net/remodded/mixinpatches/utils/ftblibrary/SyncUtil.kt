package net.remodded.mixinpatches.utils.ftblibrary

import com.google.gson.Gson
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.database.Redis.client

class SyncUtil {
    companion object {
        fun publish(type: SyncType, nbt: NBTTagCompound) {
            val message = SyncMessage(nbt, type)
            val json = Gson().toJson(message)
            client.getTopic("FTB_UPDATE").publish(json)
        }
    }
}