package com.teteukt.counter.data

import com.teteukt.counter.domain.TimeSchedule
import java.util.Date

interface Repository {
    suspend fun getTimeSchedules(): List<TimeSchedule>
    suspend fun getTimeScheduleById(id: Int): TimeSchedule?
    suspend fun createSchedule(timeSchedule: TimeSchedule, time: Date, title: String)
}