package com.teteukt.counter

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.teteukt.counter.data.Repository
import com.teteukt.counter.domain.TimeSchedule
import java.util.Date

class MainViewModel(private val repository: Repository) : BaseViewModel() {
    private val _timeSchedules = mutableStateOf(listOf<TimeSchedule>())
    val timeSchedules: State<List<TimeSchedule>> = _timeSchedules

    private val _loadingCreateSchedule = mutableStateOf(listOf<TimeSchedule>())
    val loadingCreateSchedule: State<List<TimeSchedule>> = _loadingCreateSchedule

    fun isTimeScheduleLoading(timeScheduleId: Int) =
        loadingCreateSchedule.value.firstOrNull { it.id == timeScheduleId } != null

    fun fetchTimeSchedules() {
        launch {
            _timeSchedules.value = repository.getTimeSchedules()
        }
    }

    fun createSchedule(
        timeSchedule: TimeSchedule,
        time: Date = Date(),
        title: String = "Schedule"
    ) {
        _loadingCreateSchedule.value = loadingCreateSchedule.value + listOf(timeSchedule)
        launch {
            repository.getTimeScheduleById(timeSchedule.id)?.let {
                repository.createSchedule(it, time, title)
                _timeSchedules.value = repository.getTimeSchedules()
            }
            _loadingCreateSchedule.value =
                loadingCreateSchedule.value.filter { it.id != timeSchedule.id }
        }
    }
}