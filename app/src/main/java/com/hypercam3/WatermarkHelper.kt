package com.hypercam3
import android.graphics.*
class WatermarkHelper {
    var enabled = true
    fun toggle(): Boolean { enabled = !enabled; return enabled }
    fun add(src: Bitmap): Bitmap {
        if(!enabled) return src
        val r = src.copy(src.config?:Bitmap.Config.ARGB_8888,true)
        val c = Canvas(r); val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val w = r.width; val h = r.height; val barH = (h*0.09f).toInt()
        val gp = Paint(); gp.shader = LinearGradient(0f,(h-barH).toFloat(),0f,h.toFloat(),intArrayOf(Color.TRANSPARENT,Color.parseColor("#DD000000")),null,Shader.TileMode.CLAMP)
        c.drawRect(0f,(h-barH).toFloat(),w.toFloat(),h.toFloat(),gp)
        p.color = Color.WHITE; p.textSize = w*0.038f; p.typeface = Typeface.create(Typeface.DEFAULT,Typeface.BOLD); p.letterSpacing = 0.05f
        val x = w*0.05f; val y = h*0.96f; c.drawText("LEICA",x,y,p)
        val dot = Paint(Paint.ANTI_ALIAS_FLAG); dot.color = Color.RED
        c.drawCircle(x+p.measureText("LEICA")+w*0.02f,y-p.textSize*0.3f,w*0.008f,dot)
        p.textSize = w*0.025f; p.typeface = Typeface.DEFAULT; p.alpha = 200
        c.drawText("VARIO-SUMMILUX 1:1.8-2.2/14-75 ASPH. | LIQUID GLASS",x,y-h*0.035f,p)
        p.textSize = w*0.028f; p.textAlign = Paint.Align.RIGHT; c.drawText("Shot on HyperCam Liquid",w*0.95f,y,p)
        return r
    }
}
