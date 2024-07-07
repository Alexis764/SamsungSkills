package co.com.samsungskills.navigation

import android.location.Location
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.com.samsungskills.feature.issue_listing.MyIssueListingScreen
import co.com.samsungskills.feature.site_listing.MySiteListingScreen
import co.com.samsungskills.feature.staff_page.MyStaffPageScreen

@Composable
fun MyNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    location: Location?
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SiteListing.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Routes.SiteListing.route) {
            MySiteListingScreen(location, navController)
        }

        composable(Routes.IssueListing.route) {
            MyIssueListingScreen(navController)
        }

        composable(Routes.StaffPage.route) {
            MyStaffPageScreen()
        }
    }
}