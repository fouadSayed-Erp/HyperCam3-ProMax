package com.hypercam3
import android.graphics.*
class NatureBooster {
    var enabled = true
    // تحسين ألوان الطبيعة - أخضر وأزرق طبيعي بدون ما يأثر على البشرة
    fun boost(src: Bitmap): Bitmap {
        if(!enabled) return src
        val cm = ColorMatrix(floatArrayOf(
            1.0f, 0f, 0f, 0f, 0f,
            -0.1f, 1.25f, -0.15f, 0f, -5f,
            -0.05f, -0.1f, 1.15f, 0f, -3f,
            0f, 0f, 0f, 1f, 0f
        ))
        val r = Bitmap.createBitmap(src.width, src.height, src.config?:Bitmap.Config.ARGB_8888)
        val c = Canvas(r); val p = Paint(); p.colorFilter = ColorMatrixColorFilter(cm); c.drawBitmap(src, 0f, 0f, p); return r
    }
}
