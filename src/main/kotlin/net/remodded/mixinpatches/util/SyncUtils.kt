package net.remodded.mixinpatches.util

import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftbquests.quest.ChangeProgress
import com.feed_the_beast.ftbquests.quest.ServerQuestFile
import com.feed_the_beast.ftbquests.util.ServerQuestData
import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.ReCore
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.redisson.api.listener.MessageListener

object SyncUtils {

    init {
        Redis.client.getTopic("FTBSync").addListener(String::class.java, SyncListener)
    }

    private val updateSet = HashSet<String>()

    object SyncListener : MessageListener<String> {
        override fun onMessage(channel: CharSequence, msg: String) {
            val jsonElement = Gson().fromJson(msg, JsonElement::class.java)
            val nbtR = NbtSerializer.deserialize(jsonElement) as NBTTagCompound
            val teamID = nbtR.getString("TeamID")
            val nbt = nbtR.getCompoundTag("Data")
            val team = Universe.get().getTeam(teamID)
            val questData = ServerQuestData.get(team)
            val updateType = UpdateType.valueOf(nbt.getString("UpdateType"))

            // if updateSet already contains this message it mean that it came from this server, so we don't need to update anything
            if (updateSet.contains(msg)) {
                updateSet.remove(msg)
                return
            }
            // adding message to updateSet at this point indicate that it came from other node, and should be processed but not propagated
            updateSet.add(msg)
            loadData(updateType, nbt, questData)
        }
    }

    fun loadData(updateType: UpdateType, nbt: NBTTagCompound, questData: ServerQuestData) {
        ReCore.logger.warn("Update [${updateType.name}]")
        val sqf = ServerQuestFile.INSTANCE

        when (updateType) {
            UpdateType.TASKDATA -> {
                val task = sqf.getTask(nbt.getInteger("ID"))!!
                questData
                    .getTaskData(task)
                    .setProgress(nbt.getLong("Progress"))
            }
            UpdateType.CHAPTER -> {
                val chapter = sqf.getChapter(nbt.getInteger("ID"))!!
                val progressChange = ChangeProgress.valueOf(nbt.getString("ChangeProgress"))
                chapter.changeProgress(questData, progressChange)
            }
            UpdateType.QUEST -> {
                val quest = sqf.getQuest(nbt.getInteger("ID"))!!
                val progressChange = ChangeProgress.valueOf(nbt.getString("ChangeProgress"))
                quest.changeProgress(questData, progressChange)
            }
            UpdateType.TASK -> {
                val task = sqf.getTask(nbt.getInteger("ID"))!!
                val progressChange = ChangeProgress.valueOf(nbt.getString("ChangeProgress"))
                task.changeProgress(questData, progressChange)
            }
            UpdateType.REWARD -> {
                val reward = sqf.getReward(nbt.getInteger("ID"))!!
                val uuid = nbt.getUniqueId("UUID")!!
                questData.setRewardClaimed(uuid, reward)
            }
        }
    }

    fun handleUpdate(nbt: NBTTagCompound) {
        val message = NbtSerializer.serialize(nbt).toString()

        // if updateSet contains message at this moment it means that it came from other node and shouldn't be propagated.
        // and we can safely delete this message
        if (updateSet.contains(message)) {
            updateSet.remove(message)
            return
        }

        // if update set doesn't contain message it means that it is change from this server and should be propagated
        // also setting message in updateSet at this point can help detect update from this server in message listener
        updateSet.add(message)
        Redis.client.getTopic("FTBSync").publish(message)
    }

    enum class UpdateType {
        TASKDATA,
        CHAPTER,
        QUEST,
        TASK,
        REWARD
    }
}