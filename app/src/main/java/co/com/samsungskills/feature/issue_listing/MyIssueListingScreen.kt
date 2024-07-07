package co.com.samsungskills.feature.issue_listing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.com.samsungskills.core.database.entities.IssueEntity
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun MyIssueListingScreen(
    navController: NavHostController = rememberNavController(),
    issueListingViewModel: IssueListingViewModel = hiltViewModel()
) {
    val isConfirmDialogVisible by issueListingViewModel.isConfirmDialogVisible.observeAsState(false)
    val issueList = issueListingViewModel.issueList

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IssueListingTopBar()
        Divider(color = Color.Gray)

        IssueListingFilterChips(issueListingViewModel)

        if (issueList.isNotEmpty()) {
            IssueList(issueListingViewModel, issueList)

        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No issues",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.Center),
                    letterSpacing = 2.sp
                )
            }
        }
    }

    if (isConfirmDialogVisible) ConfirmDialog(issueListingViewModel)
}


@Composable
fun ConfirmDialog(issueListingViewModel: IssueListingViewModel) {
    val updateIssueEntityInfo: IssueEntity by issueListingViewModel.updateIssueEntityInfo.observeAsState(
        IssueEntity(
            siteName = "",
            dataTime = 0,
            issueType = "",
            description = ""
        )
    )

    Dialog(onDismissRequest = { }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, GrayButtonBackGround),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Close case ?",
                    fontWeight = FontWeight.Bold,
                    color = GrayButtonBackGround,
                    fontSize = 20.sp
                )

                Button(
                    onClick = {
                        issueListingViewModel.updateIssueEntity(updateIssueEntityInfo)
                        issueListingViewModel.onHideConfirmDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        issueListingViewModel.onHideConfirmDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, GrayButtonBackGround),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
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
}


@Composable
fun IssueList(issueListingViewModel: IssueListingViewModel, issueList: List<IssueEntity>) {
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(issueList) {
            IssueItem(it) { issueEntity ->
                issueListingViewModel.onShowConfirmDialog(issueEntity)
            }
        }
    }
}

@Composable
fun IssueItem(issueEntity: IssueEntity, onCloseCaseButtonClick: (IssueEntity) -> Unit) {
    val instant = Instant.ofEpochMilli(issueEntity.dataTime)
    val localDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime()
    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val lastCheck = if (issueEntity.isActive)
        "Report Datetime: ${dateTimeFormat.format(localDateTime)}"
    else
        "Resolved Datetime: ${dateTimeFormat.format(localDateTime)}"


    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text = issueEntity.siteName,
                    fontWeight = FontWeight.Bold,
                    color = GrayButtonBackGround,
                    fontSize = 16.sp
                )

                Text(
                    text = lastCheck,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = issueEntity.description,
                    fontWeight = FontWeight.Bold,
                    color = GrayButtonBackGround,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier.width(100.dp)
            ) {
                Text(
                    text = if (!issueEntity.isActive) "Resolved" else issueEntity.issueType,
                    color = GrayButtonBackGround,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GrayButtonBackGround)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = issueEntity.isActive) {
                    Button(
                        onClick = { onCloseCaseButtonClick(issueEntity) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Close\nCase",
                            color = Color.White,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueListingFilterChips(issueListingViewModel: IssueListingViewModel) {
    val issueCategoryList = issueListingViewModel.issueCategoryList

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
            issueCategoryList.forEachIndexed { index, issueFilterModel ->
                FilterChip(
                    selected = issueFilterModel.isSelected,
                    onClick = { issueListingViewModel.changeIssueCategorySelected(issueFilterModel) },
                    label = {
                        Text(
                            text = issueFilterModel.name,
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

                if (index != issueCategoryList.lastIndex) {
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


@Composable
fun IssueListingTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Issues",
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
    MyIssueListingScreen()
}