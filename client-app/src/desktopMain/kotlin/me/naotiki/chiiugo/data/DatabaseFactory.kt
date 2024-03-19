package me.naotiki.chiiugo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(DailyStatistics)
        }
    }
}
suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

object Languages : IntIdTable() {
    val name=varchar("name",50)
    val extension = varchar("extension",16)
    val totalTime=long("time")
}
class Language(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Language>(Languages)
    var name     by Languages.name
    var extension by Languages.extension
    var totalTime by Languages.totalTime
}

object DailyStatistics : IntIdTable() {
    val date=date("date").clientDefault {Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date}
    val totalTime=long("time")
}
class DailyStatistic(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DailyStatistic>(DailyStatistics)
    var date     by DailyStatistics.date
    var totalTime by DailyStatistics.totalTime
}

/*
object Languages : IdTable<Long>() {
    val name=varchar("name",50)
    val extension = varchar("extension",16)
    val totalTime=long("time")
}
class Language(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Language>(Languages)
    var name     by Languages.name
    var extension by Languages.extension
    var totalTime by Languages.totalTime
}*/
