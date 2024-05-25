package com.example.localauth.localAuth

sealed interface BioMetricResult{
    data object BioMetricFailed:BioMetricResult
    data object BioMetricHardwareUnavailable:BioMetricResult
    data object BioMetricNotEnrolled:BioMetricResult
    data object BioMetricSuccess:BioMetricResult
    data object BioMetricNotAvailable:BioMetricResult
    data class BioMetricError(val error:String):BioMetricResult
}
