package com.hypercam3
import android.graphics.*
class SkinToneEnhancer {
    var enabled = true
    // تحسين دقة ألوان البشرة - بشرة طبيعية بدون احمرار زيادة
    fun enhance(src: Bitmap): Bitmap {
        if(!enabled) return src
        val cm = ColorMatrix(floatArrayOf(
            0.95f, 0.05f, 0f, 0f, 2f,
            0.02f, 1.02f, -0.04f, 0f, 1f,
            0f, -0.02f, 1.02f, 0f, 1f,
            0f, 0f, 0f, 1f, 0f
        ))
        val r = Bitmap.createBitmap(src.width, src.height, src.config?:Bitmap.Config.ARGB_8888)
        val c = Canvas(r); val p = Paint(); p.colorFilter = ColorMatrixColorFilter(cm); c.drawBitmap(src, 0f, 0f, p); return r
    }
}
