package com.go.project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticleBean
import com.go.mine.data.ArticlePageInfoBean
import com.go.common.data.ListUiState
import com.go.common.viewModel.BaseViewModel
import com.go.project.data.ProjectCategoryBean
import com.go.project.http.ProjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.collections.emptyList
import kotlin.collections.emptyMap
import kotlin.collections.orEmpty

class ProjectViewModel(private val repository: ProjectRepository): BaseViewModel() {

    private var pageInfoMaps: MutableMap<Int,ArticlePageInfoBean> = mutableMapOf()
    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _projectList: MutableState<Map<Int, List<ArticleBean>>> = mutableStateOf(emptyMap())
    val projectList: State<Map<Int,List<ArticleBean>>> = _projectList
    private fun isEmptyData(): Boolean = _projectList.value.isEmpty()

    private var _projectCategoryList = mutableStateOf<List<ProjectCategoryBean>>(emptyList())
    val projectCategoryList:State<List<ProjectCategoryBean>> = _projectCategoryList

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refresh(cid: Int?=null) {
        var localCID = cid?:-1
        LogUtils.d(TAG,"refresh start cid:$cid\t_listUiState:${_listUiState.value}\t_projectList.keys:${_projectList.value.keys}")
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmptyData())
        if(cid == null) {
            repository.getProjectTree().flatMapConcat {
                if(it.isSucceed()) {
                    _projectCategoryList.value = it.data.orEmpty()
                    localCID = _projectCategoryList.value.firstOrNull()?.id?:0
                    LogUtils.d(TAG,"refresh getProjectTree _projectCategoryList size:${_projectCategoryList.value.size}\t_cid:$localCID")
                    repository.getProjectList(cid = localCID)
                } else {
                    throw RuntimeException(it.errorMsg)
                }
            }
        } else {
            LogUtils.d(TAG,"getProjectList cid:$cid")
            repository.getProjectList(1, cid = cid)
        }
        .onEach {
            LogUtils.d(TAG,"refresh onEach:$it")
            if(it.isSucceed()) {
                _listUiState.value =ListUiState.RefreshSucceed(loadedCount = it.data?.datas.orEmpty().size)
                it.data?.let { data ->
                    pageInfoMaps[localCID] = data.copy(datas = emptyList())
                    _projectList.value += mapOf(localCID to data.datas)
                }
                LogUtils.d(TAG,"refresh onEach _projectList size:${_projectList.value.size}")
            } else {
                _listUiState.value =ListUiState.RefreshError(message = it.errorMsg, isEmptyData = isEmptyData())
            }
        }.onCompletion { cause ->
            LogUtils.d(TAG,"refresh onCompletion cid:$localCID\tit:$cause\tprojectList.size:${_projectList.value.keys.map {"$it"}}\tprojectCategoryList.size:${_projectCategoryList.value.size}")
        }.catch { cause ->
            LogUtils.d(TAG,"refresh catch:$cause")
            _listUiState.value =ListUiState.RefreshError(message = cause.message?:cause.toString(), isEmptyData = isEmptyData())
        }.launchIn(viewModelScope)
    }

    fun loadMore(cid:Int) {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.LoadingMore
        val curPage = pageInfoMaps[cid]?.curPage?:0
        repository.getProjectList(page=curPage+1, cid = cid)
            .onStart {
                LogUtils.d(TAG,"loadMore onStart cid:$cid")
            }
            .onEach {
                LogUtils.d(TAG,"loadMore onEach cid:$cid\tcurPage:$curPage\tit:$it")
                if(it.isSucceed()) {
                    it.data?.let { _data ->
                        LogUtils.d(TAG,"loadMore onEach cid:$cid\tcurPage:$curPage\tloadedCount:${_data.datas.size}\tit:$it}")
                        pageInfoMaps[cid] = _data.copy(datas = emptyList(), loadedCount = _data.datas.size)
                        val newList = (_projectList.value[cid]?:emptyList()) + _data.datas
                        _projectList.value += mapOf(cid to newList)
                    }
                } else {
                    throw RuntimeException(it.errorMsg)
                }
            }
            .onCompletion { cause ->
                _listUiState.value =
                    if(cause != null)
                        ListUiState.LoadMoreError(message = cause.message?:cause.toString())
                    else ListUiState.LoadMoreSucceed(pageInfoMaps[cid]?.loadedCount?:0)
                LogUtils.d(TAG,"loadMore onCompletion cid:$cid\tit:$cause" +
                        "\tprojectList.size:${_projectList.value.size}" +
                        "\tprojectCategoryList.size:${_projectCategoryList.value.size}\t_listUiState:$_listUiState")
            }
            .catch {
                LogUtils.d(TAG,"loadMore catch cid:$cid\tit:$it")
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "ProjectViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProjectViewModel(repository = ProjectRepository.Instance)
            }
        }
    }
}