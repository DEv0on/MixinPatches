package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.Universe
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import java.util.*

@Mixin(Universe::class)
interface UniverseAccessor {
    @Accessor("players")
    fun getPlayerList(): Map<UUID, ForgePlayer>
}