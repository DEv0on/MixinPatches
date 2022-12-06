package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.events.team.ForgeTeamCreatedEvent
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.feed_the_beast.ftblib.lib.util.StringUtils
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.utils.ftblibrary.SyncType
import net.remodded.mixinpatches.utils.ftblibrary.SyncUtil
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo


@Mixin(ForgeTeamCreatedEvent::class)
class ForgeTeamCreatedEventMixin {

    @Inject(method = ["<init>(Lcom/feed_the_beast/ftblib/lib/data/ForgeTeam;)V"], at = [At("TAIL")])
    fun constructor(team: ForgeTeam, callbackInfo: CallbackInfo) {
        val nbt = NBTTagCompound()
        nbt.setShort("Id", team.uid)
        nbt.setUniqueId("PlayerUUID", team.owner.id)
        nbt.setString("Color", team.color.name)
        nbt.setString("TeamId", team.id)

        SyncUtil.publish(SyncType.TEAM_CREATED, nbt)

        if (team.type.save && team.isValid) {
            val teamNBT = team.serializeNBT()
            teamNBT.setString("ID", team.id)
            teamNBT.setShort("UID", team.uid)
            teamNBT.setString("Type", team.type.getName())
            NBTUtils.writeNBT(team.getDataFile(""), teamNBT)
        }
        val player = team.getOwner()

        val playerNBT = player!!.serializeNBT()
        playerNBT.setString("Name", player.name)
        playerNBT.setString("UUID", StringUtils.fromUUID(player.id))
        playerNBT.setString("TeamID", player.team.id)
        NBTUtils.writeNBT(player.getDataFile(""), playerNBT)
    }
}
