package net.remodded.mixinpatches.mixins.common.ftbquests

import com.feed_the_beast.ftblib.lib.net.MessageToServer
import com.feed_the_beast.ftbquests.FTBQuests
import com.feed_the_beast.ftbquests.net.edit.MessageMoveQuest
import net.minecraft.entity.player.EntityPlayerMP
import net.remodded.mixinpatches.Core
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MessageMoveQuest::class)
abstract class MessageMoveQuestMixin : MessageToServer() {

    @Inject(method = ["onMessage"], at = [At("HEAD")], cancellable = true)
    fun onMessage(player: EntityPlayerMP, ci: CallbackInfo) {
        if (!FTBQuests.canEdit(player)) {
            Core.logger.error("${player.name} tried to exploit MessageMoveQuest.class (FTBQuests)!")
            ci.cancel()
        }
    }
}