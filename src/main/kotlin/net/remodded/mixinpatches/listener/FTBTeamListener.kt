package net.remodded.mixinpatches.listener

import com.feed_the_beast.ftblib.events.team.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.remodded.mixinpatches.Core
import net.remodded.mixinpatches.util.UniverseUtils
import net.remodded.recore.database.Redis

@Mod.EventBusSubscriber
class FTBTeamListener {
    companion object {
        @JvmStatic
        @SubscribeEvent
        fun onForgeTeamCreated(event: ForgeTeamCreatedEvent) {
            UniverseUtils.saveTeam(event.team)
            Core.logger.warn("Saved team ${event.team.title.unformattedText}!")
        }
        @JvmStatic
        @SubscribeEvent
        fun onForgeTeamOwnerChanged(event: ForgeTeamOwnerChangedEvent) {
            UniverseUtils.saveTeam(event.team)
            Core.logger.warn("Saved team ${event.team.title.unformattedText}!")
        }
        @JvmStatic
        @SubscribeEvent
        fun onForgeTeamPlayerJoined(event: ForgeTeamPlayerJoinedEvent) {
            UniverseUtils.saveTeam(event.team)
            UniverseUtils.savePlayer(event.player)
            Core.logger.warn("Saved team ${event.team.title.unformattedText}!")
            Core.logger.warn("Saved player ${event.player.name}!")
        }
        @JvmStatic
        @SubscribeEvent
        fun onForgeTeamPlayerLeft(event: ForgeTeamPlayerLeftEvent) {
            UniverseUtils.saveTeam(event.team)
            UniverseUtils.savePlayer(event.player)
            Core.logger.warn("Saved team ${event.team.title.unformattedText}!")
            Core.logger.warn("Saved player ${event.player.name}!")
        }

        @JvmStatic
        @SubscribeEvent
        fun onForgeTeamSave(event: ForgeTeamSavedEvent) {
            val storage = Redis.client.getListMultimap<String, String>("FTBSync")
            storage.removeAll(event.team.id)
        }
    }
}