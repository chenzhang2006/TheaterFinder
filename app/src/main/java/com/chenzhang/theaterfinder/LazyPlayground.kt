package com.chenzhang.theaterfinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chenzhang.theaterfinder.ui.theme.TheaterFinderTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class) // AnimatedVisibility
@Composable
fun MessageList(messages: List<Message>) {
    Box {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(state = listState) {
            items(messages.count()) {
                Text(modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp), text = messages[it].msg)
            }
        }

        // Show the button if the first visible item is past
        // the first item. We use a remembered derived state to
        // minimize unnecessary compositions
        val showButton = remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(visible = showButton.value) {
            Button(onClick = { coroutineScope.launch {
                listState.scrollToItem(0)
            }}) {
                Text("To Top")
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .map { index -> index > 0 }
                .distinctUntilChanged()
                .filter { it == true }
                .collect {
                    // analytics events
                }
        }
    }
}

data class Message(val id: Int, val msg: String)

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
fun PreviewColumn() {
    val msgs = (1..100).map { Message(id = it, msg = "Message $it") }
    TheaterFinderTheme {
        MessageList(messages = msgs)
    }
}
