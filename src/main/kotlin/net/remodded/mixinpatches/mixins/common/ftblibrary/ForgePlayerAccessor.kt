package net.remodded.mixinpatches.mixins.common.ftblibrary

import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.Universe
import net.minecraft.entity.player.EntityPlayerMP
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(ForgePlayer::class)
interface ForgePlayerAccessor {
    @Invoker
    fun callOnLoggedIn(player: EntityPlayerMP, universe: Universe, firstLogin: Boolean)

}