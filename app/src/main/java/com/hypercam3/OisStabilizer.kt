package com.hypercam3
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.ImageCapture
import android.hardware.camera2.CaptureRequest
class OisStabilizer {
    var enabled = true
    fun apply(b: ImageCapture.Builder): ImageCapture.Builder {
        if(enabled){
            val e = Camera2Interop.Extender(b)
            e.setCaptureRequestOption(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON)
            try{ e.setCaptureRequestOption(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON) }catch(_:Exception){}
        }
        return b
    }
    fun toggle(): Boolean { enabled = !enabled; return enabled }
    fun status(): String = if(enabled) "OIS ON" else "OIS OFF"
}
