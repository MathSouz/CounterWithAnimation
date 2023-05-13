package com.teteukt.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teteukt.counter.domain.Schedule
import com.teteukt.counter.domain.TimeSchedule
import com.teteukt.counter.extensions.fromHourAndMinute
import com.teteukt.counter.extensions.toCalendar
import com.teteukt.counter.ui.theme.CounterWithAnimationTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = getViewModel()
        setContent {
            CounterWithAnimationTheme {
                Content(
                    viewModel.timeSchedules.value,
                    onAddDay = {
                        viewModel.createSchedule(it)
                    },
                    onAddDawn = {
                        viewModel.createSchedule(it)
                    },
                    onAddNight = {
                        viewModel.createSchedule(it)
                    },
                    dayCreateScheduleLoading = viewModel.isTimeScheduleLoading(0),
                    dawnCreateScheduleLoading = viewModel.isTimeScheduleLoading(1),
                    nightCreateScheduleLoading = viewModel.isTimeScheduleLoading(2),
                )
            }
        }
        viewModel.fetchTimeSchedules()
    }
}

@Composable
fun FirstScreen(): UIState {

    return UIState(
        Pair(
            colorResource(id = R.color.blue_light_1), colorResource(id = R.color.blue_light_2)
        ), (-300).dp, 0F, colorResource(id = R.color.sun_color), 1F, 0F
    )
}

@Composable
fun SecondScreen(): UIState {
    return UIState(
        Pair(
            colorResource(id = R.color.orange_light_1), colorResource(id = R.color.orange_light_2)
        ), 0.dp, 90F, colorResource(id = R.color.sun_color), .5F, .7F
    )
}

