package com.mutablestate.readonline.domain.textprocessor

import android.util.Log
import androidx.compose.ui.text.toUpperCase
import com.google.mlkit.vision.text.Text
import com.mutablestate.readonline.domain.models.BacKeyParts
import java.util.*
import kotlin.collections.ArrayList

fun Text.toBacKeys(): BacKeyParts {
    var docNum: String
    val birthDate: String
    val expiryDate: String

    var mrz = textBlocks[textBlocks.size - 1].text
    val lines: MutableList<String> = ArrayList()

    Log.d("ReadTextFromOCR", text)

    try {
        if (mrz.length < 33) {
            lines.add(textBlocks[textBlocks.size - 3].text.replace(" ".toRegex(), "").uppercase())
            lines.add(textBlocks[textBlocks.size - 2].text.replace(" ".toRegex(), "").uppercase())
            lines.add(mrz.replace(" ".toRegex(), "").uppercase())
        } else {
            mrz = mrz.replace(" ".toRegex(), "").uppercase()
            val builder = StringBuilder()
            for (i in 0 until mrz.length) {
                if (mrz[i] == '\n') {
                    lines.add(builder.toString())
                    builder.delete(0, builder.length)
                } else {
                    builder.append(mrz[i])
                }
            }
            lines.add(builder.toString())
        }

        if (lines.size == 3) {
            Log.v("MANUAL_TESTING_LOG", "Is an ID")
            docNum = lines[0]
            val dates = lines[1]
            docNum = docNum.substring(5, 14)
            docNum = docNum.replace("O".toRegex(), "0")
            birthDate = dates.substring(0, 6)
            expiryDate = dates.substring(8, 14)
        } else {
            Log.v("MANUAL_TESTING_LOG", "Is a Passport")
            val infoLine = lines[lines.size - 1]
            docNum = infoLine.substring(0, 9)
            docNum = docNum.replace("O".toRegex(), "0")
            birthDate = infoLine.substring(13, 19)
            expiryDate = infoLine.substring(21, 27)
        }

        Log.d("MRZParts", "$docNum $birthDate $expiryDate")

        return BacKeyParts(
            docNum = docNum,
            birthDate = birthDate,
            expiryDate = expiryDate
        )

    } catch (e: Exception) {
        Log.e("ErrorReadingMrz", e.message.toString())
        return BacKeyParts(
            docNum = "",
            birthDate = "",
            expiryDate = ""
        )
    }
}