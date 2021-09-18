package net.remodded.mixinpatches.util

import com.feed_the_beast.ftblib.events.player.ForgePlayerSavedEvent
import com.feed_the_beast.ftblib.events.team.ForgeTeamSavedEvent
import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftblib.lib.data.TeamType
import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.feed_the_beast.ftblib.lib.util.StringUtils

object UniverseUtils {
    fun getFakeTeam(u: Universe, s: Short, ss: String, tt: TeamType): ForgeTeam {
        return object : ForgeTeam(u, s, ss, tt) {
            override fun markDirty() {
                universe.markDirty()
            }
        }
    }

    fun savePlayer(player: ForgePlayer) {
        val nbt = player.serializeNBT()
        nbt.setString("Name", player.name)
        nbt.setString("UUID", StringUtils.fromUUID(player.id))
        nbt.setString("TeamID", player.team.id)
        NBTUtils.writeNBTSafe(player.getDataFile(""), nbt)
        ForgePlayerSavedEvent(player).post()
    }

    fun saveTeam(team: ForgeTeam) {
        val file = team.getDataFile("")
        if (team.type.save && team.isValid) {
            val nbt = team.serializeNBT()
            nbt.setString("ID", team.id)
            nbt.setShort("UID", team.uid)
            nbt.setString("Type", team.type.getName())
            NBTUtils.writeNBTSafe(file, nbt)
            ForgeTeamSavedEvent(team).post()
        }
    }

//    data class ScheduledTask(val type: TimeType, val time: Long, val task: IScheduledTask)
//    data class PersistentScheduledTask(val id: ResourceLocation, val type: TimeType, val time: Long, val data: NBTTagCompound)
}