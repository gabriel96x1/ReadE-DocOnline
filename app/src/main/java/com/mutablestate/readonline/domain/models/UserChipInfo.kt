package com.mutablestate.readonline.domain.models

import android.graphics.Bitmap

data class UserChipInfo(
    val primaryId: String,
    val secondId: String,
    val gender: String,
    val issuingState: String,
    val nationality: String,
    val docNum: String,
    val expiryDate: String,
    val passiveAuth: String,
    val ChipAuth: String,
    val photo: Bitmap
)
