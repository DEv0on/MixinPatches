package net.remodded.mixinpatches.mixins.common.forestry

import forestry.api.core.EnumHumidity
import forestry.api.core.EnumTemperature
import forestry.apiculture.worldgen.Hive
import net.minecraft.world.biome.Biome
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Mixin(Hive::class)
class HiveMixin {
    @Overwrite
    fun isGoodBiome(biome: Biome): Boolean = true
    @Overwrite
    fun isGoodHumidity(humidity: EnumHumidity): Boolean = true
    @Overwrite
    fun isGoodTemperature(temperature: EnumTemperature): Boolean = true
}