package com.example.userconnect.models

data class User(
    val cell: String,
    val dob: Dob,
    val email: String,
    val gender: String,
    val id: Id,
    val location: Location?,
    val login: Login?,
    val name: Name,
    val nat: String,
    val phone: String,
    val picture: Picture,
    val registered: Registered,

    var isExtraVisible: Boolean = false // Flag to toggle extra data
)