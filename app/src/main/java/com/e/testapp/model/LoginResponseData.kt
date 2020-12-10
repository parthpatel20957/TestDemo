package com.e.testapp.model

data class LoginResponseData(
    val errorCode: String,
    val errorMessage: String,
    val user: User
) {
    data class User(
        val userId: Int,
        val userName: String
    )
}