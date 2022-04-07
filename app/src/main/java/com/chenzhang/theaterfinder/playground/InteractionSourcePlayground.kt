package com.chenzhang.theaterfinder.playground

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chenzhang.theaterfinder.ui.theme.TheaterFinderTheme

@Composable
fun InteractionSourceScreen() {
// Hoist the MutableInteractionSource that we will provide to interactions
    val interactionSource = remember { MutableInteractionSource() }

// Provide the MutableInteractionSource instances to the interactions we want to observe state
// changes for
    val draggable = Modifier.draggable(
        interactionSource = interactionSource,
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { /* update some business state here */ }
    )

    val clickable = Modifier.clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current
    ) { /* update some business state here */ }

// Observe changes to the binary state for these interactions
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

// Use the state to change our UI
    val (text, color) = when {
        isDragged && isPressed -> "Dragged and pressed" to Color.Red
        isDragged -> "Dragged" to Color.Green
        isPressed -> "Pressed" to Color.Blue
        // Default / baseline state
        else -> "Drag me horizontally, or press me!" to Color.Black
    }

    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize()
            .size(width = 240.dp, height = 80.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .then(clickable)
                .then(draggable)
                .border(BorderStroke(3.dp, color))
                .padding(3.dp)
        ) {
            Text(
                text, style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxSize().wrapContentSize()
            )
        }
    }
}

@Preview
@Composable
fun PreviewInteractionSource() {
    TheaterFinderTheme {
        InteractionSourceScreen()
    }
}
