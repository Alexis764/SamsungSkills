package co.com.samsungskills.navigation

sealed class Routes(val route: String) {
    data object SiteListing: Routes("siteListing")
    data object IssueListing: Routes("issueListing")
    data object StaffPage: Routes("staffPage")
}