package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftbquests.quest.QuestData
import com.feed_the_beast.ftbquests.quest.reward.Reward
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.util.SyncUtils
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.*

@Mixin(QuestData::class)
class QuestDataMixin {

    @Inject(method = ["setRewardClaimed"], at = [At("HEAD")])
    fun setRewardClaimed(player: UUID, reward: Reward, cir: CallbackInfoReturnable<Boolean>) {
        val storage = Redis.client.getListMultimap<String, String>("FTBSync")
        val questData = (this as Any) as QuestData
        val nbt = NBTTagCompound()
        nbt.setString("UpdateType", SyncUtils.UpdateType.REWARD.name)
        nbt.setUniqueId("UUID", player)
        nbt.setInteger("ID", reward.id)
        val jsonNBTElement = NbtSerializer.serialize(nbt)
        val teamID = questData.teamID
        storage.put(teamID, jsonNBTElement.toString())
        val pubNBT = NBTTagCompound()
        pubNBT.setString("TeamID", teamID)
        pubNBT.setTag("Data", nbt)
        Redis.client.getTopic("FTBSync").publish(NbtSerializer.serialize(pubNBT).toString())
    }

}