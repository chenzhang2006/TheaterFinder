package com.chenzhang.theaterfinder.playground

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.chenzhang.theaterfinder.ui.theme.TheaterFinderTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// https://developer.android.com/reference/kotlin/androidx/compose/foundation/interaction/InteractionSource
@Composable
fun Interaction2() {

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
        // This component is a compound component where part of it is clickable and part of it is
        // draggable. As a result we want to show indication for the _whole_ component, and not
        // just for clickable area. We set `null` indication here and provide an explicit
        // Modifier.indication instance later that will draw indication for the whole component.
        indication = null
    ) { /* update some business state here */ }

// SnapshotStateList we will use to track incoming Interactions in the order they are emitted
    val interactions = remember { mutableStateListOf<Interaction>() }

// Collect Interactions - if they are new, add them to `interactions`. If they represent stop /
// cancel events for existing Interactions, remove them from `interactions` so it will only
// contain currently active `interactions`.
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

// Display some text based on the most recent Interaction stored in `interactions`
    val text = when (interactions.lastOrNull()) {
        is DragInteraction.Start -> "Dragged"
        is PressInteraction.Press -> "Pressed"
        else -> "No state"
    }

    Column(
        Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Row(
            // Draw indication for the whole component, based on the Interactions dispatched by
            // our hoisted MutableInteractionSource
            Modifier.indication(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
        ) {
            Box(
                Modifier
                    .size(width = 240.dp, height = 80.dp)
                    .then(clickable)
                    .border(BorderStroke(3.dp, Color.Blue))
                    .padding(3.dp)
            ) {
                val pressed = interactions.any { it is PressInteraction.Press }
                Text(
                    text = if (pressed) "Pressed" else "Not pressed",
                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
            Box(
                Modifier
                    .size(width = 240.dp, height = 80.dp)
                    .then(draggable)
                    .border(BorderStroke(3.dp, Color.Red))
                    .padding(3.dp)
            ) {
                val dragged = interactions.any { it is DragInteraction.Start }
                Text(
                    text = if (dragged) "Dragged" else "Not dragged",
                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
        }
        Text(
            text = text,
            style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxSize().wrapContentSize()
        )
    }
}

@Preview
@Composable
fun PreviewInteraction2() {
    TheaterFinderTheme {
        Interaction2()
    }
}
