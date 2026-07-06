package com.go.mine.http

import com.blankj.utilcode.util.LogUtils
import com.go.mine.data.ArticlePageInfoBean
import com.go.common.http.cookieJar
import com.go.common.http.ApiResponse
import com.go.mine.data.UserBean
import com.go.mine.data.UserCoinInfo
import com.go.mine.data.emptyUserBean
import com.go.mine.dataStore.UserDataStore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.timeout
import kotlin.time.Duration.Companion.seconds

class UserRepository(private val api: UserApi,private val userDataStore: UserDataStore) {

    companion object {
        private const val TAG = "UserRepositoryTAG"
        val Instance by lazy{
            UserRepository(UserApi.Instance, userDataStore = UserDataStore.Instance)
        }
    }

    suspend fun updateUserInfo(userBean:UserBean) {
        userDataStore.write(userBean)
    }

    fun localUser(): Flow<UserBean>  {
        return userDataStore.read()
    }

    fun getUserinfo(): Flow<ApiResponse<UserCoinInfo>> {
        return api.getUserinfo()
    }

    /** 登录 */
    fun login(username: String,password: String): Flow<ApiResponse<UserBean>> {
        return api.login(username,password).onEach {
            if(it.isSucceed()) {
                it.data?.let { data ->
                    userDataStore.write(data)
                }
            }
        }.catch {
            LogUtils.d(TAG,"it:$it")
        }
    }

    /** 注册 */
    fun register(username: String,password: String,repassword: String): Flow<ApiResponse<Any?>> {
        return api.register(username=username, password = password, repassword = repassword)
    }

    /** 退出登陆 */
    @OptIn(FlowPreview::class)
    fun logout(): Flow<ApiResponse<Any?>> {
        return api.logout().timeout(2.seconds).onCompletion {
            userDataStore.write(emptyUserBean)
            cookieJar.clear()
        }
    }

    fun addCollectArticle(id:Int): Flow<ApiResponse<Any?>> {
        return api.addCollectArticle(id = id)
    }

    fun getCollectList(page:Int): Flow<ApiResponse<ArticlePageInfoBean>> {
        return api.getCollectList(page = page)
    }

    fun unCollectArticle(id:Int,originId: Int = -1):Flow<ApiResponse<Any?>> {
        return api.unCollectArticle(id = id,originId = originId)
    }

    fun unCollectArticle(originId: Int):Flow<ApiResponse<Any?>> {
        return api.unCollectArticle(originId = originId)
    }

}