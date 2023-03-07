package net.remodded.mixinpatches.mixins.common.mekanism

import mekanism.common.tier.FluidTankTier
import mekanism.common.tile.TileEntityFluidTank
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TileEntityFluidTank::class)
abstract class TileEntityFluidTankMixin {
    @Shadow
    lateinit var tier: FluidTankTier

    @Inject(method = ["manageInventory"], at = [At("INVOKE", target = "Lnet/minecraftforge/fluids/FluidTank;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V")])
    private fun manageInventory(ci: CallbackInfo) {
        if (this.tier == FluidTankTier.CREATIVE)
            ci.cancel()
    }
}