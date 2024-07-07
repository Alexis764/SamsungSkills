package co.com.samsungskills

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.com.samsungskills.navigation.MyNavigation
import co.com.samsungskills.ui.theme.GrayButtonBackGround
import co.com.samsungskills.ui.theme.SamsungSkillsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SamsungSkillsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Navigation Configuration
                    val navigationItemList = listOf(
                        NavigationItem.Sites,
                        NavigationItem.Issues,
                        NavigationItem.Staff
                    )
                    val navController = rememberNavController()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination


                    //Location permission
                    val locationPermission = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        )
                    )

                    LaunchedEffect(key1 = Unit) {
                        locationPermission.launchMultiplePermissionRequest()
                    }


                    //User location
                    val scope = rememberCoroutineScope()
                    var location by remember { mutableStateOf<Location?>(null) }
                    if (locationPermission.allPermissionsGranted) {
                        LaunchedEffect(key1 = Unit) {
                            scope.launch {
                                val result = mainViewModel.getUserLocation()

                                if (result != null) {
                                    location = result
                                }
                            }
                        }
                    }


                    //View
                    Scaffold(
                        bottomBar = {
                            MyBottomNavigationBar(
                                navigationItemList,
                                currentDestination,
                                navController
                            )
                        }
                    ) { paddingValues ->
                        MyNavigation(navController, paddingValues, location)
                    }
                }
            }
        }
    }
}


@Composable
fun MyBottomNavigationBar(
    navigationItemList: List<NavigationItem>,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBar(
        Modifier
            .fillMaxWidth()
            .height(70.dp),
        containerColor = Color.LightGray
    ) {
        navigationItemList.forEach { navItem ->
            val selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true

            NavigationBarItem(
                selected = selected,
                alwaysShowLabel = true,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = null)
                },
                label = {
                    Text(text = navItem.title, fontSize = 10.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.LightGray,
                    selectedIconColor = GrayButtonBackGround,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = GrayButtonBackGround,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}