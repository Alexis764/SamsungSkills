package co.com.samsungskills.feature.staff_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.samsungskills.core.database.entities.StaffEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffPageViewModel @Inject constructor(
    private val staffPageRepository: StaffPageRepository
) : ViewModel() {

    private val _isQrModalVisible = MutableLiveData<Boolean>()
    val isQrModalVisible: LiveData<Boolean> = _isQrModalVisible

    fun showQrModal() {
        _isQrModalVisible.value = true
    }

    fun hideQrModal() {
        _isQrModalVisible.value = false
    }


    private val _staffEntity = MutableLiveData<StaffEntity>()
    val staffEntity: LiveData<StaffEntity> = _staffEntity

    fun updateStaff(staffEntity: StaffEntity) {
        viewModelScope.launch {
            staffPageRepository.updateStaff(staffEntity)
        }
    }

    init {
        viewModelScope.launch {
            staffPageRepository.getStaff().collect {
                _staffEntity.value = it
            }
        }
    }

}