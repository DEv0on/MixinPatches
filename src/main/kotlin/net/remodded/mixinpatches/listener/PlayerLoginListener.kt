package net.remodded.mixinpatches.listener

import com.feed_the_beast.ftblib.events.player.ForgePlayerLoadedEvent
import com.feed_the_beast.ftblib.events.team.ForgeTeamLoadedEvent
import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftblib.lib.data.TeamType
import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftblib.lib.util.StringUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.database.FTBCollection
import net.remodded.mixinpatches.database.Mongo
import net.remodded.mixinpatches.util.UniverseUtils
import net.remodded.recore.serialize.NbtSerializer
import net.remodded.recore.util.sync
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.io.File

object PlayerLoginListener {
    @DelicateCoroutinesApi
    @Listener
    fun onPlayerLogin(event: ClientConnectionEvent.Auth) {
        val gameProfile = event.profile
        val u = Universe.get()
        var playerData: FTBCollection?
        var playerNbt: NBTTagCompound
        var teamNbt: NBTTagCompound? = null
        var isTeamAlreadyLoaded = false
        GlobalScope.launch(sync) {


            val playerDir = File(u.worldDirectory, "data/ftb_lib/players/")
            playerData = Mongo.ftbCollection.findOneById("$playerDir/${gameProfile.uniqueId}.dat")
            if (playerData != null) {
                playerNbt =
                    NbtSerializer.deserialize(
                        Gson().fromJson(
                            playerData!!.data,
                            JsonElement::class.java
                        )
                    ) as NBTTagCompound
                val uuidStr = playerNbt.getString("UUID")
                val uuid = StringUtils.fromString(uuidStr)

                if (uuid != null) {
                    val forgePlayer = ForgePlayer(u, uuid, playerNbt.getString("Name"))
                    u.players[uuid] = forgePlayer
                }

                if (playerNbt.hasKey("TeamID")) {
                    val teamID = playerNbt.getString("TeamID")
                    if (u.getTeam(teamID).id != "") {
                        isTeamAlreadyLoaded = true
                        u.players[uuid]?.team = u.getTeam(teamID)
                    }
                    if (!isTeamAlreadyLoaded) {
                        val teamData: FTBCollection?
                        val teamDir = File(u.worldDirectory, "data/ftb_lib/teams/")
                        teamData = Mongo.ftbCollection.findOneById("$teamDir/${teamID}.dat")
                        if (teamData != null) {
                            teamNbt = NbtSerializer.deserialize(
                                Gson().fromJson(
                                    teamData.data,
                                    JsonElement::class.java
                                )
                            ) as NBTTagCompound

                            val sID = teamNbt!!.getString("ID")
                            val uID = teamNbt!!.getShort("UID")
                            val team =
                                ForgeTeam(
                                    u,
                                    u.generateTeamUID(uID),
                                    sID,
                                    TeamType.NAME_MAP.get(teamNbt!!.getString("Type"))
                                )
                            u.addTeam(team)
                            if (uID.toInt() == 0) {
                                team.markDirty()
                            }
                        }
                    }

                }

                val player = u.players[gameProfile.uniqueId]

                if (player != null) {
                    if (playerNbt != null && !playerNbt.isEmpty) {
                        player.team = u.getTeam(playerNbt.getString("TeamID"))
                        player.deserializeNBT(playerNbt)
                    }
                    ForgePlayerLoadedEvent(player).post()
                    Mongo.logger.warn("Loaded player ${gameProfile.uniqueId}")
                    if (!isTeamAlreadyLoaded) {
                        if (player.team.type.save) {
                            if (teamNbt != null && !teamNbt!!.isEmpty) {
                                player.team.deserializeNBT(teamNbt)

                                if (player.team.title.unformattedText == "No Team") {
                                    event.isCancelled = true
                                    u.removeTeam(player.team)
                                    player.team = u.getTeam("")
                                }

                            }
                            ForgeTeamLoadedEvent(player.team).post()
                            Mongo.logger.warn("Loaded team ${player.team.title.unformattedText}")
                        }
                    }
                }
            }
        }
    }

    @Listener
    fun onPlayerDisconnect(event: ClientConnectionEvent.Disconnect) {
        val u = Universe.get()
        val gameProfile = event.targetEntity.profile
        val forgePlayer = u.getPlayer(gameProfile.uniqueId)!!
        val forgeTeam = forgePlayer.team ?: return

        UniverseUtils.savePlayer(forgePlayer)
        UniverseUtils.saveTeam(forgeTeam)

        u.players.remove(gameProfile.uniqueId)
        Mongo.logger.warn("Unloaded player ${gameProfile.name.get()}")
    }
}
