package co.com.samsungskills.feature.site_listing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPopup(siteReportName: String, siteListingViewModel: SiteListingViewModel) {
    val scope = rememberCoroutineScope()
    val reportPopupState = rememberModalBottomSheetState()
    val localDateTime = LocalDateTime.now()
    val localDateMillis = localDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

    val reportCategoryList = siteListingViewModel.reportCategoryList
    val reportDescription: String by siteListingViewModel.reportDescription.observeAsState("")
    val isError: Boolean by siteListingViewModel.isError.observeAsState(false)

    ModalBottomSheet(
        onDismissRequest = { siteListingViewModel.hideReportIssuePopup() },
        containerColor = Color.White,
        sheetState = reportPopupState,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            ReportPopupTopBar(
                onCancelButtonClick = {
                    scope.launch { reportPopupState.hide() }
                        .invokeOnCompletion {
                            if (!reportPopupState.isVisible) siteListingViewModel.hideReportIssuePopup()
                        }
                },
                onSaveButtonClick = {
                    siteListingViewModel.onSaveReport(
                        siteReportName,
                        localDateMillis,
                        reportCategoryList.first { it.isSelected },
                        reportDescription
                    )
                    scope.launch { reportPopupState.hide() }
                        .invokeOnCompletion {
                            if (!reportPopupState.isVisible) siteListingViewModel.hideReportIssuePopup()
                        }
                }
            )
            Divider(color = Color.Black, thickness = 2.dp)

            ReportInformation(
                localDateTime,
                siteReportName,
                reportCategoryList,
                reportDescription,
                siteListingViewModel
            )

            TextError(if (!isError) "Show Error Message (if any)" else "Please input all the fields")
        }
    }
}


@Composable
fun TextError(message: String) {
    Text(
        text = message,
        fontWeight = FontWeight.Bold,
        color = GrayButtonBackGround,
        fontSize = 16.sp,
        modifier = Modifier.padding(12.dp)
    )
}


@Composable
fun ReportInformation(
    localDateTime: LocalDateTime,
    siteReportName: String,
    reportCategoryList: List<ReportCategoryModel>,
    reportDescription: String,
    siteListingViewModel: SiteListingViewModel
) {
    val localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InformationTextRow("Site Name:", "[$siteReportName]")
            InformationTextRow("Datetime:", "[${localDateFormat.format(localDateTime)}]")
            InformationCategory(reportCategoryList) {
                siteListingViewModel.changeCategorySelection(it)
            }
            InformationDescription(reportDescription) {
                siteListingViewModel.onReportDescriptionChanged(it)
            }
        }
    }
}

@Composable
fun InformationDescription(reportDescription: String, onValueChange: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Description",
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(12.dp)
        ) {
            TextField(
                value = reportDescription,
                onValueChange = { onValueChange(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationCategory(
    reportCategoryList: List<ReportCategoryModel>,
    onCategoryClick: (ReportCategoryModel) -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Issue type",
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                reportCategoryList.forEachIndexed { index, reportCategoryModel ->
                    FilterChip(
                        selected = reportCategoryModel.isSelected,
                        onClick = { onCategoryClick(reportCategoryModel) },
                        label = {
                            Text(
                                text = reportCategoryModel.name,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedLabelColor = Color.White,
                            selectedContainerColor = GrayButtonBackGround,
                            containerColor = Color.Transparent,
                            labelColor = Color.LightGray
                        )
                    )

                    if (index != reportCategoryList.lastIndex) {
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(GrayButtonBackGround)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InformationTextRow(name: String, info: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = info,
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun ReportPopupTopBar(onCancelButtonClick: () -> Unit, onSaveButtonClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModalTopBarButton("cancel") { onCancelButtonClick() }

        Text(
            text = "Report Issue",
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        ModalTopBarButton("Save") { onSaveButtonClick() }
    }
}

@Composable
fun ModalTopBarButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GrayButtonBackGround)
    ) {
        Text(
            text = text,
            color = Color.White,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}