package com.henriquemisael.card

import org.jetbrains.exposed.dao.id.IntIdTable

object CardsTable : IntIdTable(name = "cards") {
    val title = varchar("title", 64)
    val description = text("description")
}
