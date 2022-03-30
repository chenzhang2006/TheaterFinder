package com.chenzhang.theaterfinder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chenzhang.theaterfinder.ui.theme.TheaterFinderTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TheaterFinderScreen() {
    rememberSystemUiController().isStatusBarVisible = false

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoomPreference = 10f, minZoomPreference = 5f)
        )
    }
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }
    GoogleMap(properties = mapProperties, uiSettings = mapUiSettings)

    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    LaunchedEffect(backdropState) {
        backdropState.reveal()
    }
    val offset by backdropState.offset
    val value = backdropState.currentValue
    val halfHeightDp = LocalConfiguration.current.screenHeightDp / 2
    val halfHeightPx = with(LocalDensity.current) { halfHeightDp.dp.toPx() }
    BackdropScaffold(
        scaffoldState = backdropState,
        frontLayerScrimColor = Color.Unspecified,
        appBar = {},
        peekHeight = 0.dp,
        backLayerBackgroundColor = Color.White,
        headerHeight = halfHeightDp.dp,
        backLayerContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .alpha(offset / halfHeightPx)
            ) {
                val newYork = LatLng(40.73, -73.9712)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(newYork, 12f)
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(position = LatLng(40.73, -73.9912))
                    Marker(position = LatLng(40.745, -73.9812))
                    Marker(position = LatLng(40.755, -73.9942))
                }
            }
        },
        frontLayerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                val verticalListAlpha = ((halfHeightPx - offset) / halfHeightPx).coerceIn(0f..1f)
                val horizontalListAlpha = (offset / halfHeightPx).coerceIn(0f..1f)

                TimeTabs()
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.alpha(verticalListAlpha)) {
                        itemsIndexed(List(30) { "Movie $it" }) { index, item ->
                            Column {
                                Card(
                                    elevation = 4.dp,
                                    modifier = Modifier
                                        .size(width = 360.dp, height = 200.dp)
                                        .padding(8.dp)
                                        .clickable { }
                                ) {
                                    Image(
                                        painter = painterResource(id = getImageResourceId(index)),
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize(),
                                        alignment = Alignment.Center,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Movie $index",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.subtitle2
                                )
                            }
                            Divider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
                        }
                    }

                    LazyRow(modifier = Modifier.alpha(horizontalListAlpha)) {
                        itemsIndexed(List(30) { "Movie $it" }) { index, item ->
                            Column {
                                Card(
                                    elevation = 4.dp,
                                    modifier = Modifier
                                        .size(width = 280.dp, height = 200.dp)
                                        .padding(8.dp)
                                        .clickable { }
                                ) {
                                    Image(
                                        painter = painterResource(id = getImageResourceId(index)),
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize(),
                                        alignment = Alignment.Center,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = item,
                                    modifier = Modifier.padding(start = 12.dp),
                                    style = MaterialTheme.typography.subtitle2
                                )
                                Text(
                                    text = "Released on [date]",
                                    modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

private fun getImageResourceId(index: Int) = when (index % 3) {
    0 -> R.drawable.movie1
    1 -> R.drawable.movie2
    2 -> R.drawable.movie3
    else -> R.drawable.movie1
}

@Composable
private fun TimeTabs() {
    var tabIndex by remember { mutableStateOf(0) }
    TabRow(selectedTabIndex = tabIndex, backgroundColor = Color.Transparent) {
        listOf("Morning", "Afternoon", "Night").forEachIndexed { index, text ->
            Tab(
                selected = tabIndex == index,
                onClick = {
                    tabIndex = index
                },
                text = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.button
                    )
                },
                modifier = Modifier.background(Color.White)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBackdrop() {
    TheaterFinderTheme {
        TheaterFinderScreen()
    }
}
