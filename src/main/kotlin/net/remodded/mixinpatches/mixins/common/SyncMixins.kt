package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftbquests.quest.ChangeProgress
import com.feed_the_beast.ftbquests.quest.Chapter
import com.feed_the_beast.ftbquests.quest.Quest
import com.feed_the_beast.ftbquests.quest.QuestData
import com.feed_the_beast.ftbquests.quest.task.Task
import com.feed_the_beast.ftbquests.quest.task.TaskData
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.util.SyncUtils
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TaskData::class)
class TaskDataMixin {
    @Inject(method = ["setProgress"], at = [At("HEAD")])
    fun setProgress(progress: Long, ci: CallbackInfo) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val taskData = (this as Any) as TaskData<*>
        val nbt = NBTTagCompound()
        nbt.setString("UpdateType", SyncUtils.UpdateType.TASKDATA.name)
        nbt.setInteger("ID", taskData.task.id)
        nbt.setLong("Progress", progress)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = taskData.data.teamID
        storage.put(teamID, jsonNBTElement.toString())
        val pubNBT = NBTTagCompound()
        pubNBT.setString("TeamID", teamID)
        pubNBT.setTag("Data", nbt)
        SyncUtils.handleUpdate(pubNBT)
    }
}

@Mixin(Chapter::class)
class ChapterMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress, ci: CallbackInfo) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val chapter = (this as Any) as Chapter
        val nbt = NBTTagCompound()
        nbt.setString("UpdateType", SyncUtils.UpdateType.CHAPTER.name)
        nbt.setString("ChangeProgress", type.name)
        nbt.setInteger("ID", chapter.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
        val pubNBT = NBTTagCompound()
        pubNBT.setString("TeamID", teamID)
        pubNBT.setTag("Data", nbt)
        SyncUtils.handleUpdate(pubNBT)
    }
}

@Mixin(Quest::class)
class QuestMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress, ci: CallbackInfo) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val quest = (this as Any) as Quest
        val nbt = NBTTagCompound()
        nbt.setString("UpdateType", SyncUtils.UpdateType.QUEST.name)
        nbt.setString("ChangeProgress", type.name)
        nbt.setInteger("ID", quest.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
        val pubNBT = NBTTagCompound()
        pubNBT.setString("TeamID", teamID)
        pubNBT.setTag("Data", nbt)
        SyncUtils.handleUpdate(pubNBT)
    }
}

@Mixin(Task::class)
class TaskMixin {
    @Inject(method = ["changeProgress"], at = [At("HEAD")])
    fun changeProgress(data: QuestData, type: ChangeProgress, ci: CallbackInfo) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val task = (this as Any) as Task
        val nbt = NBTTagCompound()
        nbt.setString("UpdateType", SyncUtils.UpdateType.TASK.name)
        nbt.setString("ChangeProgress", type.name)
        nbt.setInteger("ID", task.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = data.teamID
        storage.put(teamID, jsonNBTElement.toString())
        val pubNBT = NBTTagCompound()
        pubNBT.setString("TeamID", teamID)
        pubNBT.setTag("Data", nbt)
        SyncUtils.handleUpdate(pubNBT)
    }
}