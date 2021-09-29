package net.remodded.mixinpatches.util

import net.minecraft.nbt.NBTTagCompound

data class UpdateDetails(
    val teamId: String,
    val updateType: String
) {
    constructor(nbt: NBTTagCompound) : this(
        nbt.getString("TeamID"),
        nbt.getString("UpdateType")
    )
}
