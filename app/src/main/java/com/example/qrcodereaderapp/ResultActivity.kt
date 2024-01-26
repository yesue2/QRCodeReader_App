package com.example.qrcodereaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qrcodereaderapp.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val result = intent.getStringExtra("msg") ?: "데이터가 존재하지 않습니다."
        setUI(result)
    }

    private fun setUI(result: String) {
        binding.tvResult.text = result
        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }
}