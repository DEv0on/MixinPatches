package net.remodded.mixinpatches

import WayofTime.bloodmagic.core.data.BMWorldSavedData
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.redisson.Redisson
import org.redisson.api.RMap
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import java.util.*

@Mod(modid = Core.MODID, version = Core.VERSION, name = Core.NAME, acceptableRemoteVersions = "*")
class Core {
    companion object {
        const val MODID = "mixinpatches"
        const val NAME = "mixinpatches"
        const val VERSION = "1.0.0"
        val logger = LogManager.getLogger("MixinPatches")

        lateinit var worldDataInstance: BMWorldSavedData
        lateinit var networkData: RMap<UUID, String>

        const val httpProxyEnabled = true
        const val httpProxyAddress = ""
        const val httpProxyPort = 3128

        private const val redisHostname = "redis"
        private const val redisPort = "6379"
        private val redisUsername: String? = null
        private val redisPassword: String = ""

        var redisClient: RedissonClient

        private fun establishRedisConnection(): RedissonClient {
            val config = Config()
            config.codec = StringCodec()
            val serverConfig = config.useSingleServer()
            serverConfig.connectionMinimumIdleSize = 2
            serverConfig.connectionPoolSize = 6
            serverConfig.address = "redis://$redisHostname:$redisPort"
            if (redisUsername != null) {
                serverConfig.username = redisUsername
            }
            serverConfig.password = redisPassword
            return Redisson.create(config)
        }

        init {
            redisClient = establishRedisConnection()
        }
    }
}
