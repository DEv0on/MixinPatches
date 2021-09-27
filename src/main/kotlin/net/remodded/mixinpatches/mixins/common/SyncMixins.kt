package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftbquests.quest.ChangeProgress
import com.feed_the_beast.ftbquests.quest.Chapter
import com.feed_the_beast.ftbquests.quest.Quest
import com.feed_the_beast.ftbquests.quest.QuestData
import com.feed_the_beast.ftbquests.quest.task.Task
import com.feed_the_beast.ftbquests.quest.task.TaskData
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Mixin(TaskData::class)
class TaskDataMixin {
    @Inject(method = ["setProgress"], at = [At("HEAD")])
    fun setProgress(progress: Long) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val taskData = (this as Any) as TaskData<*>
        val nbt = NBTTagCompound()
        nbt.setInteger("UpdateType", UpdateType.TASKDATA.ordinal)
        nbt.setInteger("ID", taskData.task.id)
        nbt.setLong("Progress", progress)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = taskData.data.teamID
        storage.put(teamID, jsonNBTElement.toString())
    }
}

@Mixin(Quest::class)
class QuestMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val quest = ((this as Any) as Quest)
        val nbt = NBTTagCompound()
        nbt.setInteger("UpdateType", UpdateType.QUEST.ordinal)
        nbt.setInteger("ChangeProgress", type.ordinal)
        nbt.setInteger("ID", quest.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
    }
}

@Mixin(Chapter::class)
class ChapterMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val chapter = ((this as Any) as Chapter)
        val nbt = NBTTagCompound()
        nbt.setInteger("UpdateType", UpdateType.CHAPTER.ordinal)
        nbt.setInteger("ChangeProgress", type.ordinal)
        nbt.setInteger("ID", chapter.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
    }
}

@Mixin(Task::class)
class TaskMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val task = ((this as Any) as Task)
        val nbt = NBTTagCompound()
        nbt.setInteger("UpdateType", UpdateType.TASK.ordinal)
        nbt.setInteger("ChangeProgress", type.ordinal)
        nbt.setInteger("ID", task.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
    }
}


enum class UpdateType {
    TASKDATA,
    CHAPTER,
    QUEST,
    TASK
}