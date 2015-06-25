package org.restler.testserver.springdata

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

Entity class Person(
        Id var id: String = "",
        Column var name: String = ""
) : Serializable
