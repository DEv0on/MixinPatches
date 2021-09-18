package net.remodded.mixinpatches.util

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

object MongoData {
    fun readByteArrayAsNBT(byteArray: ByteArray): NBTTagCompound {
        if (byteArray.isEmpty())
            return NBTTagCompound()

        val inByteStream = ByteArrayInputStream(byteArray)
        val inputStream = DataInputStream(inByteStream)

        return CompressedStreamTools.read(inputStream)
    }

    fun writeNBTTagCompoundAsByteArray(nbtTagCompound: NBTTagCompound): ByteArray {
        val dataOutput = ByteArrayOutputStream()
        val out = DataOutputStream(dataOutput)

        CompressedStreamTools.write(nbtTagCompound, out)

        return dataOutput.toByteArray()
    }
}