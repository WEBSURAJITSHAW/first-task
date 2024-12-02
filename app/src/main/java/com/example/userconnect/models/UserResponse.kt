package com.example.userconnect.models

data class UserResponse(
    val info: Info,
    val results: List<User>
)