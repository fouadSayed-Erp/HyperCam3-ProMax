package com.hypercam3
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.ImageCapture
import android.hardware.camera2.CaptureRequest
class TripodMode {
    var enabled = false
    var timer = 0
    fun apply(b: ImageCapture.Builder): ImageCapture.Builder {
        if(enabled) {
            val e = Camera2Interop.Extender(b)
            try { e.setCaptureRequestOption(CaptureRequest.CONTROL_AE_LOCK, true) } catch(_:Exception){}
        }
        return b
    }
    fun toggle(): Boolean { enabled = !enabled; return enabled }
    fun status(): String = if(enabled) "TRIPOD: ON - ثابت" else "TRIPOD: OFF"
}
