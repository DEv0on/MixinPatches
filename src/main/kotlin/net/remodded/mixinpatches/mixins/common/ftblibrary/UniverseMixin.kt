package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.amazonaws.services.s3.model.S3ObjectSummary
import com.feed_the_beast.ftblib.FTBLib
import com.feed_the_beast.ftblib.events.ServerReloadEvent
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoadedEvent
import com.feed_the_beast.ftblib.events.team.ForgeTeamLoadedEvent
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent.CreateServerTeams
import com.feed_the_beast.ftblib.lib.EnumReloadType
import com.feed_the_beast.ftblib.lib.EnumTeamColor
import com.feed_the_beast.ftblib.lib.data.*
import com.feed_the_beast.ftblib.lib.util.FileUtils
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.feed_the_beast.ftblib.lib.util.StringUtils
import com.feed_the_beast.ftblib.lib.util.misc.TimeType
import com.mojang.authlib.GameProfile
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.Constants
import net.remodded.mixinpatches.utils.ftblibrary.SyncType
import net.remodded.mixinpatches.utils.ftblibrary.SyncUtil
import net.remodded.recore.database.S3
import net.remodded.recore.database.S3.client
import net.remodded.recore.serialize.NbtSerializer.serialize
import org.spongepowered.api.Sponge
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.io.File
import java.io.InputStream
import java.util.*

@Mixin(Universe::class)
abstract class UniverseMixin {

    @Shadow lateinit var persistentScheduledTasks: List<Any>
    @Shadow lateinit var teamMap: Short2ObjectOpenHashMap<ForgeTeam>
    @Shadow lateinit var teams: Map<String, ForgeTeam>

    @Shadow abstract fun getPlayer(player: GameProfile): ForgePlayer?
    @Shadow abstract fun markDirty()
    @Shadow abstract fun clearCache()
    @Shadow abstract fun getPlayers(): Collection<ForgePlayer>

