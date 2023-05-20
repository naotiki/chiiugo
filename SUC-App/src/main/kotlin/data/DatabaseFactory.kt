package data

import data.Languages.long
import data.Languages.varchar
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Languages)
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
