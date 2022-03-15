package com.notes.domain

import java.time.LocalDateTime

data class Note (
    val id: Long,
    var title: String,
    var content: String,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime
)