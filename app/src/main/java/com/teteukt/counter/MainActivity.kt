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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teteukt.counter.ui.theme.CounterWithAnimationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterWithAnimationTheme {
                Content()
            }
        }
    }
}

@Composable
fun Screen(firstColor: Color, secondColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        firstColor,
                        secondColor
                    ),
                    startY = 0F,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {

    }
}

@Composable
fun FirstScreen(): UIState {

    return UIState(
        Pair(
            colorResource(id = R.color.blue_light_1),
            colorResource(id = R.color.blue_light_2)
        ),
        (-300).dp,
        0F,
        colorResource(id = R.color.sun_color),
        1F,
        0F
    )
}

@Composable
fun SecondScreen(): UIState {
    return UIState(
        Pair(
            colorResource(id = R.color.orange_light_1),
            colorResource(id = R.color.orange_light_2)
        ),
        0.dp,
        90F,
        colorResource(id = R.color.sun_color),
        .5F,
        .7F
    )
}

@Composable
fun ThirdScreen(): UIState {
    return UIState(
        Pair(
            colorResource(id = R.color.dark_blue_light_1),
            colorResource(id = R.color.dark_blue_light_2)
        ),
        (-300).dp,
        180F,
        colorResource(id = R.color.moon_color),
        .1F,
        1F
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val states = listOf(FirstScreen(), SecondScreen(), ThirdScreen())
        val pagerState: PagerState = rememberPagerState { states.size }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val currentState: UIState = states[pagerState.currentPage]
            val firstColor by
            animateColorAsState(
                targetValue = currentState.background.first,
                animationSpec = tween(1000, easing = LinearEasing)
            )
            val secondColor by
            animateColorAsState(
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
                                firstColor,
                                secondColor
                            ),
                            startY = 0F,
                            endY = Float.POSITIVE_INFINITY
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

        HorizontalPager(state = pagerState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterForm(onClickLogin = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })
            }
        }
    }
}

data class LoginFormState(
    val email: TextFieldValue,
    val password: TextFieldValue,
    val passwordVisible: Boolean
)

@Composable
fun RegisterForm(onClickLogin: (() -> Unit)? = null) {
    var state by remember {
        mutableStateOf(LoginFormState(TextFieldValue(""), TextFieldValue(""), false))
    }

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
            )
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Cadastro", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.padding(0.dp, 12.dp))

            Text("Usuário")
            Spacer(modifier = Modifier.padding(0.dp, 4.dp))
            TextField(
                value = state.email,
                onValueChange = {
                    state = state.copy(email = it)
                },
                shape = RoundedCornerShape(100),
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(0.dp, 12.dp))

            Text("Senha")
            Spacer(modifier = Modifier.padding(0.dp, 4.dp))
            TextField(
                value = state.password,
                onValueChange = {
                    state = state.copy(password = it)
                },
                visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(100),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        state = state.copy(passwordVisible = state.passwordVisible.not())
                    }) {
                        val passwordVisibleIcon =
                            painterResource(id = R.drawable.ic_round_visibility_24)
                        val passwordInvisibleIcon =
                            painterResource(id = R.drawable.ic_round_visibility_off_24)
                        Icon(
                            if (state.passwordVisible) passwordVisibleIcon else passwordInvisibleIcon,
                            ""
                        )
                    }
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Button(modifier = Modifier,
                onClick = { onClickLogin?.invoke() }) {
                Text(text = "Login")
                Icon(Icons.Rounded.KeyboardArrowRight, "")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CounterWithAnimationTheme {
        RegisterForm()
    }
}