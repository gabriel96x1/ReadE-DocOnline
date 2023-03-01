package com.mutablestate.readonline.domain.utils

import android.util.Log
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
}