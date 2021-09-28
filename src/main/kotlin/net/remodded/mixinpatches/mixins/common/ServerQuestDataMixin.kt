@file:Mixin(ServerQuestData::class)

package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.events.team.ForgeTeamLoadedEvent
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftbquests.quest.ChangeProgress
import com.feed_the_beast.ftbquests.quest.ServerQuestFile
import com.feed_the_beast.ftbquests.util.ServerQuestData
import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.util.SyncUtils
import net.remodded.recore.database.Redis
import net.remodded.recore.serialize.NbtSerializer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(
    method = ["onTeamLoaded"],
    at = [At(
        "INVOKE",
        shift = At.Shift.AFTER,
        target = "Lcom/feed_the_beast/ftbquests/util/ServerQuestData;readData(Lnet/minecraft/nbt/NBTTagCompound;)V"
    )]
)
private fun onTeamLoaded(event: ForgeTeamLoadedEvent, ci: CallbackInfo) {
    val listMultimap = Redis.client.getListMultimap<String, String>("FTBSync")
    val updates = listMultimap.getAll(event.team.id)

    updates.forEach { data ->
        val jsonElement = Gson().fromJson(data, JsonElement::class.java)
        val nbt = NbtSerializer.deserialize(jsonElement) as NBTTagCompound
        loadData(event.team, nbt)
    }
}

private fun loadData(team: ForgeTeam, nbt: NBTTagCompound) {
    val sqf = ServerQuestFile.INSTANCE
    val questData = ServerQuestData.get(team)
    val updateType = SyncUtils.UpdateType.valueOf(nbt.getString("UpdateType"))
    SyncUtils.loadData(updateType, nbt, questData)
}
