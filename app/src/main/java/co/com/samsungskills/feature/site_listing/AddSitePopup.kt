package co.com.samsungskills.feature.site_listing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSitePopup(siteListingViewModel: SiteListingViewModel) {
    val scope = rememberCoroutineScope()
    val addModalState = rememberModalBottomSheetState()
    val verticalScroll = rememberScrollState()

    val localDateTime = LocalDateTime.now()
    val localDateMillis = localDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = localDateMillis)

    val siteNameValue: String by siteListingViewModel.siteNameValue.observeAsState("")
    val siteLatitude: String by siteListingViewModel.siteLatitude.observeAsState("")
    val siteLongitude: String by siteListingViewModel.siteLongitude.observeAsState("")

    val addErrorMessage: String by siteListingViewModel.addErrorMessage.observeAsState("")

    ModalBottomSheet(
        onDismissRequest = { siteListingViewModel.hideAddSitePopup() },
        containerColor = Color.White,
        sheetState = addModalState,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .verticalScroll(verticalScroll)
        ) {
            AddPopupTopBar(
                onCancelButtonClick = {
                    scope.launch { addModalState.hide() }
                        .invokeOnCompletion {
                            if (!addModalState.isVisible) siteListingViewModel.hideAddSitePopup()
                        }
                },
                onSaveButtonClick = {
                    val isSave = siteListingViewModel.onSaveSite(
                        siteNameValue,
                        siteLatitude,
                        siteLongitude,
                        datePickerState.selectedDateMillis!!
                    )

                    if (isSave) {
                        scope.launch { addModalState.hide() }
                            .invokeOnCompletion {
                                if (!addModalState.isVisible) siteListingViewModel.hideAddSitePopup()
                            }
                    }
                }
            )
            Divider(color = Color.Black, thickness = 2.dp)

            AddSiteForm(
                siteNameValue,
                siteLatitude,
                siteLongitude,
                datePickerState,
                siteListingViewModel
            )

            AddTextError(addErrorMessage.ifBlank { "Show Error Message (if any)" })
        }
    }
}


@Composable
fun AddTextError(message: String) {
    Text(
        text = message,
        fontWeight = FontWeight.Bold,
        color = GrayButtonBackGround,
        fontSize = 16.sp,
        modifier = Modifier.padding(12.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSiteForm(
    siteNameValue: String,
    siteLatitude: String,
    siteLongitude: String,
    datePickerState: DatePickerState,
    siteListingViewModel: SiteListingViewModel
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AddInputArea("Site Name:") {
                SiteNameTextField(siteNameValue) {
                    siteListingViewModel.onSiteNameValueChanged(it)
                }
            }

            AddInputArea("Latitude:") {
                MyNumberTextField(siteLatitude, "Please enter the latitude") {
                    siteListingViewModel.onSiteLatitudeValueChanged(it)
                }
            }

            AddInputArea("Longitude:") {
                MyNumberTextField(siteLongitude, "Please enter the longitude") {
                    siteListingViewModel.onSiteLongitudeValueChanged(it)
                }
            }

            DatePickerArea(datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerArea(datePickerState: DatePickerState) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Last Check Date:",
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = DatePickerDefaults.colors(containerColor = Color.LightGray)
        )
    }
}

@Composable
fun MyNumberTextField(value: String, placeholder: String, onValueChanged: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        placeholder = {
            Text(text = placeholder)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        maxLines = 1,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color.LightGray,
            focusedPlaceholderColor = Color.LightGray,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            cursorColor = Color.Black,
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = GrayButtonBackGround
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun SiteNameTextField(siteNameValue: String, onSiteNameChanged: (String) -> Unit) {
    OutlinedTextField(
        value = siteNameValue,
        onValueChange = { onSiteNameChanged(it) },
        placeholder = {
            Text(text = "Please enter the site name")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        maxLines = 1,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color.LightGray,
            focusedPlaceholderColor = Color.LightGray,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            cursorColor = Color.Black,
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = GrayButtonBackGround
        )
    )
}

@Composable
fun AddInputArea(name: String, textField: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        textField()
    }
}


@Composable
fun AddPopupTopBar(onCancelButtonClick: () -> Unit, onSaveButtonClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModalTopBarButton("cancel") { onCancelButtonClick() }

        Text(
            text = "Add Site",
            fontWeight = FontWeight.Bold,
            color = GrayButtonBackGround,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        ModalTopBarButton("Save") { onSaveButtonClick() }
    }
}


