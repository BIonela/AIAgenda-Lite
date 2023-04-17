package com.example.aiagenda.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

fun calculateDaysLeft(startDay: String, endDay: String): Int {
    val formatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
    val startDayDate: LocalDate = LocalDate.parse(startDay, formatter)
    val endDayDate: LocalDate = LocalDate.parse(endDay, formatter)
    val submitTime = ChronoUnit.DAYS.between(startDayDate, endDayDate).toDouble()

    val currentDate = LocalDate.now()
    val currentDiff = ChronoUnit.DAYS.between(startDayDate, currentDate).toDouble()

    if (currentDiff < 0.0) {
        return 0
    }

    return (currentDiff / submitTime * 100.0).roundToInt()
}