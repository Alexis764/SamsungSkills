package co.com.samsungskills.feature.site_listing

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.samsungskills.core.database.entities.IssueEntity
import co.com.samsungskills.core.database.entities.SiteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SiteListingViewModel @Inject constructor(
    private val siteListingRepository: SiteListingRepository
) : ViewModel() {

    private val _searchTextValue = MutableLiveData<String>()
    val searchTextValue: LiveData<String> = _searchTextValue

    fun onSearchTextValueChange(value: String) {
        _searchTextValue.value = value
        if (value.trim().isBlank()) {
            _siteList.clear()
            _siteList.addAll(siteListAbsolute)
        }
    }


    private val siteListAbsolute = mutableStateListOf<SiteEntity>()
    private val _siteList = mutableStateListOf<SiteEntity>()
    val siteList: List<SiteEntity> = _siteList

    fun filterList(searchTextValue: String) {
        _siteList.clear()
        _siteList.addAll(siteListAbsolute.filter {
            it.name.lowercase().startsWith(searchTextValue.lowercase())
        })
    }


    init {
        viewModelScope.launch {
            siteListingRepository.getAllSite().collect {
                siteListAbsolute.clear()
                siteListAbsolute.addAll(it)
                siteListAbsolute.sortBy { siteEntity -> siteEntity.name }

                _siteList.clear()
                _siteList.addAll(siteListAbsolute)
            }
        }
    }


    private val _isSitePopup = MutableLiveData<Boolean>()
    val isSitePopup: LiveData<Boolean> = _isSitePopup

    private val _sitePopupEntity = MutableLiveData<SiteEntity>()
    val sitePopupEntity: LiveData<SiteEntity> = _sitePopupEntity

    fun showSitePopup(siteEntity: SiteEntity) {
        _isSitePopup.value = true
        _sitePopupEntity.value = siteEntity
    }

    fun hideSitePopup() {
        _isSitePopup.value = false
    }

    fun updateSite(siteEntity: SiteEntity, newLastCheckDate: Double) {
        viewModelScope.launch {
            siteListingRepository.updateSite(siteEntity.copy(lastCheckDate = newLastCheckDate))
        }
    }


    private val _isReportIssuePopup = MutableLiveData<Boolean>()
    val isReportIssuePopup: LiveData<Boolean> = _isReportIssuePopup

    private val _siteReportName = MutableLiveData<String>()
    val siteReportName: LiveData<String> = _siteReportName

    fun showReportIssuePopup(siteReportName: String) {
        _isReportIssuePopup.value = true
        _siteReportName.value = siteReportName
    }

    fun hideReportIssuePopup() {
        _isReportIssuePopup.value = false
    }


    private val _reportCategoryList = mutableStateListOf(
        ReportCategoryModel("Type A", true),
        ReportCategoryModel("Type B"),
        ReportCategoryModel("Other")
    )
    val reportCategoryList: List<ReportCategoryModel> = _reportCategoryList

    fun changeCategorySelection(reportCategoryModel: ReportCategoryModel) {
        val categoryIndex = _reportCategoryList.indexOf(reportCategoryModel)

        _reportCategoryList.forEachIndexed { index, _ ->
            _reportCategoryList[index] = _reportCategoryList[index].copy(isSelected = false)
        }

        _reportCategoryList[categoryIndex] =
            _reportCategoryList[categoryIndex].copy(isSelected = true)
    }


    private val _reportDescription = MutableLiveData<String>()
    val reportDescription: LiveData<String> = _reportDescription

    fun onReportDescriptionChanged(value: String) {
        _reportDescription.value = value
    }


    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun onSaveReport(
        siteReportName: String,
        localDateMillis: Long,
        first: ReportCategoryModel,
        reportDescription: String
    ) {
        if (reportDescription.trim().isNotBlank()) {
            viewModelScope.launch {
                siteListingRepository.insertIssue(
                    IssueEntity(
                        siteName = siteReportName,
                        dataTime = localDateMillis,
                        issueType = first.name,
                        description = reportDescription
                    )
                )
            }

        } else {
            _isError.value = true
        }
    }


    private val _isAddSitePopup = MutableLiveData<Boolean>()
    val isAddSitePopup: LiveData<Boolean> = _isAddSitePopup

    fun showAddSitePopup() {
        _isAddSitePopup.value = true
    }

    fun hideAddSitePopup() {
        _isAddSitePopup.value = false
    }


    private val _siteNameValue = MutableLiveData<String>()
    val siteNameValue: LiveData<String> = _siteNameValue

    private val _siteLatitude = MutableLiveData<String>()
    val siteLatitude: LiveData<String> = _siteLatitude

    private val _siteLongitude = MutableLiveData<String>()
    val siteLongitude: LiveData<String> = _siteLongitude

    private val _addErrorMessage = MutableLiveData<String>()
    val addErrorMessage: LiveData<String> = _addErrorMessage

    fun onSiteNameValueChanged(value: String) {
        if (value.length <= 20) _siteNameValue.value = value
    }

    fun onSiteLatitudeValueChanged(value: String) {
        if (value.length <= 20) _siteLatitude.value = value
    }

    fun onSiteLongitudeValueChanged(value: String) {
        if (value.length <= 20) _siteLongitude.value = value
    }

    fun onSaveSite(
        siteNameValue: String,
        siteLatitude: String,
        siteLongitude: String,
        selectedDateMillis: Long
    ): Boolean {
        val latitude: Double
        val longitude: Double

        if (siteNameValue.trim().isBlank() ||
            siteLatitude.trim().isBlank() ||
            siteLongitude.trim().isBlank()
        ) {
            _addErrorMessage.value = "Error message: \"missing field\""
            return false

        } else {
            try {
                latitude = siteLatitude.toDouble()
                longitude = siteLongitude.toDouble()

            } catch (_: Exception) {
                _addErrorMessage.value = "Error message: \"invalid input\""
                return false
            }
        }

        val instant = Instant.ofEpochMilli(selectedDateMillis)
        val seconds = instant.epochSecond
        val nanos = instant.nano
        val timestamp = seconds + nanos / 1000000000.0

        viewModelScope.launch {
            siteListingRepository.insertSite(
                SiteEntity(
                    id = System.currentTimeMillis().hashCode().toString(),
                    name = siteNameValue,
                    latitude = latitude,
                    longitude = longitude,
                    lastCheckDate = timestamp
                )
            )
        }

        return true
    }


}