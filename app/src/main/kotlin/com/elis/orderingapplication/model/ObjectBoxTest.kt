package com.elis.orderingapplication.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ObjectBoxTest(
    @Id
    var id: Long = 0,
    var name: String? = null
)
