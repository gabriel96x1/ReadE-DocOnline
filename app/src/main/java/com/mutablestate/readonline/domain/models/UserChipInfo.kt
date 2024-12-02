package com.mutablestate.readonline.domain.models

import android.graphics.Bitmap
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG7File

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
    val photo: Bitmap?,
    val extraInfo: DG11File?,
    val signature: DG7File?,
    val issuer: DG12File?
)
