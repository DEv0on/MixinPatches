@file:Mixin(SpongeUsernameCache::class)

package net.remodded.mixinpatches.core.mixins.common.sponge

import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.common.util.SpongeUsernameCache
import java.util.*

@Shadow
private lateinit var map: MutableMap<UUID, String>

@Shadow
private var dirty: Boolean = false

@Overwrite
fun load() {}

@Overwrite
fun save() {
    dirty = false
}

@Inject(method = ["<clinit>"], at = [At("TAIL")])
private fun staticInit(ci: CallbackInfo) {
    map = Core.redisClient.getMap("UsernameCache")
}

@Overwrite
fun getMap(): Map<UUID, String> {
    return (map as Map<String, String>).mapKeys { (k, v) -> UUID.fromString(k) }
}
