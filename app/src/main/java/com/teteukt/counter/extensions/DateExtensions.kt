package com.teteukt.counter.extensions

import java.util.Calendar
import java.util.Date

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Calendar.toDate(): Date = this.time

fun Calendar.fromHourAndMinute(hours: Int, minute: Int): Calendar {
    this.set(Calendar.HOUR_OF_DAY, hours)
    this.set(Calendar.MINUTE, minute)
    return this
}