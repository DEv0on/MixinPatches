package net.remodded.mixinpatches.utils.ftblibrary

import com.feed_the_beast.ftblib.FTBLib
import com.feed_the_beast.ftblib.lib.EnumTeamColor
import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftblib.lib.data.TeamType
import com.feed_the_beast.ftblib.lib.data.Universe
import com.google.gson.Gson
import net.remodded.mixinpatches.mixins.common.ftblibrary.ForgeTeamMixin
import net.remodded.mixinpatches.mixins.common.ftblibrary.UniverseMixin
import net.remodded.recore.ReCore.Companion.instance
import net.remodded.recore.serialize.NbtSerializer.serialize
import org.redisson.api.listener.MessageListener
import org.spongepowered.api.Sponge
import java.util.*

class SyncHandler : MessageListener<String> {

    override fun onMessage(channel: CharSequence, msg: String) {
        val message = Gson().fromJson(msg, SyncMessage::class.java)
        val nbt = message.getNBT()
        FTBLib.LOGGER.warn("Received FTB update message [" + message.getSyncType().name + "]")
        FTBLib.LOGGER.info(serialize(nbt).toString())
        Sponge.getScheduler().createSyncExecutor(instance).execute {
            when (message.getSyncType()) {
                SyncType.PLAYER -> {
                    val uuid = nbt.getUniqueId("PlayerUUID")
                    val universe = Universe.get()
                    if (!universe.players.containsKey(uuid)) universe.players[uuid] =
                        ForgePlayer(universe, uuid, nbt.getString("Name"))
                }

                SyncType.TEAM_JOIN -> {
                    val playerUUID = nbt.getUniqueId("PlayerUUID")
                    val team =
                        Universe.get().getTeam(nbt.getString("TeamId"))
                    val forgePlayer =
                        Universe.get().getPlayer(playerUUID)
                    (team as ForgeTeamMixin).internalAddMember(forgePlayer, false)
                }

                SyncType.TEAM_LEAVE -> {
                    val playerUUID = nbt.getUniqueId("PlayerUUID")
                    val team = Universe.get().getTeam(nbt.getString("TeamId"))
                    val forgePlayer = Universe.get().getPlayer(playerUUID)
                    (team as ForgeTeamMixin).internalRemoveMember(forgePlayer)
                }

                SyncType.TEAM_CREATED -> {
                    val id = nbt.getShort("Id")
                    if (Universe.get().getTeam(id).isValid) return@execute
                    val playerUUID = nbt.getUniqueId("PlayerUUID")
                    val teamId = nbt.getString("TeamId")
                    val color = EnumTeamColor.valueOf(nbt.getString("Color"))
                    val forgePlayer = Universe.get().getPlayer(playerUUID)
                    val team = ForgeTeam(Universe.get(), id, teamId, TeamType.PLAYER)
                    forgePlayer!!.team = team
                    team.owner = forgePlayer
                    team.color = color
                    team.universe.addTeam(team)
                }

                SyncType.TEAM_DELETED -> {
                    val teamId = nbt.getString("TeamId")
                    val team = Universe.get().getTeam(teamId)
                    if (!team.isValid) return@execute
                    val o = team.getOwner()
                    for (player in team.members) {
                        if (player !== o) {
                            (team as ForgeTeamMixin).internalRemoveMember(player)
                        }
                    }
                    if (o != null) {
                        (team as ForgeTeamMixin).internalRemoveMember(o)
                    }
                    (Universe.get() as UniverseMixin).internalRemoveTeam(team)
                }
            }
        }
    }
}