package com.go.mine.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.blankj.utilcode.util.LogUtils
import com.go.common.BaseApplication
import com.go.common.data.RequestState
import com.go.common.viewModel.BaseListViewModel
import com.go.mine.R
import com.go.mine.data.ArticleBean
import com.go.mine.data.LoginUiState
import com.go.mine.data.UserBean
import com.go.mine.data.emptyUserBean
import com.go.mine.http.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class UserViewModel(val repository: UserRepository) : BaseListViewModel<ArticleBean>() {

    init {
        LogUtils.d(TAG, "init UserViewModel:${this.hashCode()}")
        repository.localUser()
            .onEach {
                _userBean.value = it
                LogUtils.d(TAG, "localUser:$it")
                _loginUiState.value = if (it == emptyUserBean) {
                    LoginUiState.Idle
                } else {
                    LoginUiState.LoggedIn
                }
            }.catch {
                LogUtils.d(TAG, "init UserViewModel catch:$it")
            }
            .launchIn(viewModelScope)
    }

    private var _userBean = mutableStateOf(UserBean())
    val userBean by _userBean

    private var lastGetUserCoinInfo:Long = 0

    private var _loginUiState = mutableStateOf<LoginUiState>(LoginUiState.Idle)
    val loginUiState: LoginUiState by _loginUiState

    fun isLogined() = _userBean.value.id > 0

    private val mutableArticleCollectState = mutableStateOf<Map<Int,Boolean>>(mapOf())
    val articleCollectState by mutableArticleCollectState
    fun updateArticleCollectState(id:Int,isCollect:Boolean) {
        mutableArticleCollectState.value += mapOf(id to isCollect)
    }

    fun logout() {
        _loginUiState.value = LoginUiState.Logouting
        _userBean.value = emptyUserBean
        updateDataList(emptyList())
        viewModelScope.launch {
            repository.logout().onCompletion {
                LogUtils.d(TAG, "logout onCompletion")
            }.collect {
                _loginUiState.value = LoginUiState.LogoutSucceed
                delay(200.milliseconds)
                _loginUiState.value = LoginUiState.Idle
            }
        }
    }

    fun getUserCoinInfo() {
        if(!isLogined() || System.currentTimeMillis()-lastGetUserCoinInfo < 24*60*60*1000L) {
            LogUtils.d(TAG,"getUserCoinInfo start limit")
            return
        }
        repository.getUserinfo()
            .onStart {
                LogUtils.d(TAG,"getUserCoinInfo start")
            }
            .onEach {
                LogUtils.d(TAG,"getUserCoinInfo onEach it:$it")
                if(it.isSucceed() && (it.data?.coinCount?:0) > 0) {
                    it.data?.coinCount?.let { coinCount ->
                        _userBean.value = _userBean.value.copy(coinCount = coinCount)
                        repository.updateUserInfo(_userBean.value)
                    }
                }
            }.catch {
                LogUtils.d(TAG,"getUserCoinInfo catch it:$it")
            }.onCompletion {
                lastGetUserCoinInfo = System.currentTimeMillis()
                LogUtils.d(TAG,"getUserCoinInfo onCompletion it:$it")
            }
            .launchIn(viewModelScope)
    }

    fun login(userName: String, password: String) {
        if (_loginUiState.value == LoginUiState.Logging) {
            return
        }

        LogUtils.d(TAG, "login userName:$userName\tpassword:$password")
        _loginUiState.value = LoginUiState.Logging

        repository.login(username = userName, password = password)
            .onEach {
                if (it.isSucceed()) {
                    it.data?.let { user ->
                        _userBean.value = user
                    }
                    _loginUiState.value = LoginUiState.LoggedIn
                } else {
                    _loginUiState.value = LoginUiState.Error(it.errorMsg)
                }
                LogUtils.d(TAG, "login:$it")
            }
            .catch {
                _loginUiState.value = LoginUiState.Error(it.toString())
            }.launchIn(viewModelScope)
    }

    fun addCollectArticle(id:Int,scope: CoroutineScope,onEvent:(RequestState)->Unit) {
        if (!isLogined()) {
            onEvent(RequestState.Error(msg = BaseApplication.Instance.getString(com.go.common.R.string.please_login_first)))
            scope.launch {
                delay(1000.milliseconds)
                onEvent(RequestState.Dismiss)
            }
            return
        }
        repository.addCollectArticle(id = id)
            .onStart {
                onEvent(RequestState.Loading(msg = BaseApplication.Instance.getString(R.string.collecting_please_wait)))
                LogUtils.d(TAG, "addCollectArticle onStart id:$id")
            }.onEach {
                LogUtils.d(TAG, "addCollectArticle onEach:$it")
                if (it.isSucceed()) {
                    onEvent(RequestState.Success(msg = BaseApplication.Instance.getString(R.string.success_to_collect)))
                    updateArticleCollectState(id = id,isCollect = true)
                } else {
                    onEvent(RequestState.Error(msg = it.errorMsg))
                }
            }.onCompletion {
                LogUtils.d(TAG, "addCollectArticle onCompletion:$it")
                delay(1000.milliseconds)
                onEvent(RequestState.Dismiss)
            }.catch {
                LogUtils.d(TAG, "addCollectArticle catch:$it")
                onEvent(RequestState.Error(msg = BaseApplication.Instance.getString(R.string.failed_to_collect)))
            }
            .launchIn(scope)
    }

    fun unCollectArticle(id:Int,originId: Int = -1,scope: CoroutineScope,onEvent:(RequestState)->Unit) {
        LogUtils.d(TAG,"unCollectArticle id:$id\toriginId:$originId\t")

        (if(id>-1 && originId>-1)
            repository.unCollectArticle(id = id,originId=originId)
        else repository.unCollectArticle(originId = id))
        .onStart {
            onEvent(RequestState.Loading(BaseApplication.Instance.getString(R.string.un_collecting)))
        }.onEach {
            if(it.isSucceed()) {
                onEvent(RequestState.Success(BaseApplication.Instance.getString(R.string.un_collect_success)))
                updateArticleCollectState(id = id,isCollect = false)
            } else {
                onEvent(RequestState.Error(BaseApplication.Instance.getString(R.string.un_collect_fail)))
            }
        }.onCompletion {
            delay(2000.milliseconds)
            onEvent(RequestState.Dismiss)
        }.catch {
            onEvent(RequestState.Error(it.message?:it.toString()))
        }.launchIn(scope)
    }

    fun refreshCollectList() {
        getCollectList(page = defaultPageStart)
    }

    fun loadMoreCollectList() {
        getCollectList(page = curPage+1)
    }
    private fun getCollectList(page:Int) {
        if(isLoading()) {
            return
        }
        repository.getCollectList(page = page)
            .onStart {
                LogUtils.d(TAG,"getCollectList onStart page:$page")
                updateLoadingState(curPage = page)
            }.onEach {
                LogUtils.d(TAG,"getCollectList onEach page:$page\t$it")
                if(it.isSucceed()) {
                    updateCurPage(page)
                    updateDataList(curPage = page,it.data?.datas.orEmpty())
                    updateSucceedState(curPage = page, loadedCount = it.data?.datas.orEmpty().size)
                } else {
                    updateErrorState(curPage = page, errorMsg = it.errorMsg)
                }
            }.onCompletion {
                LogUtils.d(TAG,"getCollectList onCompletion\t_loginUiState:${_loginUiState.value}")
            }.catch {
                LogUtils.d(TAG,"getCollectList catch:$it")
                updateErrorState(curPage = page, errorMsg = it.message?:it.toString())
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "UserViewModelTAG"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserViewModel(repository = UserRepository.Instance)
            }
        }
    }

}