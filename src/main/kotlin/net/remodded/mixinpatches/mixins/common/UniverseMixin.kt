package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.events.ServerReloadEvent
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent
import com.feed_the_beast.ftblib.lib.EnumReloadType
import com.feed_the_beast.ftblib.lib.EnumTeamColor
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI
import com.feed_the_beast.ftblib.lib.data.FakeForgePlayer
import com.feed_the_beast.ftblib.lib.data.TeamType
import com.feed_the_beast.ftblib.lib.data.Universe
import com.feed_the_beast.ftblib.lib.util.NBTUtils
import com.feed_the_beast.ftblib.lib.util.StringUtils
import com.feed_the_beast.ftblib.lib.util.misc.IScheduledTask
import com.feed_the_beast.ftblib.lib.util.misc.TimeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.remodded.mixinpatches.util.UniverseUtils
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.io.File
import java.util.*

@Mixin(Universe::class)
class UniverseMixin {
    @Shadow
    var uuid: UUID? = null

    @Shadow
    fun getWorldDirectory(): File {
        return File("")
    }

    @Overwrite
    fun scheduleTask(type: TimeType, time: Long, task: IScheduledTask) {

    }

    @Overwrite
    fun scheduleTask(id: ResourceLocation, type: TimeType, time: Long, data: NBTTagCompound) {

    }

    @Overwrite
    private fun load() {
        val folder = File(getWorldDirectory(), "data/ftb_lib/")
        var universeData = NBTUtils.readNBT(File(folder, "universe.dat"))

        if (universeData == null)
            universeData = NBTTagCompound()

        val u = Universe.get()

        uuid = StringUtils.fromString(universeData.getString("UUID"))

        if (uuid != null && uuid!!.leastSignificantBits == 0L && uuid!!.mostSignificantBits == 0L)
            uuid = null

        val data = universeData.getCompoundTag("Data")
        UniverseLoadedEvent.Pre(u, data).post()

        u.fakePlayerTeam = UniverseUtils.getFakeTeam(u, 1.toShort(), "fakeplayer", TeamType.SERVER_NO_SAVE)

        u.fakePlayer = FakeForgePlayer(u)
        u.fakePlayer.team = u.fakePlayerTeam
        u.fakePlayerTeam.color = EnumTeamColor.GRAY

        UniverseLoadedEvent.CreateServerTeams(u).post()

        if (universeData.hasKey("FakePlayer"))
            u.fakePlayer.deserializeNBT(universeData.getCompoundTag("FakePlayer"))

        if (universeData.hasKey("FakeTeam"))
            u.fakePlayerTeam.deserializeNBT(universeData.getCompoundTag("FakeTeam"))

        u.fakePlayerTeam.owner = u.fakePlayer

        UniverseLoadedEvent.Post(u, universeData.getCompoundTag("Data")).post()
        UniverseLoadedEvent.Finished(u).post()

        FTBLibAPI.reloadServer(u, u.server, EnumReloadType.CREATED, ServerReloadEvent.ALL)
    }
}