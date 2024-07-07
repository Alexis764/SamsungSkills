package co.com.samsungskills.feature.site_listing

import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.com.samsungskills.core.database.entities.SiteEntity
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MySiteListingScreen(
    location: Location?,
    navController: NavHostController = rememberNavController(),
    siteListingViewModel: SiteListingViewModel = hiltViewModel()
) {
    val isSitePopup: Boolean by siteListingViewModel.isSitePopup.observeAsState(false)
    val sitePopupEntity by siteListingViewModel.sitePopupEntity.observeAsState(SiteEntity())

    val isReportIssuePopup: Boolean by siteListingViewModel.isReportIssuePopup.observeAsState(false)
    val siteReportName: String by siteListingViewModel.siteReportName.observeAsState("")

    val isAddSitePopup: Boolean by siteListingViewModel.isAddSitePopup.observeAsState(false)


    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            SiteListingTopBar { siteListingViewModel.showAddSitePopup() }
            Divider(color = Color.Gray)

            SearchArea(siteListingViewModel)
            Divider(color = Color.Gray)
        }

        MySiteList(siteListingViewModel, location)
    }

    if (isSitePopup) SitePopup(sitePopupEntity, siteListingViewModel)
    if (isReportIssuePopup) ReportPopup(siteReportName, siteListingViewModel)
    if (isAddSitePopup) AddSitePopup(siteListingViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitePopup(sitePopupEntity: SiteEntity, siteListingViewModel: SiteListingViewModel) {
    val scope = rememberCoroutineScope()
    val sitePopupState = rememberModalBottomSheetState()

    val currentDateTime = LocalDateTime.now()
    val instant = currentDateTime.toInstant(ZoneOffset.UTC)
    val seconds = instant.epochSecond
    val nanos = instant.nano
    val timestamp = seconds + nanos / 1000000000.0

    ModalBottomSheet(
        onDismissRequest = { siteListingViewModel.hideSitePopup() },
        containerColor = Color.White,
        sheetState = sitePopupState,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 24.dp, start = 24.dp, top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = sitePopupEntity.name,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 14.sp,
                color = GrayButtonBackGround
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = {
                        siteListingViewModel.updateSite(sitePopupEntity, timestamp)
                        scope.launch { sitePopupState.hide() }
                            .invokeOnCompletion {
                                if (!sitePopupState.isVisible) {
                                    siteListingViewModel.hideSitePopup()
                                }
                            }
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Report\nOK",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = TextStyle(lineHeight = 1.5.em),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Button(
                    onClick = {
                        scope.launch { sitePopupState.hide() }
                            .invokeOnCompletion {
                                if (!sitePopupState.isVisible) {
                                    siteListingViewModel.hideSitePopup()
                                }
                            }
                        siteListingViewModel.showReportIssuePopup(sitePopupEntity.name)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Report\nIssue",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = TextStyle(lineHeight = 1.5.em),
                        letterSpacing = 2.sp
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch { sitePopupState.hide() }
                        .invokeOnCompletion {
                            if (!sitePopupState.isVisible) {
                                siteListingViewModel.hideSitePopup()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, GrayButtonBackGround),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Bold,
                    color = GrayButtonBackGround,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
fun MySiteList(siteListingViewModel: SiteListingViewModel, location: Location?) {
    val siteList = siteListingViewModel.siteList

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(siteList) { siteEntity ->
            SiteItem(
                siteEntity = siteEntity,
                location = location,
                onSiteClick = { siteEntityClicked, isChecked ->
                    if (!isChecked) siteListingViewModel.showSitePopup(siteEntityClicked)
                },
                onSiteNear = { siteListingViewModel.showSitePopup(it) }
            )
        }
    }
}

@Composable
fun SiteItem(
    siteEntity: SiteEntity,
    location: Location?,
    onSiteClick: (SiteEntity, Boolean) -> Unit,
    onSiteNear: (SiteEntity) -> Unit
) {
    var isChecked = false
    val currentDateTime = LocalDateTime.now()

    val seconds = siteEntity.lastCheckDate.toLong()
    val nanos = ((siteEntity.lastCheckDate - seconds) * 1000000000).toInt()
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    val lastCheckDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    if (currentDateTime.year == lastCheckDateTime.year && currentDateTime.month == lastCheckDateTime.month) {
        isChecked = currentDateTime.dayOfMonth == lastCheckDateTime.dayOfMonth
    }


    if (location != null) {
        val distance = haversineDistance(
            location.longitude,
            location.latitude,
            siteEntity.longitude,
            siteEntity.latitude
        )
        if (distance <= 100 && !isChecked) onSiteNear(siteEntity)
    }


    Card(
        border = BorderStroke(1.dp, GrayButtonBackGround),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.clickable { onSiteClick(siteEntity, isChecked) }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = siteEntity.name,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                )

                Text(
                    text = "Last check: ${dateTimeFormat.format(lastCheckDateTime)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            AnimatedVisibility(visible = isChecked) {
                Text(
                    text = "Checked",
                    color = GrayButtonBackGround,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .border(1.dp, GrayButtonBackGround)
                        .padding(8.dp)
                )
            }
        }

    }
}

fun haversineDistance(
    userLongitude: Double,
    userLatitude: Double,
    siteLongitude: Double,
    siteLatitude: Double
): Double {
    val earthRadius = 6371000
    val latDistance = Math.toRadians(siteLatitude - userLatitude)
    val lonDistance = Math.toRadians(siteLongitude - userLongitude)

    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(userLatitude)) * cos(Math.toRadians(siteLatitude)) *
            sin(lonDistance / 2) * sin(lonDistance / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}


@Composable
fun SearchArea(siteListingViewModel: SiteListingViewModel) {
    val searchTextValue: String by siteListingViewModel.searchTextValue.observeAsState("")

    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MySearchTextField(
            searchTextValue,
            Modifier.weight(1f)
        ) { siteListingViewModel.onSearchTextValueChange(it) }
        Spacer(modifier = Modifier.width(12.dp))

        AnimatedVisibility(visible = searchTextValue.trim().isNotBlank()) {
            Button(
                onClick = {
                    siteListingViewModel.filterList(searchTextValue)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GrayButtonBackGround)
            ) {
                Text(
                    text = "Search",
                    color = Color.White,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MySearchTextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        maxLines = 1,
        singleLine = true,
        modifier = modifier.height(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            focusedBorderColor = GrayButtonBackGround,
            cursorColor = GrayButtonBackGround
        ),
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 14.sp)
    )
}


@Composable
fun SiteListingTopBar(onAddButtonClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "Sites",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center),
            letterSpacing = 2.sp
        )

        Card(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(40.dp)
                .width(50.dp)
                .clickable {
                    onAddButtonClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = GrayButtonBackGround
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    }
}


//if (location != null) {
//    Text(
//        text = "Lat:${location.latitude} Lon:${location.longitude}",
//        color = Color.LightGray,
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center
//    )
//}