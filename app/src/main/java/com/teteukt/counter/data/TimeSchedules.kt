package com.teteukt.counter.data

import com.teteukt.counter.domain.Schedule
import com.teteukt.counter.domain.TimeSchedule
import java.util.Date

class TimeSchedules {
    private var lastTimeScheduleIndex = 0
    private var lastScheduleIndex = 0
    private val timeSchedules = mutableListOf(
        TimeSchedule(0, "Day", listOf()),
        TimeSchedule(1, "Dawn", listOf()),
        TimeSchedule(2, "Night", listOf()),
    )

    fun createSchedule(timeSchedule: TimeSchedule, time: Date, title: String) {
        val newSchedule = Schedule(id = lastScheduleIndex++, time = time, title = title)
        timeSchedules.indexOfFirst { it.id == timeSchedule.id }.takeIf { it > -1 }?.let {
            timeSchedules.set(
                it,
                timeSchedules[it].copy(schedules = timeSchedules[it].schedules + listOf(newSchedule))
            )
        }
    }

    fun getAllTimeSchedules(): List<TimeSchedule> = timeSchedules

    fun getTimeScheduleById(id: Int): TimeSchedule? = timeSchedules.firstOrNull { it.id == id }
}