package com.example.flyaway.domain.model

import java.time.LocalDateTime

data class AppAccessLog(
    val id: String,
    val userId: String,
    val action: String,
    val timestamp: LocalDateTime
) 