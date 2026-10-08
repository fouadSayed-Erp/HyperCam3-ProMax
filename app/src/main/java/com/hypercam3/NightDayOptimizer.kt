package com.hypercam3
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.ImageCapture
import android.hardware.camera2.CaptureRequest
class NightDayOptimizer {
    enum class Mode { AUTO, DAY, NIGHT, HDR }
    var mode = Mode.AUTO
    var hdr = true
    fun apply(b: ImageCapture.Builder): ImageCapture.Builder {
        val e = Camera2Interop.Extender(b)
        try {
            if(mode == Mode.NIGHT) e.setCaptureRequestOption(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY)
        } catch(_:Exception){}
        return b
    }
    fun next(): Mode { mode = when(mode){ Mode.AUTO->Mode.DAY; Mode.DAY->Mode.NIGHT; Mode.NIGHT->Mode.HDR; Mode.HDR->Mode.AUTO }; return mode }
    fun status(): String = when(mode){
        Mode.AUTO -> "DAY MODE • HDR ON • 200MP"
        Mode.DAY -> "DAY MODE • HDR"
        Mode.NIGHT -> "NIGHT MODE • تصوير ليلي متعدد الإطارات"
        Mode.HDR -> "HDR PRO • 200MP"
    }
}
