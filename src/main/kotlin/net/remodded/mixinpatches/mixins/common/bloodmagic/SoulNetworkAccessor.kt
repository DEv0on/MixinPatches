package net.remodded.mixinpatches.mixins.common.bloodmagic

import WayofTime.bloodmagic.core.data.SoulNetwork
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import java.util.UUID

@Mixin(SoulNetwork::class)
interface SoulNetworkAccessor {

    @Accessor("playerId")
    fun setPlayerId(playerId: UUID)

}