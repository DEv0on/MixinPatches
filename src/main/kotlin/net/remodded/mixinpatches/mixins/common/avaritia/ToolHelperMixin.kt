@file:Mixin(ToolHelper::class)

package net.remodded.mixinpatches.mixins.common

import morph.avaritia.init.ModItems
import morph.avaritia.util.ToolHelper
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite

@Overwrite
fun removeBlockWithDrops(
    player: EntityPlayer,
    stack: ItemStack,
    world: World,
    pos: BlockPos,
    target: Block?,
    validMaterials: Set<Material>
) {
    if (world.isBlockLoaded(pos)) {
        val state = world.getBlockState(pos)
        val block = state.block
        if (!world.isRemote) {
            if ((target == null || target === state.block) && !block.isAir(
                    state,
                    world,
                    pos
                ) && block.blockState.block != Blocks.BEDROCK
            ) {
                val material = state.material
                if (block === Blocks.GRASS && stack.item === ModItems.infinity_axe) {
                    world.setBlockState(pos, Blocks.DIRT.defaultState)
                }
                if (block.canHarvestBlock(world, pos, player) && validMaterials.contains(material)) {
                    val event = BreakEvent(world, pos, state, player)
                    MinecraftForge.EVENT_BUS.post(event)
                    if (!event.isCanceled) {
                        if (!player.capabilities.isCreativeMode) {
                            val tile = world.getTileEntity(pos)
                            block.onBlockHarvested(world, pos, state, player)
                            if (block.removedByPlayer(state, world, pos, player, true)) {
                                block.onPlayerDestroy(world, pos, state)
                                block.harvestBlock(world, player, pos, state, tile, stack)
                                return
                            }
                        } else {
                            world.setBlockToAir(pos)
                        }
                    }
                }
            }
        }
    }
}