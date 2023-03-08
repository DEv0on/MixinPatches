package net.remodded.mixinpatches.mixins.common.forestry

import forestry.api.core.EnumHumidity
import forestry.api.core.EnumTemperature
import forestry.api.genetics.EnumTolerance
import forestry.core.utils.ClimateUtil
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Mixin(ClimateUtil::class)
class ClimateUtilMixin {
    @Overwrite
    fun isWithinLimits(
        temperature: EnumTemperature, humidity: EnumHumidity,
        baseTemp: EnumTemperature, tolTemp: EnumTolerance,
        baseHumid: EnumHumidity, tolHumid: EnumTolerance
    ): Boolean = true

    @Overwrite
    fun isWithinLimits(temperature: EnumTemperature, baseTemp: EnumTemperature, tolTemp: EnumTolerance): Boolean = true

    @Overwrite
    fun isWithinLimits(humidity: EnumHumidity, baseHumid: EnumHumidity, tolHumid: EnumTolerance): Boolean = true
}