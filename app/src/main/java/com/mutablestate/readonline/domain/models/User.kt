package com.mutablestate.readonline.domain.models

data class User(
    val lastname: String,
    val birthDate: String,
    val mrz: String = "",
    val name: String,
    val sex: String
)
