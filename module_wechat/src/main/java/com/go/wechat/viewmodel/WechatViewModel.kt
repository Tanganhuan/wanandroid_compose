package com.go.wechat.viewmodel

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
import com.go.wechat.data.WechatAuthorBean
import com.go.wechat.http.WechatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.toString

class WechatViewModel(val wechatRepository: WechatRepository): BaseViewModel() {

    private var pageInfoMaps: MutableMap<Int,ArticlePageInfoBean> = mutableMapOf()
    private var _listUiState: MutableState<ListUiState> = mutableStateOf(ListUiState.Idel)
    val listUiState: State<ListUiState> = _listUiState

    private var _articleList: MutableState<Map<Int, List<ArticleBean>>> = mutableStateOf(emptyMap())
    val articleList: State<Map<Int,List<ArticleBean>>> = _articleList
    private fun isEmptyData(): Boolean = _articleList.value.isEmpty()

    private var _wechatAuthorList = mutableStateOf<List<WechatAuthorBean>>(emptyList())
    val wechatAuthorList:State<List<WechatAuthorBean>> = _wechatAuthorList

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refresh(id: Int?=null) {
        var localID = id?:-1
        LogUtils.d(TAG,"refresh start id:$id\t_listUiState:${_listUiState.value}\t_articleList.keys:${_articleList.value.keys}")
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.Refreshing(isEmptyData = isEmptyData())
        if(id == null || id<=-1) {
            LogUtils.d(TAG,"refresh id == null")
            wechatRepository.getWxAuthorList().flatMapConcat {
                if(it.isSucceed()) {
                    _wechatAuthorList.value = it.data.orEmpty()
                    localID = _wechatAuthorList.value.firstOrNull()?.id?:0
                    LogUtils.d(TAG,"refresh getWxAuthorList getWxAuthorList size:${_wechatAuthorList.value.size}\tid:$localID")
                    wechatRepository.getWxArticleList(id = localID,page=0)
                } else {
                    LogUtils.d(TAG,"refresh errorMsg:${it.errorMsg}")
                    throw RuntimeException(it.errorMsg)
                }
            }
        } else {
            LogUtils.d(TAG,"getWxArticleList id:$id")
            wechatRepository.getWxArticleList(id =localID)
        }.onEach {
                LogUtils.d(TAG,"refresh onEach:$it")
                if(it.isSucceed()) {
                    _listUiState.value = ListUiState.RefreshSucceed(loadedCount = it.data?.datas.orEmpty().size)
                    it.data?.let { data ->
                        pageInfoMaps[localID] = data.copy(datas = emptyList())
                        _articleList.value += mapOf(localID to data.datas)
                    }
                    LogUtils.d(TAG,"refresh onEach _articleList size:${_articleList.value.size}")
                } else {
                    _listUiState.value = ListUiState.RefreshError(message = it.errorMsg, isEmptyData = isEmptyData())
                }
            }.onCompletion { cause ->

                LogUtils.d(TAG,"refresh onCompletion id:$localID\tit:$cause\tarticleList.size:${_articleList.value.keys.map {"$it"}}\t_wechatAuthorList.size:${_wechatAuthorList.value.size}")
            }.catch { cause ->
                LogUtils.d(TAG,"refresh catch:$cause")
                _listUiState.value = ListUiState.RefreshError(message = cause.message?:cause.toString(), isEmptyData = isEmptyData())
            }.launchIn(viewModelScope)
    }

    fun loadMore(id:Int) {
        if(_listUiState.value is ListUiState.Refreshing
            || _listUiState.value is ListUiState.LoadingMore) {
            return
        }
        _listUiState.value = ListUiState.LoadingMore
        val curPage = pageInfoMaps[id]?.curPage?:0
        wechatRepository.getWxArticleList(page=curPage+1, id = id)
            .onStart {
                LogUtils.d(TAG,"loadMore onStart id:$id")
            }
            .onEach {
                LogUtils.d(TAG,"loadMore onEach id:$id\tcurPage:$curPage\tit:$it")
                if(it.isSucceed()) {
                    it.data?.let { _data ->
                        LogUtils.d(TAG,"loadMore onEach id:$id\tcurPage:$curPage\tloadedCount:${_data.datas.size}\tit:$it}")
                        pageInfoMaps[id] = _data.copy(datas = emptyList(), loadedCount = _data.datas.size)
                        val newList = (_articleList.value[id]?:emptyList()) + _data.datas
                        _articleList.value += mapOf(id to newList)
                    }
                } else {
                    throw RuntimeException(it.errorMsg)
                }
            }
            .onCompletion { cause ->
                _listUiState.value =
                    if(cause != null)
                        ListUiState.LoadMoreError(message = cause.message?:cause.toString())
                    else ListUiState.LoadMoreSucceed(pageInfoMaps[id]?.loadedCount?:0)
                LogUtils.d(TAG,"loadMore onCompletion id:$id\tit:$cause" +
                        "\tarticleList.size:${_articleList.value.size}" +
                        "\t_wechatAuthorList.size:${_wechatAuthorList.value.size}\t_listUiState:$_listUiState")
            }
            .catch {
                LogUtils.d(TAG,"loadMore catch id:$id\tit:$it")
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "WechatViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WechatViewModel(wechatRepository = WechatRepository.Instance)
            }
        }
    }
}