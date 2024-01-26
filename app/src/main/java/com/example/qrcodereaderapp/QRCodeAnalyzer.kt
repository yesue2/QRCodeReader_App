package com.example.qrcodereaderapp

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(val onDetectListener: OnDetectListener) : ImageAnalysis.Analyzer{
    // 바코드 스캐닝 객체 생성
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // 이미지가 찍힐 단시 카메라의 회전 각도를 고려해 입력 이미지 생성
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // scanner.process(image) 통해 이미지 분석
            // SuccessListener, FailureListener, CompleteListener를 각각 달아주어 결과 확인 가능
            scanner.process(image).addOnSuccessListener { qrCodes ->
                // QR 코드가 성공적으로 찍혔을 시
                for (qrCode in qrCodes) {
                    // rawValue가 존재하면 rawValue값을 보내고, null이면 빈 문자열 보냄
                    onDetectListener.onDetect(qrCode.rawValue ?: "")
                }
            }.addOnFailureListener{
                it.printStackTrace()
            }.addOnCompleteListener{
                imageProxy.close()
            }
        }
    }
}