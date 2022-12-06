package net.remodded.mixinpatches

import io.github.crucible.grimoire.common.api.grimmix.Grimmix
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType
import net.minecraftforge.fml.common.Loader

@Grimmix(id = "mpgrimmix", name = "MixinPatches Grimmix")
class MPGrimmix : GrimmixController() {
    override fun buildMixinConfigs(event: IConfigBuildingEvent) {
        event.createBuilder("mixinpatches/mixins.mixinpatches.json")
            .mixinPackage("net.remodded.mixinpatches.mixins")
            .commonMixins("common.avaritia.*")
            .commonMixins("common.ae2.*")
            .commonMixins("common.bloodmagic.*")
            .commonMixins("common.botania.*")
            .commonMixins("common.ftblibrary.*")
            .commonMixins("common.ftbquests.*")
            .commonMixins("common.mekanism.*")
            .refmap("@MIXIN_REFMAP@")
            .verbose(true)
            .required(true)
            .configurationType(ConfigurationType.MOD)
            .build()
    }
}