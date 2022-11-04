@file:Suppress("SENSELESS_COMPARISON")

package net.remodded.mixinpatches.mixins.common.ae2

import appeng.me.GridNode
import appeng.me.GridStorage
import net.minecraft.nbt.NBTTagCompound
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow

@Mixin(GridNode::class)
abstract class GridNodeMixin {
    private var gridStorageId: Long = -1
    @Shadow
    var playerID = -1
    @Shadow
    lateinit var myStorage: GridStorage

    @Shadow
    abstract fun getLastSecurityKey(): Long

    @Overwrite
    fun saveToNBT(name: String, nodeData: NBTTagCompound) {
        val node = NBTTagCompound()

        node.setInteger("p", this.playerID)
        node.setLong("k", this.getLastSecurityKey())

        if (this.myStorage != null)
            node.setLong("g", this.myStorage.id)
        else
            node.setLong("g", gridStorageId)


        nodeData.setTag(name, node)
    }
}