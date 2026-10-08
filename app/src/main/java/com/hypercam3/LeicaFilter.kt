package com.hypercam3
import android.graphics.*
class LeicaFilter {
    enum class Mode { VIVID, NATURAL, AUTHENTIC, BW }
    var mode = Mode.VIVID
    fun next(): Mode { mode = when(mode){ Mode.VIVID->Mode.NATURAL; Mode.NATURAL->Mode.AUTHENTIC; Mode.AUTHENTIC->Mode.BW; Mode.BW->Mode.VIVID }; return mode }
    fun name(): String = when(mode){
        Mode.VIVID -> "LEICA VIVID • LIQUID GLASS"
        Mode.NATURAL -> "LEICA NATURAL • GLASS"
        Mode.AUTHENTIC -> "LEICA AUTHENTIC • FILM"
        Mode.BW -> "LEICA BW • MONO"
    }
    fun apply(b: Bitmap): Bitmap {
        val cm = ColorMatrix()
        when(mode){
            Mode.VIVID -> { cm.setSaturation(1.35f); val c=ColorMatrix(floatArrayOf(1.15f,0f,0f,0f,-10f,0f,1.15f,0f,0f,-10f,0f,0f,1.15f,0f,-10f,0f,0f,0f,1f,0f)); cm.postConcat(c) }
            Mode.NATURAL -> cm.setSaturation(0.95f)
            Mode.AUTHENTIC -> cm.set(floatArrayOf(1.1f,0f,0f,0f,5f,0f,1.05f,0f,0f,2f,0f,0f,0.9f,0f,-5f,0f,0f,0f,1f,0f))
            Mode.BW -> cm.setSaturation(0f)
        }
        val r = Bitmap.createBitmap(b.width,b.height,b.config?:Bitmap.Config.ARGB_8888)
        val canvas = Canvas(r); val p = Paint(); p.colorFilter = ColorMatrixColorFilter(cm); canvas.drawBitmap(b,0f,0f,p); return r
    }
}
