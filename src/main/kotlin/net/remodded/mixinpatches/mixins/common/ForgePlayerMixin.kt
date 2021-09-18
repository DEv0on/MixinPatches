package net.remodded.mixinpatches.mixins.common

import com.feed_the_beast.ftblib.lib.data.ForgePlayer
import com.feed_the_beast.ftblib.lib.data.ForgeTeam
import com.mojang.authlib.GameProfile
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import java.io.File

@Mixin(ForgePlayer::class, remap = false)
abstract class ForgePlayerMixin {

    @Shadow
    lateinit var team: ForgeTeam

    @Shadow
    abstract fun getProfile(): GameProfile

    @Overwrite
    fun getDataFile(ext: String): File {
        val dir = File(team.universe.worldDirectory, "data/ftb_lib/players/")

        return File(dir, getProfile().id.toString() + ".dat")
    }
}