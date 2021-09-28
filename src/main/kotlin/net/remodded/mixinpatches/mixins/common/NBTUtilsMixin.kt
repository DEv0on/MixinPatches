@file:JvmName("NBTUtilsMixin")
@file:Mixin(NBTUtils::class, remap = false)

package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.*
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.database.FTBCollection
import net.remodded.mixinpatches.database.Mongo
import net.remodded.mixinpatches.util.MongoData
import net.remodded.recore.serialize.NbtSerializer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import java.io.File

@DelicateCoroutinesApi
@Overwrite
fun writeNBT(file: File, nbt: NBTTagCompound) {
    GlobalScope.launch(Dispatchers.IO) {
        Mongo.logger.debug("Saving $file")
        val data = FTBCollection(
            file.path,
            NbtSerializer.serialize(nbt).toString()
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
        nbt = NbtSerializer.deserialize(Gson().fromJson(data.data, JsonElement::class.java)) as NBTTagCompound
    }
    Mongo.logger.debug("Loaded ${file.path}")
    return nbt
}