package com.example.userconnect.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userconnect.api.RetrofitClient
import com.example.userconnect.models.User
import com.example.userconnect.models.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _userList = mutableListOf<User>()

    private val _userStateFlow = MutableStateFlow<List<User>>(emptyList())
    val userStateFlow: StateFlow<List<User>> = _userStateFlow

    private val _loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlow: StateFlow<Boolean> = _loadingStateFlow

    private var isLoading = false
    private var hasMoreData = true
    private var currentPage = 1
    private val pageSize = 10

    fun fetchUsers() {
        if (isLoading || !hasMoreData) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                _loadingStateFlow.emit(true)

                val response = RetrofitClient.apiService.getUsers(currentPage, pageSize)

                if (response.isSuccessful) {
                    val newUsers = response.body()?.results.orEmpty()

                    if (newUsers.isNotEmpty()) {
                        _userList.addAll(newUsers)
                        _userStateFlow.emit(_userList)

                        currentPage++ // Increment page for the next call
                    } else {
                        hasMoreData = false // No more data available or
                        Log.d("UserViewModel", "No more users to fetch.")
                    }
                } else {
                    Log.e("UserViewModel", "API error: ${response.code()}")
                }
            } catch (exception: Exception) {
                Log.e("UserViewModel", "Exception: ${exception.message}")
            } finally {
                _loadingStateFlow.emit(false)
                isLoading = false // Reset loading state
            }
        }
    }
}
