package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.events.team.ForgeTeamPlayerJoinedEvent
import com.feed_the_beast.ftblib.events.team.ForgeTeamPlayerLeftEvent
import com.feed_the_beast.ftblib.lib.EnumTeamStatus
import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.feed_the_beast.ftblib.lib.data.TeamType
import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftblib.lib.util.FinalIDObject
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.feed_the_beast.ftblib.lib.util.StringUtils
import net.minecraft.nbt.NBTTagCompound
import net.remodded.mixinpatches.utils.ftblibrary.SyncType
import net.remodded.mixinpatches.utils.ftblibrary.SyncUtil
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.io.File

@Mixin(ForgeTeam::class)
abstract class ForgeTeamMixin: FinalIDObject(null, 0) {

    @Shadow lateinit var requestingInvite: Collection<ForgePlayer>
    @Shadow lateinit var universe: Universe
    @Shadow lateinit var type: TeamType

    @Shadow abstract fun isValid(): Boolean
    @Shadow abstract fun isOwner(player: ForgePlayer?): Boolean
    @Shadow abstract fun isInvited(player: ForgePlayer?): Boolean
    @Shadow abstract fun isMember(player: ForgePlayer?): Boolean
    @Shadow abstract fun markDirty()
    @Shadow abstract fun getMembers(): List<ForgePlayer>
    @Shadow abstract fun delete()
    @Shadow abstract fun setStatus(player: ForgePlayer?, status: EnumTeamStatus): Boolean
    @Shadow abstract fun getUID(): Short
    @Shadow abstract fun serializeNBT(): NBTTagCompound

    @Overwrite
    fun addMember(player: ForgePlayer, simulate: Boolean): Boolean {
        val wasAdded: Boolean = internalAddMember(player, simulate)
        if (!wasAdded) return false
        if (!simulate) {
            save()
            val nbt = NBTTagCompound()
            nbt.setString("TeamId", getId())
            nbt.setUniqueId("PlayerUUID", player.id)
            SyncUtil.publish(SyncType.TEAM_JOIN, nbt)
            val playerNBT = player.serializeNBT()
            playerNBT.setString("Name", player.name)
            playerNBT.setString("UUID", StringUtils.fromUUID(player.id))
            playerNBT.setString("TeamID", player.team.id)
            NBTUtils.writeNBT(player.getDataFile(""), playerNBT)
        }
        return true
    }

    @Overwrite
    fun removeMember(player: ForgePlayer): Boolean {
        val wasRemoved: Boolean = internalRemoveMember(player)
        if (wasRemoved) {
            save()
            val nbt = NBTTagCompound()
            nbt.setString("TeamId", getId())
            nbt.setUniqueId("PlayerUUID", player.id)
            SyncUtil.publish(SyncType.TEAM_LEAVE, nbt)
            val playerNBT = player.serializeNBT()
            playerNBT.setString("Name", player.name)
            playerNBT.setString("UUID", StringUtils.fromUUID(player.id))
            playerNBT.setString("TeamID", player.team.id)
            NBTUtils.writeNBT(player.getDataFile(""), playerNBT)
        }
        return wasRemoved
    }

    @Overwrite
    fun getDataFile(ext: String): File {
        val dir = File(universe.worldDirectory, "data/ftb_lib/teams/")
        if (ext.isEmpty()) return File(dir, "${getId()}.dat")
        val extDir = File(dir, ext)
        return File(extDir, "${getId()}.dat")
    }

    fun internalAddMember(player: ForgePlayer?, simulate: Boolean): Boolean {
        if (isValid() && (isOwner(player) || isInvited(player)) && !isMember(player)) {
            if (!simulate) {
                universe.clearCache()
                player!!.team = this as ForgeTeam
                players.remove(player)
                (requestingInvite as HashSet).remove(player)
                val event = ForgeTeamPlayerJoinedEvent(player)
                event.post()
                if (event.displayGui != null) {
                    event.displayGui!!.run()
                }
                player.markDirty()
                markDirty()
            }
            return true
        }
        return false
    }

    fun internalRemoveMember(player: ForgePlayer?): Boolean {
        if (!isValid() || !isMember(player)) {
            return false
        } else if (getMembers().size == 1) {
            universe.clearCache()
            ForgeTeamPlayerLeftEvent(player).post()
            if (type.isPlayer) {
                delete()
            } else {
                setStatus(player, EnumTeamStatus.NONE)
            }
            player!!.team = universe.getTeam("")
            player.markDirty()
            markDirty()
        } else if (isOwner(player)) {
            return false
        }
        universe.clearCache()
        ForgeTeamPlayerLeftEvent(player).post()
        player!!.team = universe.getTeam("")
        setStatus(player, EnumTeamStatus.NONE)
        player.markDirty()
        markDirty()
        return true
    }

    private fun save() {
        if (type.save && isValid()) {
            val nbt = serializeNBT()
            nbt.setString("ID", getId())
            nbt.setShort("UID", getUID())
            nbt.setString("Type", type.getName())
            NBTUtils.writeNBT(getDataFile(""), nbt)
        }
    }

}