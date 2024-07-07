package co.com.samsungskills.feature.staff_page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.com.samsungskills.R
import co.com.samsungskills.core.database.entities.StaffEntity
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun MyStaffPageScreen(
    staffPageViewModel: StaffPageViewModel = hiltViewModel()
) {
    val isQrModalVisible: Boolean by staffPageViewModel.isQrModalVisible.observeAsState(false)

    val staffEntity by staffPageViewModel.staffEntity.observeAsState(
        StaffEntity(name = "", rank = "")
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        StaffPageTopBar()
        Divider(color = Color.Gray)

        StaffInformation(staffEntity) { staffPageViewModel.showQrModal() }
    }

    if (isQrModalVisible) MyQrModal(staffEntity, staffPageViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyQrModal(staffEntity: StaffEntity, staffPageViewModel: StaffPageViewModel) {
    val qrModalState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val localDateTime = LocalDateTime.now()
    val localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val localDateMillis = localDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

    ModalBottomSheet(
        onDismissRequest = { staffPageViewModel.hideQrModal() },
        containerColor = Color.White,
        sheetState = qrModalState,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 24.dp, start = 24.dp, top = 12.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Show the QR Code to the Scanner",
                color = GrayButtonBackGround,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Image(
                painter = painterResource(id = R.drawable.qrcode),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = localDateFormat.format(localDateTime),
                fontSize = 13.sp,
                color = Color.Gray
            )

            Button(
                onClick = {
                    staffPageViewModel.updateStaff(
                        staffEntity.copy(
                            checkInOutDate = localDateMillis,
                            isCheckIn = !staffEntity.isCheckIn
                        )
                    )

                    scope.launch { qrModalState.hide() }
                        .invokeOnCompletion {
                            if (!qrModalState.isVisible) {
                                staffPageViewModel.hideQrModal()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GrayButtonBackGround)
            ) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
    }
}


@Composable
fun StaffInformation(staffEntity: StaffEntity, onButtonCheckClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StaffImage()
        Spacer(modifier = Modifier.height(2.dp))

        StaffName(staffEntity.name)
        StaffRank(staffEntity.rank)
        StaffCheckInOut(staffEntity.checkInOutDate, staffEntity.isCheckIn)

        Spacer(modifier = Modifier.height(2.dp))
        CheckInOutButton(if (staffEntity.isCheckIn) "Check Out" else "Check In") { onButtonCheckClick() }
    }
}

@Composable
fun CheckInOutButton(text: String, onButtonCheckClick: () -> Unit) {
    Button(
        onClick = { onButtonCheckClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StaffCheckInOut(checkInOutDate: Long?, checkIn: Boolean) {
    val dateText = if (checkInOutDate != null) {
        val instant = Instant.ofEpochMilli(checkInOutDate)
        val localDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime()
        val localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        if (checkIn) {
            "On Duty ${localDateFormat.format(localDateTime)}"
        } else {
            "Off Duty ${localDateFormat.format(localDateTime)}"
        }

    } else {
        "No check"
    }

    Text(
        text = dateText,
        fontSize = 13.sp,
        color = Color.Gray
    )
}

@Composable
fun StaffRank(rank: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Rank",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = GrayButtonBackGround
        )
        Text(
            text = "[$rank]",
            fontSize = 16.sp,
            color = GrayButtonBackGround
        )
    }
}

@Composable
fun StaffName(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Staff Name",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = GrayButtonBackGround
        )
        Text(
            text = "[$name]",
            fontSize = 16.sp,
            color = GrayButtonBackGround
        )
    }
}

@Composable
fun StaffImage() {
    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = null,
        modifier = Modifier.size(150.dp)
    )
}


@Composable
fun StaffPageTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Staff",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center),
            letterSpacing = 2.sp
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun MyPreview() {
    MyStaffPageScreen()
}