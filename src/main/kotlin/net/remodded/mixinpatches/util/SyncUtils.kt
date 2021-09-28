package net.remodded.mixinpatches.util

import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftbquests.quest.ChangeProgress
import com.feed_the_beast.ftbquests.quest.ServerQuestFile
import com.feed_the_beast.ftbquests.util.ServerQuestData
import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.nbt.NBTTagCompound
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.redisson.api.listener.MessageListener

object SyncUtils {

    init {
        Redis.client.getTopic("FTBSync").addListener(String::class.java, SyncListener)
    }

    object SyncListener : MessageListener<String> {
        override fun onMessage(channel: CharSequence, msg: String) {
            val jsonElement = Gson().fromJson(msg, JsonElement::class.java)
            val nbtR = NbtSerializer.deserialize(jsonElement) as NBTTagCompound
            val teamID = nbtR.getString("TeamID")
            val nbt = nbtR.getCompoundTag("Data")
            val team = Universe.get().getTeam(teamID)
            val questData = ServerQuestData.get(team)
            val updateType = UpdateType.valueOf(nbt.getString("UpdateType"))
            //loadData(updateType, nbt, questData)
            //todo: fix update loop
        }
    }

    fun loadData(updateType: UpdateType, nbt: NBTTagCompound, questData: ServerQuestData) {
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

    enum class UpdateType {
        TASKDATA,
        CHAPTER,
        QUEST,
        TASK,
        REWARD
    }
}