    @Overwrite
    private fun load() {
        val universeData = NBTTagCompound()

        val universeUUID = Sponge.getServer().getWorld(Sponge.getServer().defaultWorldName).get().uniqueId
        universeData.setString("UUID", universeUUID.toString())

        val taskTag = universeData.getTagList("PersistentScheduledTasks", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until taskTag.tagCount()) {
            val taskData = taskTag.getCompoundTagAt(i)
            (persistentScheduledTasks as ArrayList).add(
                Class.forName("com.feed_the_beast.ftblib.lib.data.Universe\$PersistentScheduledTask").getConstructor(
                    ResourceLocation::class.java,
                    TimeType::class.java,
                    Long::class.java,
                    NBTTagCompound::class.java
                ).newInstance(
                    ResourceLocation(taskData.getString("ID")),
                    TimeType.NAME_MAP[taskData.getString("Type")],
                    taskData.getLong("Time"),
                    taskData.getCompoundTag("Data")
                )
            )

        }
        val data = universeData.getCompoundTag("Data")
        UniverseLoadedEvent.Pre(this as Universe, data).post()
        val playerNBT: MutableMap<UUID, NBTTagCompound> = HashMap()
        val teamNBT: MutableMap<String, NBTTagCompound> = HashMap()
        if (!S3.client.doesBucketExistV2("FTB")) S3.client.putObject("FTB", "FeedTheBeast", "DummyObject")
        val fileList: List<S3ObjectSummary> = S3.client.listObjects("FTB").getObjectSummaries()
        for (s3ObjectSummary in fileList) {
            val file: File = File(s3ObjectSummary.getKey())
            if (!file.path.contains("players")) continue

            try {
                val inputStream: InputStream = client.getObject("FTB", s3ObjectSummary.key).objectContent
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val nbt = NBTUtils.readNBT(file)
            if (nbt != null) {
                var uuidString = nbt.getString("UUID")
                if (uuidString.isEmpty()) {
                    uuidString = FileUtils.getBaseName(file)
                    FileUtils.deleteSafe(file)
                }
                val uuid: UUID? = StringUtils.fromString(uuidString)
                if (uuid != null) {
                    playerNBT[uuid] = nbt
                    val player = ForgePlayer(this, uuid, nbt.getString("Name"))
                    this.players.put(uuid, player)
                }
            }
        }

        for (s3ObjectSummary in fileList) {
            val file: File = File(s3ObjectSummary.getKey())
            if (!file.path.contains("teams")) continue
            if (!file.name.endsWith(".dat") && file.name.indexOf('.') != file.name.lastIndexOf('.')) continue
            try {
                val inputStream: InputStream = client.getObject("FTB", s3ObjectSummary.key).objectContent
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val nbt = NBTUtils.readNBT(file)
            if (nbt != null) {
                var s = nbt.getString("ID")
                if (s.isEmpty()) {
                    s = FileUtils.getBaseName(file)
                }
                teamNBT[s] = nbt
                val uid = nbt.getShort("UID")
                val team = ForgeTeam(this, generateTeamUID(uid), s, TeamType.NAME_MAP[nbt.getString("Type")])
                addTeam(team)
                if (uid.toInt() == 0) {
                    team.markDirty()
                }
            }
        }

        fakePlayerTeam = object : ForgeTeam(this, 1.toShort(), "fakeplayer", TeamType.SERVER_NO_SAVE) {
            override fun markDirty() {
                this@UniverseMixin.markDirty()
            }
        }
        fakePlayer = FakeForgePlayer(this)
        fakePlayer.team = fakePlayerTeam
        fakePlayerTeam.setColor(EnumTeamColor.GRAY)
        CreateServerTeams(this).post()
        for (player in getPlayers()) {
            val nbt = playerNBT[player.id]
            if (nbt != null && !nbt.isEmpty) {
                player.team = getTeam(nbt.getString("TeamID"))
                player.deserializeNBT(nbt)
            }
            ForgePlayerLoadedEvent(player).post()
        }
        for (team in getTeams()) {
            if (!team.type.save) {
                continue
            }
            val nbt = teamNBT[team.id]
            if (nbt != null && !nbt.isEmpty) {
                team.deserializeNBT(nbt)
            }
            ForgeTeamLoadedEvent(team).post()
        }
        if (universeData.hasKey("FakePlayer")) {
            fakePlayer.deserializeNBT(universeData.getCompoundTag("FakePlayer"))
        }
        if (universeData.hasKey("FakeTeam")) {
            fakePlayerTeam.deserializeNBT(universeData.getCompoundTag("FakeTeam"))
        }
        fakePlayerTeam.owner = fakePlayer
        UniverseLoadedEvent.Post(this, data).post()
        UniverseLoadedEvent.Finished(this).post()
        FTBLibAPI.reloadServer(this, server, EnumReloadType.CREATED, ServerReloadEvent.ALL)
    }

    @Overwrite
    fun save() {

    }

    @Overwrite
    private fun onPlayerLoggedIn(player: EntityPlayerMP) {
        if (!player.server.playerList.canJoin(player.gameProfile)) {
            return
        }
        var p: ForgePlayer? = loadPlayer(player.uniqueID)
        if (p == null) {
            p = ForgePlayer(this as Universe, player.uniqueID, player.name)
            players.put(p.id, p)
            (p as ForgePlayerAccessor).callOnLoggedIn(player, this as Universe, true)
            val nbt = NBTTagCompound()
            nbt.setUniqueId("PlayerUUID", player.uniqueID)
            nbt.setString("Name", player.name)
            SyncUtil.publish(SyncType.PLAYER, nbt)
            val playerNBT = p.serializeNBT()
            playerNBT.setString("Name", p.name)
            playerNBT.setString("UUID", StringUtils.fromUUID(p.id))
            playerNBT.setString("TeamID", p.team.id)
            NBTUtils.writeNBT(p.getDataFile(""), playerNBT)
        } else {
            if (p.id != player.uniqueID || p.name != player.name) {
//                File old = p.getDataFile("");
                ((this as UniverseAccessor).getPlayerList() as HashMap).remove(p.id)
                p.profile = GameProfile(player.uniqueID, player.name)
                ((this as UniverseAccessor).getPlayerList() as HashMap).put(p.id, p)
                //                old.renameTo(p.getDataFile(""));
                p.markDirty()
                p.team.markDirty()
                markDirty()
            }
            (p as ForgePlayerAccessor).callOnLoggedIn(player, this as Universe, false)
        }
        FTBLib.LOGGER.info("Player logged in: " + serialize(p.serializeNBT()))
        FTBLib.LOGGER.info("Player team: " + serialize(p.team.serializeNBT()))
    }

    private fun loadPlayer(uuid: UUID): ForgePlayer? {
        val playerPath = "world/data/ftb_lib/players/$uuid.dat"
        if (!client.doesObjectExist("FTB", playerPath)) return null
        val inputStream: InputStream = client.getObject("FTB", playerPath).objectContent
        val file = File(playerPath)
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val nbt = NBTUtils.readNBT(file)
        var uuidString = nbt!!.getString("UUID")
        if (uuidString.isEmpty()) {
            uuidString = FileUtils.getBaseName(file)
            FileUtils.deleteSafe(file)
        }
        val player = ForgePlayer(this as Universe, uuid, nbt.getString("Name"))
        players.put(uuid, player)
        player.team = getTeam(nbt.getString("TeamID"))
        player.deserializeNBT(nbt)
        return player
    }

    fun internalRemoveTeam(team: ForgeTeam) {
        teamMap.remove(team.uid)
        (teams as HashMap).remove(team.id)
        markDirty()
        clearCache()
    }
}