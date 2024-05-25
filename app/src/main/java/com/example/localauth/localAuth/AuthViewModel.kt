package com.example.localauth.localAuth

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel:ViewModel() {

    private var _authResult=MutableLiveData<BioMetricResult>()
    var authResult:LiveData<BioMetricResult> = _authResult

    fun authenticateUser(title:String,description:String,activity:AppCompatActivity){
        var manager= BiometricManager.from(activity)
        val authenticators=if(Build.VERSION.SDK_INT>=30) BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL else BiometricManager.Authenticators.BIOMETRIC_STRONG

        var promptInfo= BiometricPrompt.PromptInfo.Builder()
        promptInfo.also {
            it.setTitle(title)
            it.setDescription(description)
            it.setAllowedAuthenticators(authenticators)
        }

        if(Build.VERSION.SDK_INT<30){
            promptInfo.setNegativeButtonText("Cancel")
        }

        when(manager.canAuthenticate(authenticators)){
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE->{
                _authResult.value=BioMetricResult.BioMetricHardwareUnavailable
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE->{
                _authResult.value=BioMetricResult.BioMetricNotAvailable
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->{
                _authResult.value=BioMetricResult.BioMetricNotEnrolled
                return
            }
            else->Unit
        }

        val prompt=BiometricPrompt(activity,object :BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _authResult.value=BioMetricResult.BioMetricFailed
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _authResult.value=BioMetricResult.BioMetricSuccess
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                _authResult.value=BioMetricResult.BioMetricError(error = errString.toString())
            }
        })

        prompt.authenticate(promptInfo.build())
    }
}