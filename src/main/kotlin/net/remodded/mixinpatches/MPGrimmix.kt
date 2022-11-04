package net.remodded.mixinpatches

import io.github.crucible.grimoire.common.api.grimmix.Grimmix
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType
import net.minecraftforge.fml.common.Loader

@Grimmix(id = "mpgrimmix", name = "MixinPatches Grimmix")
class MPGrimmix : GrimmixController() {
    override fun buildMixinConfigs(event: IConfigBuildingEvent) {
        val builder = event.createBuilder("mixinpatches/mixins.mixinpatches.json")
            .mixinPackage("net.remodded.mixinpatches.mixins")
            .refmap("@MIXIN_REFMAP@")
            .verbose(true)
            .required(true)
            .configurationType(ConfigurationType.MOD)

        if (Loader.isModLoaded("Avaritia"))
            builder.commonMixins("common.avaritia.*")

        if (Loader.isModLoaded("appliedenergistics2"))
            builder.commonMixins("common.ae2.*")

        if (Loader.isModLoaded("bloodmagic"))
            builder.commonMixins("common.bloodmagic.*")

        if (Loader.isModLoaded("botania"))
            builder.commonMixins("common.botania.*")

        if (Loader.isModLoaded("ftblib"))
            builder.commonMixins("common.ftblibrary.*")

        if (Loader.isModLoaded("ftbquests"))
            builder.commonMixins("common.ftbquests.*")

        if (Loader.isModLoaded("mekanism"))
            builder.commonMixins("common.mekanism.*")

        builder.build()
    }
}