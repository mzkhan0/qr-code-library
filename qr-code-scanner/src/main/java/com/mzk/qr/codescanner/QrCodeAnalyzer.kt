package com.mzk.qr.codescanner

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

private const val TAG = "QrCodeAnalyzer"

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_39).build())

            scanner.process(image).addOnSuccessListener {barcodes->
                Log.d(TAG, "analyze: success $barcodes")
                if (barcodes.isNotEmpty()){
                    val res = barcodes[0].rawValue
                    res?.let {
                        onQrCodeScanned(it)
                    }
                }
                imageProxy.close()
                mediaImage.close()
            }.addOnFailureListener {
                Log.d(TAG, "analyze: ${it.message}")
            }
        }
    }
}
