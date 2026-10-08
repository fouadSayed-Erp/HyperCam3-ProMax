package com.hypercam3
import android.content.Context
import android.hardware.camera2.CameraManager
class CameraLensManager(private val ctx: Context) {
    enum class Lens { ULTRA_WIDE, WIDE, TELE_2X, PERISCOPE_5X, MACRO }
    var current = Lens.WIDE
    fun switchTo(l: Lens): String { current = l; return name() }
    fun name(): String = when(current) {
        Lens.ULTRA_WIDE -> "0.6X ULTRA WIDE"
        Lens.WIDE -> "1X WIDE • 200MP"
        Lens.TELE_2X -> "2X TELEPHOTO"
        Lens.PERISCOPE_5X -> "5X PERISCOPE"
        Lens.MACRO -> "MACRO • 2CM"
    }
}
