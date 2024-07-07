package co.com.samsungskills

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.samsungskills.core.local.ReadSitesDocument
import co.com.samsungskills.core.local.ReadStaffDocument
import co.com.samsungskills.core.preferences.FirstTimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readStaffDocument: ReadStaffDocument,
    private val readSitesDocument: ReadSitesDocument,
    private val firstTimeManager: FirstTimeManager,
    private val mainRepository: MainRepository,
    private val locationService: LocationService
) : ViewModel() {

    init {
        viewModelScope.launch {
            firstTimeManager.getFirstTime().collect { firsTime ->
                if (firsTime) {
                    mainRepository.insertSiteList(readSitesDocument())
                    mainRepository.insertStaff(readStaffDocument())
                    firstTimeManager.saveFirstTime(false)
                }
            }
        }
    }


    suspend fun getUserLocation(): Location? {
        return locationService.getUserLocation()
    }

}