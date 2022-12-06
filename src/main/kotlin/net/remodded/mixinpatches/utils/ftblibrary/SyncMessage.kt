package net.remodded.mixinpatches.utils.ftblibrary

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.serialize.NbtSerializer.deserialize
import net.remodded.recore.serialize.NbtSerializer.serialize

class SyncMessage(nbt: NBTTagCompound, private var syncType: SyncType) {
    private var nbtString: String

    init {
        this.nbtString = serialize(nbt).toString()
    }

    fun getNBT(): NBTTagCompound {
        val json = Gson().fromJson(nbtString, JsonElement::class.java)
        return deserialize(json) as NBTTagCompound
    }

    fun getNbtString(): String {
        return nbtString
    }

    fun getSyncType(): SyncType {
        return syncType
    }
}