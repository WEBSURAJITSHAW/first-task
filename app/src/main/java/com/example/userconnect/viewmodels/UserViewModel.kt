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

    fun getUsers(page: Int, results: Int) {
        if (isLoading) return // Prevent the call if loading is already in progress

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true // Set loading to true while making the request
                _loadingStateFlow.emit(true)

                val response = RetrofitClient.apiService.getUsers(page, results)

                if (response.isSuccessful) {
                    response.body()?.results?.let { newUsers ->
                        _userList.addAll(newUsers) // Append new data
                        _userStateFlow.emit(_userList) // Emit the updated list
                    }
                } else {
                    Log.e("UserViewModel", "Response failed with code: ${response.code()}")
                }
            } catch (exception: Exception) {
                Log.e("UserViewModel", "Exception: ${exception.message}")
            } finally {
                _loadingStateFlow.emit(false)
                isLoading = false // Set loading to false after the request is finished
            }
        }
    }

}
