@file:JvmName("NBTUtilsMixin")
@file:Mixin(NBTUtils::class, remap = false)

package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.*
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.database.FTBCollection
import net.remodded.mixinpatches.database.Mongo
import net.remodded.mixinpatches.util.MongoData
import net.remodded.recore.ReCore
import net.remodded.recore.database.Redis
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import java.io.File

@DelicateCoroutinesApi
@Overwrite
fun writeNBT(file: File, nbt: NBTTagCompound) {
    GlobalScope.launch(Dispatchers.IO) {
        val loadedIslands = Redis.client.getMap<String, String>("LoadedIslands")
        val currentNode = ReCore.config.nodeName
        if (!loadedIslands.containsKey(currentNode)) return@launch
        Mongo.logger.debug("Saving $file")
        val data = FTBCollection(
            file.path,
            MongoData.writeNBTTagCompoundAsByteArray(nbt)
        )

        Mongo.ftbCollection.replaceOneById(file.path, data, ReplaceOptions().upsert(true))
        Mongo.logger.debug("Saved $file")
    }
}

@Overwrite
fun readNBT(file: File): NBTTagCompound {
    var nbt = NBTTagCompound()
    Mongo.logger.debug("Loading ${file.path}")
    runBlocking {
        val data = Mongo.ftbCollection.findOneById(file.path) ?: return@runBlocking
        nbt = MongoData.readByteArrayAsNBT(data.data)
    }
    Mongo.logger.debug("Loaded ${file.path}")
    return nbt
}