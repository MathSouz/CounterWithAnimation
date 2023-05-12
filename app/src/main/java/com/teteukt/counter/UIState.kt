package com.teteukt.counter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

class UIState(
    val background: Pair<Color, Color>,
    val sunPosition: Dp,
    val sunRotation: Float,
    val sunColor: Color,
    val cloudAlpha: Float,
    val starsAlpha: Float
)