package net.remodded.mixinpatches.util

import net.minecraft.nbt.NBTTagCompound

data class UpdateDetails(
    val teamId: String,
    val updateType: SyncUtils.UpdateType
) {
    constructor(nbt: NBTTagCompound) : this(
        nbt.getString("TeamID"),
        SyncUtils.UpdateType.valueOf(nbt.getString("UpdateType"))
    )
}