package com.mutablestate.readonline.domain.utils

import android.util.Log
import com.google.mlkit.vision.text.Text
import com.mutablestate.readonline.domain.models.UserChipInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object ReadingUtils {
    fun convertDate(input: String?): String? {
        return if (!input.isNullOrEmpty()) {
            try {
                SimpleDateFormat("yyMMdd", Locale.US)
                    .format(SimpleDateFormat("yyMMdd", Locale.US).parse(input)!!)
            } catch (e: ParseException) {
                Log.e("Date Conversion", e.toString())
                null
            }
        } else null
    }

    fun isRealId(text: String, chipInfo: UserChipInfo): Boolean {
        var count = 0
        if (text.contains(chipInfo.primaryId.replace(" ", ""))) count++
        if (text.contains(chipInfo.secondId.replace(" ", ""))) count++
        if (text.contains(chipInfo.nationality)) count++
        if (text.contains(chipInfo.gender[0])) count++
        if (text.contains(chipInfo.docNum)) count++
        if (text.contains(chipInfo.expiryDate)) count++

        Log.d("Count", count.toString())

        return count > 3
    }
}