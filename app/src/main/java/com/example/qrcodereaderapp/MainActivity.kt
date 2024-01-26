package com.example.qrcodereaderapp

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodereaderapp.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    // 바인딩 변수 생성
    private lateinit var binding : ActivityMainBinding
    // ListenableFuture형 변수 생성 => ListenableFuture에 태스크가 제대로 끝났을 때 동작 지정 가능
    private lateinit var cameraProvideFuture : ListenableFuture<ProcessCameraProvider>
    // 태그 기능 코드 => 나중에 권한을 요청한 후 결과를 onRequestPermissionsResult에서 받을 떄 필요
    // 0과 같거나 큰 양수이기만 하면 어떤 수든 상관없음
    private val PERMISSIONS_REQUEST_CODE = 1
    // 카메라 권한 지정
    private val PERMISSIONS_REQUIRED = arrayOf(android.Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 클래스에 포함된 inflate() 함수를 실행해 바인딩 클래스의 객체 생성
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root  // 바인딩 객체의 root 뷰 참조
        setContentView(view)  // 생성한 뷰 설정

        if (!hasPermissions(this)) {  // 권한이 없을 때
            // 카메라 권한 요청
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            // 이미 권한 있을 때
            startCamera()
        }
    }

    // all => PERMISSIONS_REQUIRED 배열의 원소가 모두 조건문을 만족하면 true, 아니면 false 반환
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청 콜백 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // onCreate() 메서드에서 requestPermissions의 인수로 넣은 PERMISSIONS_REQUEST_CODE와 맞는지 확인
        if (requestCode == PERMISSIONS_REQUEST_CODE) {  // 권한 수락 시
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()){
                Toast.makeText(this@MainActivity, "권한 요청이 승인되었습니다.", Toast.LENGTH_LONG).show()
                startCamera()
            } else {  // 권한 거부 시
                Toast.makeText(this@MainActivity, "권한 요청이 거부되었습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    fun getImageAnalysis() : ImageAnalysis {
        val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder().build()

        // QRCodeAnalyzer 객체 생성 후 setAnalyzer() 함수의 인수로 넣어줌
        // object를 통해 OnDetectListener 인터페이스 객체 생성 후 onDetect() 함수를 오버라이드
        imageAnalysis.setAnalyzer(cameraExecutor, QRCodeAnalyzer(object : OnDetectListener {
            override fun onDetect(msg: String) {
                // onDetect() 함수가 QRCodeAnalyzer에서 불렀을 때 행동 정의
                Toast.makeText(this@MainActivity, "${msg}", Toast.LENGTH_SHORT).show()
            }
        }))
        return imageAnalysis
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
            // 이미지분석 객체 가져오기
            val imageAnalysis = getImageAnalysis()
            // DEFAULT_BACK_CAMERA(후면 카메라) 선택
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // preview(미리보기) 쓰기 선택
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
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