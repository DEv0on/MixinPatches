@file:Mixin(NBTUtils::class)

package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.services.s3.transfer.model.UploadResult
import com.amazonaws.util.IOUtils
import com.amazonaws.util.Md5Utils
import com.feed_the_beast.ftblib.FTBLib
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.database.S3.client
import net.remodded.recore.util.toHex
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import java.io.*

@Overwrite
fun writeNBT(file: File, tag: NBTTagCompound) {
    val path = file.path.replace("./", "")
    FTBLib.LOGGER.info("Writing $path")
    val nbt = writeNBTTagCompoundAsByteArray(tag)
    val stream = ByteArrayInputStream(nbt)
    val meta = ObjectMetadata()
    meta.contentLength = nbt.size.toLong()
    val putObjectRequest = PutObjectRequest("FTB", path, stream, meta)
    val transferManager = TransferManagerBuilder.standard()
        .withMinimumUploadPartSize((5 * 1024 * 1024).toLong())
        .withS3Client(client)
        .build()
    try {
        val upload = transferManager.upload(putObjectRequest)
        var result: UploadResult? = null
        try {
            result = upload.waitForUploadResult()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val md5 = Md5Utils.computeMD5Hash(nbt).toHex()
        assert(result != null)
        if (md5 != result!!.eTag) {
            FTBLib.LOGGER.warn("Problem with saving " + path + ". Hash code is: " + result.eTag + " but expected: " + md5)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        //            try (FileOutputStream fileOutputStream = new FileOutputStream(FileUtils.newFile(file))) {
//                CompressedStreamTools.writeCompressed(tag, fileOutputStream);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
    }
}

private fun writeNBTTagCompoundAsByteArray(nbtTagCompound: NBTTagCompound): ByteArray {
    val dataOutput = ByteArrayOutputStream()
    val out = DataOutputStream(dataOutput)
    try {
        CompressedStreamTools.write(nbtTagCompound, out)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return dataOutput.toByteArray()
}

@Overwrite
fun readNBT(file: File): NBTTagCompound? {
    val path = file.path.replace("./", "")
    return if (!client.doesObjectExist("FTB", path)) null else try {
        val s3Object = client.getObject("FTB", path)
        val byteArray = IOUtils.toByteArray(s3Object.objectContent)
        readByteArrayAsNBT(byteArray)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


@Throws(IOException::class)
private fun readByteArrayAsNBT(byteArray: ByteArray): NBTTagCompound? {
    if (byteArray.size == 0) return null
    val inByteStream = ByteArrayInputStream(byteArray)
    val inputStream = DataInputStream(inByteStream)
    return CompressedStreamTools.read(inputStream)
}
