package net.remodded.mixinpatches

import io.github.crucible.grimoire.common.api.grimmix.Grimmix
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType

@Grimmix(id = "mpgrimmix", name = "MixinPatches Grimmix")
class MPGrimmix : GrimmixController() {
    override fun buildMixinConfigs(event: IConfigBuildingEvent) {
        event.createBuilder("mixinpatches/mixins.mixinpatches.json")
                .mixinPackage("net.remodded.mixinpatches.mixins")
                .commonMixins("common.ae2.*")
                .commonMixins("common.avaritia.*")
                .commonMixins("common.bloodmagic.*")
                .commonMixins("common.botania.*")
                .commonMixins("common.cctweaked.*")
                .commonMixins("common.energycontrol.*")
//              .commonMixins("common.ftblibrary.*")
                .commonMixins("common.ftbquests.*")
                .commonMixins("common.mekanism.*")
                .refmap("@MIXIN_REFMAP@")
                .verbose(true)
                .required(true)
                .configurationType(ConfigurationType.MOD)
                .build()
        event.createBuilder("mixinpatches/coremod.mixinpatches.json")
                .mixinPackage("net.remodded.mixinpatches.core.mixins")
                .commonMixins("common.sponge.*")
                .refmap("@MIXIN_REFMAP@")
                .verbose(true)
                .required(true)
                .configurationType(ConfigurationType.CORE)
                .build()
    }
}