@Composable
fun ThirdScreen(): UIState {
    return UIState(
        Pair(
            colorResource(id = R.color.dark_blue_light_1),
            colorResource(id = R.color.dark_blue_light_2)
        ), (-300).dp, 180F, colorResource(id = R.color.moon_color), .1F, 1F
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(
    timeSchedule: List<TimeSchedule>,
    onAddDay: ((TimeSchedule) -> Unit)? = null,
    onAddDawn: ((TimeSchedule) -> Unit)? = null,
    onAddNight: ((TimeSchedule) -> Unit)? = null,
    dayCreateScheduleLoading: Boolean = false,
    dawnCreateScheduleLoading: Boolean = false,
    nightCreateScheduleLoading: Boolean = false,
) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val states = listOf(FirstScreen(), SecondScreen(), ThirdScreen())
        val pagerState: PagerState = rememberPagerState { states.size }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val currentState: UIState = states[pagerState.currentPage]
            val firstColor by animateColorAsState(
                targetValue = currentState.background.first,
                animationSpec = tween(1000, easing = LinearEasing)
            )
            val secondColor by animateColorAsState(
                targetValue = currentState.background.second,
                animationSpec = tween(1000, easing = LinearEasing)
            )
            val position by animateDpAsState(
                targetValue = currentState.sunPosition,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )

            val rotation by animateFloatAsState(targetValue = currentState.sunRotation)

            val skyBoxColor by animateColorAsState(targetValue = currentState.sunColor)

            val cloudOpacity by animateFloatAsState(targetValue = currentState.cloudAlpha)

            val startsOpacity by animateFloatAsState(targetValue = currentState.starsAlpha)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                firstColor, secondColor
                            ), startY = 0F, endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.sun),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = position)
                    .rotate(rotation)
                    .scale(2F),
                colorFilter = ColorFilter.tint(color = skyBoxColor)
            )

            Image(
                painter = painterResource(id = R.drawable.stars),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .alpha(startsOpacity)
                    .align(Alignment.TopStart)
                    .offset(80.dp, 120.dp)
                    .scale(3F)
            )

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cloudOpacity),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.cloud),
                contentDescription = ""
            )

            Image(
                painter = painterResource(id = R.drawable.mountains),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }

        if (timeSchedule.isNotEmpty()) {
            HorizontalPager(state = pagerState) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (it) {
                        0 -> FirstScreenContent(timeSchedule[0], dayCreateScheduleLoading) {
                            onAddDay?.invoke(it)
                        }

                        1 -> SecondScreenContent(timeSchedule[1], dawnCreateScheduleLoading) {
                            onAddDawn?.invoke(it)
                        }

                        2 -> ThirdScreenContent(timeSchedule[2], nightCreateScheduleLoading) {
                            onAddNight?.invoke(it)
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage - 1)
                        }
                    }, enabled = pagerState.currentPage > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.KeyboardArrowLeft, "")
                            Text(text = "Previous")
                        }
                    }

                    Button(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                        }
                    }, enabled = pagerState.currentPage < pagerState.pageCount - 1) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Next")
                            Icon(Icons.Rounded.KeyboardArrowRight, "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Screen(
    timeSchedule: TimeSchedule,
    onSaveSchedule: ((Triple<Schedule, Date, String>) -> Unit)? = null,
    onClickAdd: (() -> Unit)? = null,
    onSaveTitle: ((String) -> Unit)? = null,
    loadingCreateSchedule: Boolean = false
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TitleInput(timeSchedule.title, onChangeValue = onSaveTitle)

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(timeSchedule.schedules, key = {
                it.id
            }) {
                ScheduleInput(schedule = it, onConfirm = onSaveSchedule)
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Row {
                    IconButton(
                        onClick = {
                            onClickAdd?.invoke()
                        },
                        enabled = loadingCreateSchedule.not(),
                        colors = IconButtonDefaults.filledIconButtonColors()
                    ) {
                        if (loadingCreateSchedule) {
                            CircularProgressIndicator()
                        } else {
                            Icon(Icons.Rounded.Add, "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleInput(initialTitle: String, onChangeValue: ((String) -> Unit)? = null) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(initialTitle))
    }

    var hasChanges by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .background(color = Color.White, shape = CircleShape)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            BasicTextField(
                value = textFieldValue, onValueChange = {
                    hasChanges = true
                    textFieldValue = it
                }, modifier = Modifier.weight(1F), textStyle = TextStyle(
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 24.sp
                )
            )
            IconButton(onClick = {
                focusManager.clearFocus()
                hasChanges = false
                onChangeValue?.invoke(textFieldValue.text)
            }, enabled = hasChanges, colors = IconButtonDefaults.filledIconButtonColors()) {
                Icon(Icons.Rounded.Done, "")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInput(
    schedule: Schedule,
    onConfirm: ((Triple<Schedule, Date, String>) -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    var hasChanges by remember {
        mutableStateOf(false)
    }
    var selectedTime by remember {
        mutableStateOf(schedule.time)
    }

    var titleTextInputValue by remember {
        mutableStateOf(TextFieldValue(schedule.title))
    }
    val calendar = selectedTime.toCalendar()
    val hours = calendar[Calendar.HOUR_OF_DAY]
    val minutes = calendar[Calendar.MINUTE]

    val timePickerState = rememberTimePickerState(initialMinute = minutes, initialHour = hours)

    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showTimePickerDialog) {
        AlertDialog(onDismissRequest = { showTimePickerDialog = false }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimePicker(state = timePickerState)
                Button(onClick = {
                    selectedTime = calendar.fromHourAndMinute(
                        timePickerState.hour,
                        timePickerState.minute
                    ).time
                    showTimePickerDialog = false
                    hasChanges = true
                }) {
                    Text(text = "Confirm")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(color = Color.White, shape = CircleShape)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { showTimePickerDialog = true }) {
                val zeroLeadingHours = hours.toString().padStart(2, '0')
                val zeroLeadingMinutes = minutes.toString().padStart(2, '0')
                Text(
                    text = "${zeroLeadingHours}:${zeroLeadingMinutes}"
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            BasicTextField(value = titleTextInputValue, onValueChange = {
                hasChanges = true
                titleTextInputValue = it
            }, modifier = Modifier.weight(1F), singleLine = true)
            IconButton(onClick = {
                focusManager.clearFocus()
                hasChanges = false
                onConfirm?.invoke(Triple(schedule, selectedTime, titleTextInputValue.text))
            }, enabled = hasChanges, colors = IconButtonDefaults.filledIconButtonColors()) {
                Icon(Icons.Rounded.Done, "")
            }
        }
    }
}

@Composable
fun FirstScreenContent(
    timeSchedule: TimeSchedule,
    loadingCreateSchedule: Boolean = false,
    onClickAdd: ((TimeSchedule) -> Unit)? = null,
) {
    Screen(timeSchedule, onClickAdd = {
        onClickAdd?.invoke(timeSchedule)
    }, loadingCreateSchedule = loadingCreateSchedule)
}

@Composable
fun SecondScreenContent(
    timeSchedule: TimeSchedule,
    loadingCreateSchedule: Boolean = false,
    onClickAdd: ((TimeSchedule) -> Unit)? = null,
) {
    Screen(timeSchedule, onClickAdd = {
        onClickAdd?.invoke(timeSchedule)
    }, loadingCreateSchedule = loadingCreateSchedule)
}

@Composable
fun ThirdScreenContent(
    timeSchedule: TimeSchedule,
    loadingCreateSchedule: Boolean = false,
    onClickAdd: ((TimeSchedule) -> Unit)? = null,
) {
    Screen(timeSchedule, onClickAdd = {
        onClickAdd?.invoke(timeSchedule)
    }, loadingCreateSchedule = loadingCreateSchedule)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CounterWithAnimationTheme {

    }
}