package net.remodded.mixinpatches.mixins.common.ae2

import appeng.core.AELog
import appeng.me.GridStorage
import appeng.me.GridStorageSearch
import net.minecraftforge.common.config.Configuration
import net.remodded.recore.database.Redis
import net.remodded.recore.database.Redis.client
import org.redisson.api.RMap
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.lang.ref.WeakReference


@Mixin(targets = ["appeng.core.worlddata.StorageData"])
class StorageDataMixin {
    private lateinit var gridMap: RMap<Long, String>

    @Shadow
    lateinit var loadedStorage: MutableMap<GridStorageSearch, WeakReference<GridStorageSearch>>
    @Shadow
    var lastGridStorage: Long = 0

    @Inject(method = ["<init>(Lnet/minecraftforge/common/config/Configuration;)V"], at = [At("TAIL")])
    fun constructor(settingsFile: Configuration, ci: CallbackInfo) {
        val client = Redis.client
        gridMap = client.getMap("AE2-GridData")
    }

    @Overwrite
    fun getGridStorage(storageID: Long): GridStorage {
        val gss = GridStorageSearch(storageID)
        val result: WeakReference<GridStorageSearch>? = this.loadedStorage[gss]

        if (result?.get() == null) {
            var data = gridMap[storageID]
            if (data == null) data = ""
            val thisStorage = GridStorage(data, storageID, gss)
            gss.gridStorage = WeakReference(thisStorage)
            this.loadedStorage[gss] = WeakReference(gss)
            return thisStorage
        }
        val gs = result.get()!!.gridStorage.get()
        gridMap[gs!!.id] = gs.value
        return gs
    }

    @Inject(method = ["getNewGridStorage"], at = [At("RETURN")], cancellable = true)
    fun getNewGridStorage(cir: CallbackInfoReturnable<GridStorage>) {
        gridMap[cir.returnValue.id] = cir.returnValue.value
    }

    @Overwrite
    fun nextGridStorage(): Long {
        val aeConfig = client.getMap<String, String>("AEConfig")
        val lastGridStorage = aeConfig["lastGridStorage"]
        val newGridStorageID = (lastGridStorage!!.toInt() + 1).toString()
        aeConfig["lastGridStorage"] = newGridStorageID
        return lastGridStorage.toInt().toLong()
    }

    @Overwrite
    fun destroyGridStorage(id: Long) {
        this.gridMap.remove(id)
    }

    @Overwrite
    fun onWorldStart() {
        val aeConfig = client.getMap<String, String>("AEConfig")
        if (!aeConfig.containsKey("lastGridStorage")) aeConfig["lastGridStorage"] = "0"

        val lastString = aeConfig["lastGridStorage"]

        try {
            this.lastGridStorage = lastString!!.toLong()
        } catch (err: NumberFormatException) {
            AELog.warn("The config contained a value which was not represented as a Long: %s", lastString)
            this.lastGridStorage = 0
        }
    }
}