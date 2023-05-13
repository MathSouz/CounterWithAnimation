package com.teteukt.counter.data

import com.teteukt.counter.domain.TimeSchedule
import java.util.Date

class RepositoryImpl(private val timeSchedules: TimeSchedules) : Repository {
    override suspend fun getTimeSchedules(): List<TimeSchedule> =
        timeSchedules.getAllTimeSchedules()

    override suspend fun getTimeScheduleById(id: Int): TimeSchedule? =
        timeSchedules.getTimeScheduleById(id)

    override suspend fun createSchedule(timeSchedule: TimeSchedule, time: Date, title: String) =
        timeSchedules.createSchedule(timeSchedule, time, title)
}