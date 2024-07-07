package co.com.samsungskills.feature.issue_listing

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.com.samsungskills.core.database.entities.IssueEntity
import co.com.samsungskills.feature.staff_page.IssueFilterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class IssueListingViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {

    private val _issueCategoryList = mutableStateListOf(
        IssueFilterModel("All", true),
        IssueFilterModel("Active"),
        IssueFilterModel("Closed")
    )
    val issueCategoryList: List<IssueFilterModel> = _issueCategoryList

    fun changeIssueCategorySelected(issueFilterModel: IssueFilterModel) {
        val issueIndex = _issueCategoryList.indexOf(issueFilterModel)

        _issueCategoryList.forEachIndexed { index, _ ->
            _issueCategoryList[index] = _issueCategoryList[index].copy(isSelected = false)
        }

        _issueCategoryList[issueIndex] = _issueCategoryList[issueIndex].copy(isSelected = true)

        _issueList.clear()
        when (issueFilterModel.name) {
            "All" -> _issueList.addAll(_issueAbsoluteList)
            "Active" -> _issueList.addAll(_issueAbsoluteList.filter { it.isActive })
            "Closed" -> _issueList.addAll(_issueAbsoluteList.filter { !it.isActive })
        }
    }


    private val _issueAbsoluteList = mutableStateListOf<IssueEntity>()
    private val _issueList = mutableStateListOf<IssueEntity>()
    val issueList: List<IssueEntity> = _issueList

    init {
        viewModelScope.launch {
            issueRepository.getAllIssue().collect {
                _issueAbsoluteList.clear()
                _issueAbsoluteList.addAll(it)
                _issueAbsoluteList.sortByDescending { entity -> entity.dataTime }

                _issueList.clear()
                _issueList.addAll(_issueAbsoluteList)
            }
        }
    }


    private val _isConfirmDialogVisible = MutableLiveData<Boolean>()
    val isConfirmDialogVisible: LiveData<Boolean> = _isConfirmDialogVisible

    private val _updateIssueEntityInfo = MutableLiveData<IssueEntity>()
    val updateIssueEntityInfo: LiveData<IssueEntity> = _updateIssueEntityInfo

    fun onShowConfirmDialog(issueEntity: IssueEntity) {
        _updateIssueEntityInfo.value = issueEntity
        _isConfirmDialogVisible.value = true
    }

    fun onHideConfirmDialog() {
        _isConfirmDialogVisible.value = false
    }

    fun updateIssueEntity(issueEntity: IssueEntity) {
        viewModelScope.launch {
            val localDatetime = LocalDateTime.now()
            val instant = localDatetime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

            issueRepository.updateIssue(issueEntity.copy(isActive = false, dataTime = instant))
        }
    }

}