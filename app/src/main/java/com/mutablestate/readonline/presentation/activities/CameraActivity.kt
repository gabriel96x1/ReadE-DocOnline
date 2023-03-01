package com.mutablestate.readonline.presentation.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mutablestate.readonline.R
import com.mutablestate.readonline.domain.textprocessor.TextRecognitionProcessor
import com.mutablestate.readonline.domain.textprocessor.ocrinternals.GraphicOverlay
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.max


class CameraActivity : AppCompatActivity() {
    private lateinit var previewView : PreviewView
    private lateinit var graphicOverlay : GraphicOverlay
    private lateinit var captureBtn : Button
    private lateinit var imageProcessor : TextRecognitionProcessor
    private lateinit var cameraSelector : CameraSelector
    private lateinit var imageCapture : ImageCapture
    private var imageMaxWidth: Int? = null
    private var imageMaxHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        imageProcessor = TextRecognitionProcessor(this, TextRecognizerOptions.Builder().build())

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        previewView = findViewById(R.id.previewView)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        captureBtn = findViewById(R.id.btn_take_pic)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        captureBtn.setOnClickListener {

            // Capture the image and save it to the file
            imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val bitmap = imageProxyToBitmap(image)

                    tryReloadAndDetectInImage(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    // Image capture failed, handle the error
                    Log.e("Camera Capture", exception.message.toString())

                }
            })
        }

        imageMaxHeight = previewView.height
        imageMaxWidth = previewView.width
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        imageCapture = ImageCapture.Builder().build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun tryReloadAndDetectInImage(bitmap: Bitmap) {
        Log.d("Detection Try", "Try reload and detect image")
        try {

            // Clear the overlay first
            graphicOverlay.clear()

            graphicOverlay.setImageSourceInfo(
                bitmap.width, bitmap.height,false
            )

            imageProcessor.processBitmap(bitmap, graphicOverlay)
        } catch (e: IOException) {
            Log.e("Detection Try", "Error retrieving saved image")
        }
    }
}