package com.example.qrcodereaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodereaderapp.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : AppCompatActivity() {
    // 바인딩 변수 생성
    private lateinit var binding : ActivityMainBinding
    // ListenableFuture형 변수 생성 => ListenableFuture에 태스크가 제대로 끝났을 때 동작 지정 가능
    private lateinit var cameraProvideFuture : ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 클래스에 포함된 inflate() 함수를 실행해 바인딩 클래스의 객체 생성
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root  // 바인딩 객체의 root 뷰 참조
        setContentView(view)  // 생성한 뷰 설정

        startCamera()
    }

    fun startCamera() {
        // cameraProvideFuture에 객체의 참조값 할당
        cameraProvideFuture = ProcessCameraProvider.getInstance(this)
        // cameraProvideFuture 태스크가 끝나면 실행
        cameraProvideFuture.addListener(Runnable {
            // ProcessCameraProvider 객체 가져오기
            // ProcessCameraProvider: 카메라의 생명 주기를 액티비티 생명 주기에 바인드 해줌
            val cameraProvider = cameraProvideFuture.get()
            // 미리보기 객체 가져오기
            val preview = getPreview()
            // DEFAULT_BACK_CAMERA(후면 카메라) 선택
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // preview(미리보기) 쓰기 선택
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(this))
    }

    fun getPreview() : Preview {
        val preview : Preview = Preview.Builder().build()  // Preview 객체 생성
        // setSurfaceProvider(): Preview 객체에 SurfaceProvider를 설정해줌
        //SurfaceProvider: Preview에 Surface(화면에 보여지는 픽셀들이 모여 있는 객체)를 제공해주는 인터페이스
        preview.setSurfaceProvider(binding.barcodePreview.getSurfaceProvider())
        return preview
    }
}