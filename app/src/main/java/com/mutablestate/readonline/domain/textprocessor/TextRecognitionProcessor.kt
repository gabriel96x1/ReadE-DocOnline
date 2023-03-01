package com.mutablestate.readonline.domain.textprocessor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.mutablestate.readonline.domain.textprocessor.ocrinternals.GraphicOverlay
import com.mutablestate.readonline.domain.textprocessor.ocrinternals.PreferenceUtils
import com.mutablestate.readonline.domain.textprocessor.ocrinternals.VisionProcessorBase
import com.mutablestate.readonline.presentation.activities.ReaderActivityKt

class TextRecognitionProcessor(
    context: Context?, textRecognizerOptions: TextRecognizerOptionsInterface?
) : VisionProcessorBase<Text?>(context) {
    private val textRecognizer: TextRecognizer
    private val shouldGroupRecognizedTextInBlocks: Boolean
    private val showLanguageTag: Boolean

    init {
        shouldGroupRecognizedTextInBlocks =
            PreferenceUtils.shouldGroupRecognizedTextInBlocks(context)
        showLanguageTag = PreferenceUtils.showLanguageTag(context)
        textRecognizer = TextRecognition.getClient(textRecognizerOptions!!)
    }

    override fun stop() {
        textRecognizer.close()
    }

    override fun detectInImage(image: InputImage?): Task<Text?> {
        return textRecognizer.process(image!!)
    }

    override fun onSuccess(text: Text, graphicOverlay: GraphicOverlay) {
        Log.d(TAG, "On-device Text detection successful")
        logExtrasForTesting(text)
        val bacKeyParts = text.toBacKeys()
        val intent = Intent(graphicOverlay.context, ReaderActivityKt::class.java)
        intent.putExtra("docNum", bacKeyParts.docNum)
        intent.putExtra("birthDate", bacKeyParts.birthDate)
        intent.putExtra("expiryDate", bacKeyParts.expiryDate)
        graphicOverlay.context.startActivity(intent)


        //graphicOverlay.add(
        //new TextGraphic(graphicOverlay, text, shouldGroupRecognizedTextInBlocks, showLanguageTag));
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed. $e")
    }

    companion object {
        private const val TAG = "TextRecProcessor"
        private fun logExtrasForTesting(text: Text?) {
            if (text != null) {
                Log.v(MANUAL_TESTING_LOG, "Detected text has : " + text.textBlocks.size + " blocks")
                for (i in text.textBlocks.indices) {
                    val lines = text.textBlocks[i].lines
                    Log.v(
                        MANUAL_TESTING_LOG,
                        String.format("Detected text block %d has %d lines", i, lines.size)
                    )
                    for (j in lines.indices) {
                        val elements = lines[j].elements
                        Log.v(
                            MANUAL_TESTING_LOG,
                            String.format("Detected text line %d has %d elements", j, elements.size)
                        )
                        for (k in elements.indices) {
                            val element = elements[k]
                            Log.v(
                                MANUAL_TESTING_LOG,
                                String.format("Detected text element %d says: %s", k, element.text)
                            )
                            Log.v(
                                MANUAL_TESTING_LOG, String.format(
                                    "Detected text element %d has a bounding box: %s",
                                    k, element.boundingBox!!.flattenToString()
                                )
                            )
                            Log.v(
                                MANUAL_TESTING_LOG, String.format(
                                    "Expected corner point size is 4, get %d",
                                    element.cornerPoints!!.size
                                )
                            )
                            for (point in element.cornerPoints!!) {
                                Log.v(
                                    MANUAL_TESTING_LOG, String.format(
                                        "Corner point for element %d is located at: x - %d, y = %d",
                                        k, point.x, point.y
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
