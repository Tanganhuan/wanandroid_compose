package com.go.navigate.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.common.data.ListUiState
import com.go.common.viewModel.BaseViewModel
import com.go.navigate.http.CourseRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class CourseViewModel(private val repository: CourseRepository): BaseViewModel() {

    private var _courseListUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val courseListUiState: State<ListUiState> = _courseListUiState

    private var _courseList: MutableState<List<ArticleBean>> = mutableStateOf(emptyList())
    val courseList: State<List<ArticleBean>> = _courseList


    fun getCourseList() {
        if(_courseListUiState.value is ListUiState.Refreshing
            || _courseListUiState.value is ListUiState.LoadingMore) {
            return
        }
        repository.getCourseList()
            .onStart {
                LogUtils.d(TAG,"getCourseList onStart")
                _courseListUiState.value = ListUiState.Refreshing(isEmptyData = _courseList.value.isEmpty())
            }.onEach {
                LogUtils.d(TAG,"getCourseList onEach:$it")
                if(it.isSucceed()) {
                    _courseListUiState.value = ListUiState.LoadMoreSucceed(loadedCount = 0)
                    it.data?.let {
                        _courseList.value = it
                    }
                } else {
                    _courseListUiState.value = ListUiState.RefreshError(message = it.errorMsg, isEmptyData = _courseList.value.isEmpty())
                }
            }.onCompletion {
                LogUtils.d(TAG,"getCourseList onCompletion:$it")
            }.catch {
                LogUtils.d(TAG,"getCourseList catch:$it")
                _courseListUiState.value = ListUiState.RefreshError(message = it.message?:it.toString(), isEmptyData = _courseList.value.isEmpty())
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "CourseViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseViewModel(repository = CourseRepository.Instance)
            }
        }
    }
}