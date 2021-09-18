package net.remodded.mixinpatches.database

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

object Mongo {
    val logger: Logger = LogManager.getLogger("FTBDatabase")
    val database: CoroutineDatabase = net.remodded.recore.database.Mongo.database;
    val ftbCollection: CoroutineCollection<FTBCollection> = database.getCollection("FTB")
}