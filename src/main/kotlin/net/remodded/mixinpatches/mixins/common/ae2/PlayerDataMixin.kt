package net.remodded.mixinpatches.mixins.common.ae2

import appeng.core.AppEng
import com.google.common.base.Preconditions
import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.config.Configuration
import net.remodded.recore.database.Redis
import net.remodded.recore.database.Redis.client
import org.redisson.api.RMap
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Mixin(targets = ["appeng.core.worlddata.PlayerData"])
class PlayerDataMixin {
    private lateinit var playerMap: RMap<String, String>
    private lateinit var reversePlayerMap: RMap<String, String>

    @Shadow
    var lastPlayerID: Int = 0
    @Shadow
    lateinit var config: Configuration

    @Inject(method = ["<init>(Lnet/minecraftforge/common/config/Configuration;)V"], at = [At("TAIL")])
    fun constructor(configFile: Configuration, ci: CallbackInfo) {
        val client = Redis.client
        playerMap = client.getMap("AE2-PlayerData")
        reversePlayerMap = client.getMap("AE2-PlayerDataReverse")
    }

    @Overwrite
    fun getPlayerFromID(playerId: Int): EntityPlayer? {
        val uuidString = playerMap.get(playerId.toString()) ?: return null
        val uuid = UUID.fromString(uuidString)
        for (player in AppEng.proxy.players) {
            if (player.uniqueID == uuid) {
                return player
            }
        }
        return null
    }

    @Overwrite
    fun getPlayerID(profile: GameProfile): Int {
        Preconditions.checkNotNull(profile)
        Preconditions.checkNotNull(this.config.getCategory("players"))
        Preconditions.checkState(profile.isComplete)

        val uuid = profile.id.toString()

        val playerId = reversePlayerMap[uuid]
        if (playerId != null)
            return playerId.toInt()

        val newPlayerID: Int = this.nextPlayer()
        playerMap[newPlayerID.toString()] = uuid
        reversePlayerMap[uuid] = newPlayerID.toString()

        return newPlayerID
    }

    @Overwrite
    fun nextPlayer(): Int {
        val aeConfig = client.getMap<String, String>("AEConfig")
        val lastPlayerID = aeConfig["lastPlayerID"]
        val newPlayerID = (lastPlayerID!!.toInt() + 1).toString()
        aeConfig["lastPlayerID"] = newPlayerID
        return lastPlayerID.toInt()
    }

    @Overwrite
    fun onWorldStart() {
        val aeConfig = client.getMap<String, String>("AEConfig")
        if (!aeConfig.containsKey("lastPlayerID")) aeConfig["lastPlayerID"] = "0"
        var lastPlayerID = aeConfig["lastPlayerID"]
        this.lastPlayerID = lastPlayerID!!.toInt()

        config.save()
    }

}