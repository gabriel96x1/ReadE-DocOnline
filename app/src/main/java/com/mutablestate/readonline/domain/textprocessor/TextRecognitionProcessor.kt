package com.mutablestate.readonline.domain.textprocessor

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.mutablestate.readonline.R
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
    private val TAG = "TextRecProcessor"
    private lateinit var activity: Activity

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
        val bacKeyParts = text.toBacKeys()

        if (bacKeyParts.docNum.isEmpty() || bacKeyParts.birthDate.isEmpty() || bacKeyParts.expiryDate.isEmpty()) {

            alertDialogSetup(graphicOverlay)

        } else {
            val intent = Intent(graphicOverlay.context, ReaderActivityKt::class.java)
            intent.putExtra("text", text.text)
            intent.putExtra("docNum", bacKeyParts.docNum)
            intent.putExtra("birthDate", bacKeyParts.birthDate)
            intent.putExtra("expiryDate", bacKeyParts.expiryDate)
            graphicOverlay.context.startActivity(intent)
            activity = graphicOverlay.context as Activity
            activity.finish()
        }
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed. $e")

    }

    private fun alertDialogSetup(graphicOverlay: GraphicOverlay) {
        activity = graphicOverlay.context as Activity
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_camera)
        dialog.setTitle("Something went wrong with your photograph")

        val button : Button = dialog.findViewById(R.id.dialogButtonOK)
        button.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }

        dialog.setOnDismissListener {
            dialog.dismiss()
            activity.finish()
        }
        dialog.show()
    }
}
