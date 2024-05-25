package com.example.localauth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.localauth.databinding.ActivityMainBinding
import com.example.localauth.localAuth.AuthViewModel
import com.example.localauth.localAuth.BioMetricResult

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel by lazy {
        ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.authButton.setOnClickListener {
            authViewModel.authenticateUser(title = "Authentication", description = "Authenticate user",this)

        }

        authViewModel.authResult.observe(this){
            var result = when(it){
                BioMetricResult.BioMetricFailed->{
                    "Authentication Failed"
                }
                is BioMetricResult.BioMetricError ->{
                    "Authentication Error: ${it.error}"
                }
                BioMetricResult.BioMetricHardwareUnavailable ->{
                    "Biometric Hardware Unavailable"
                }
                BioMetricResult.BioMetricNotAvailable ->{
                    "Biometric Not Available"
                }
                BioMetricResult.BioMetricNotEnrolled ->{
                    if(Build.VERSION.SDK_INT>=30){
                        var enrollIntent=Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                        }
                        startActivity(enrollIntent)
                    }
                    "Biometric Not Enrolled"
                }
                BioMetricResult.BioMetricSuccess ->{
                    "Authentication Success"
                }
            }
            binding.authText.text=result
        }

    }
}