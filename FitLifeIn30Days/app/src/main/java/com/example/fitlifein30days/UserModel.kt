package com.example.fitlifein30days

data class UserModel(
    var name: String,
    var surname: String,
    var gender : String,
    var weight: Double,
    var height: Double,
    var age: Int,
    var progress: MutableList<Boolean> = MutableList(30) { false }
)
