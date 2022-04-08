package com.chenzhang.theaterfinder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chenzhang.theaterfinder.ui.theme.TheaterFinderTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TheaterFinderScreen() {
    rememberSystemUiController().setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = MaterialTheme.colors.isLight
    )

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
        headerHeight = halfHeightDp.dp,
        backLayerContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(offset / halfHeightPx)
            ) {
                initMap()
            }
        },
        frontLayerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                val columnAlpha = ((halfHeightPx - offset) / halfHeightPx).coerceIn(0f..1f)
                val rowAlpha = (offset / halfHeightPx).coerceIn(0f..1f)
                val columnState = rememberLazyListState()
                val rowState = rememberLazyListState()

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp),
                    text = "154 W 14th Street",
                    style = MaterialTheme.typography.h6,
                    color = contentColorFor(backgroundColor = MaterialTheme.colors.primary)
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    if (columnAlpha > 0) {
                        if (columnAlpha == 1f) {
                            LaunchedEffect(columnState) {
                                columnState.animateScrollToItem(rowState.firstVisibleItemIndex)
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.alpha(columnAlpha),
                            state = columnState
                        ) {
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
                    }
                    if (rowAlpha > 0) {
                        if (rowAlpha == 1f) {
                            LaunchedEffect(rowState) {
                                rowState.animateScrollToItem(columnState.firstVisibleItemIndex)
                            }
                        }
                        LazyRow(
                            modifier = Modifier.alpha(rowAlpha),
                            state = rowState
                        ) {
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
        }
    )
}

@Composable
private fun initMap() {
    val context = LocalContext.current
    val newYork = LatLng(40.73, -73.9712)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(newYork, 12f)
    }
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoomPreference = 16f,
                minZoomPreference = 5f,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.google_style)
            )
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings
    ) {
        Marker(position = LatLng(40.73, -73.9912))
        Marker(position = LatLng(40.745, -73.9812))
        Marker(position = LatLng(40.755, -73.9942))
    }

//    LaunchedEffect(cameraPositionState) {
//        cameraPositionState.animate(
//            CameraUpdateFactory.newCameraPosition(
//                CameraPosition.fromLatLngZoom(
//                    LatLng(
//                        41.666,
//                        -74.333
//                    ), 10.0f
//                )
//            )
//        )
//    }
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
