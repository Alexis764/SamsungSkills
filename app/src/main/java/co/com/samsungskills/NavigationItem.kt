package co.com.samsungskills

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.ui.graphics.vector.ImageVector
import co.com.samsungskills.navigation.Routes

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Sites : NavigationItem(
        route = Routes.SiteListing.route,
        title = "Sites",
        icon = Icons.Default.Place
    )

    data object Issues : NavigationItem(
        route = Routes.IssueListing.route,
        title = "Issues",
        icon = Icons.Default.ReportProblem
    )

    data object Staff : NavigationItem(
        route = Routes.StaffPage.route,
        title = "Staff",
        icon = Icons.Default.Person
    )
}