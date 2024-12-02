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

    private val _userList = mutableListOf<User>() // Local cache for users
    private val _userStateFlow = MutableStateFlow<List<User>>(emptyList())
    val userStateFlow: StateFlow<List<User>> = _userStateFlow

    private val _loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlow: StateFlow<Boolean> = _loadingStateFlow

    fun getUsers(page: Int, results: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadingStateFlow.emit(true)

                Log.d("UserViewModel", "Fetching users with page=$page and results=$results")
                val response = RetrofitClient.apiService.getUsers(page, results)

                if (response.isSuccessful) {
                    response.body()?.results?.let { newUsers ->
                        // Append new users to the existing list
                        _userList.addAll(newUsers)

                        // Emit updated list to StateFlow
                        _userStateFlow.emit(_userList)
                    }
                } else {
                    Log.e("UserViewModel", "Response failed with code: ${response.code()}")
                }
            } catch (exception: Exception) {
                Log.e("UserViewModel", "Exception: ${exception.message}")
            } finally {
                _loadingStateFlow.emit(false)
            }
        }
    }
}
