package card

import KuadroDatasource
import com.henriquemisael.card.CardsTable
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.cardRoutes() {
    routing {
        route("/card") {
            cardRoute()
        }
    }
}

fun connect() {
    Database.connect(KuadroDatasource.instance!!)
}

fun Route.cardRoute() {
    get {
        connect()
        call.respond(transaction {
            CardsTable.selectAll()
                .map { Card(it[CardsTable.id].value, it[CardsTable.title], it[CardsTable.description]) }
        })
    }

    post {
        val card = call.receive<Card>()
        connect()
        transaction {
            CardsTable.insert {
                it[title] = card.title
                it[description] = card.description
            }
        }
        call.respond(HttpStatusCode.Created)
    }

    route("/{id}") {
        get {

            val id = if (call.parameters.contains("id")) call.parameters["id"]!!.toIntOrNull() else null
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                connect()
                call.respond(transaction {
                    val select = CardsTable.select { CardsTable.id eq id }
                    if (select.count() == 0L) HttpStatusCode.NotFound
                    else
                        with(select.first()) {
                            Card(
                                this[CardsTable.id].value,
                                this[CardsTable.title],
                                this[CardsTable.description]
                            )
                        }
                })
            }
        }

        put {

            val id = if (call.parameters.contains("id")) call.parameters["id"]!!.toIntOrNull() else null
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val card = call.receive<Card>()
                connect()
                transaction {
                    CardsTable.update({ CardsTable.id eq id }) {
                        it[title] = card.title
                        it[description] = card.description
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }

        delete {

            val id = if (call.parameters.contains("id")) call.parameters["id"]!!.toIntOrNull() else null
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                connect()
                transaction {
                    CardsTable.deleteWhere { CardsTable.id eq id }
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

}
