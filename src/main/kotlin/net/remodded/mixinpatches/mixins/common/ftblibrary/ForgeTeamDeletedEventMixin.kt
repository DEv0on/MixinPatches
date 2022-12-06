package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.events.team.ForgeTeamDeletedEvent
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
import java.io.File

@Mixin(ForgeTeamDeletedEvent::class)
class ForgeTeamDeletedEventMixin {
    @Inject(method = ["<init>(Lcom/feed_the_beast/ftblib/lib/data/ForgeTeam;Ljava/io/File;)V"], at = [At("TAIL")])
    fun constructor(team: ForgeTeam, f: File, callbackInfo: CallbackInfo) {
        val nbt = NBTTagCompound()
        nbt.setString("TeamId", team.id)
        SyncUtil.publish(SyncType.TEAM_DELETED, nbt)

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