package com.hypercam3
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
class MainActivity:AppCompatActivity(){
    lateinit var previewView: PreviewView
    lateinit var btnCapture: Button; lateinit var btnLeica: Button; lateinit var btnOis: Button; lateinit var btnWatermark: Button; lateinit var btnGlass: Button
    lateinit var txtFilter: TextView; lateinit var txtOis: TextView
    var imageCapture: ImageCapture? = null
    val exec = Executors.newSingleThreadExecutor()
    val leica = LeicaFilter(); val ois = OisStabilizer(); val wm = WatermarkHelper()
    override fun onCreate(s:Bundle?){
        super.onCreate(s); setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture); btnLeica = findViewById(R.id.btnLeica); btnOis = findViewById(R.id.btnOis)
        btnWatermark = findViewById(R.id.btnWatermark); btnGlass = findViewById(R.id.btnGlassMode)
        txtFilter = findViewById(R.id.txtFilterName); txtOis = findViewById(R.id.txtOisStatus)
        btnLeica.setOnClickListener{ leica.next(); txtFilter.text = leica.name(); Toast.makeText(this, leica.name(), Toast.LENGTH_SHORT).show() }
        btnOis.setOnClickListener{ val e = ois.toggle(); txtOis.text = if(e)"OIS ON" else "OIS OFF"; btnOis.text = if(e)"OIS ON" else "OIS OFF"; startCamera() }
        btnWatermark.setOnClickListener{ val e = wm.toggle(); btnWatermark.text = if(e)"WATERMARK: ON" else "OFF"; Toast.makeText(this, if(e)"Leica Watermark ON" else "OFF", Toast.LENGTH_SHORT).show() }
        btnGlass.setOnClickListener{ Toast.makeText(this,"Liquid Glass iOS26 Style ON",Toast.LENGTH_SHORT).show() }
        btnCapture.setOnClickListener{ takePhoto() }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) startCamera()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
    }
    fun takePhoto(){
        val ic = imageCapture ?: return
        val name = "HyperCam_Liquid_${System.currentTimeMillis()}"
        val cv = ContentValues().apply{ put(MediaStore.MediaColumns.DISPLAY_NAME,name); put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg"); put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/HyperCam") }
        val opts = ImageCapture.OutputFileOptions.Builder(contentResolver,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv).build()
        ic.takePicture(opts,ContextCompat.getMainExecutor(this),object:ImageCapture.OnImageSavedCallback{
            override fun onError(e:ImageCaptureException){ Toast.makeText(baseContext,"Failed: ${e.message}",Toast.LENGTH_SHORT).show() }
            override fun onImageSaved(o:ImageCapture.OutputFileResults){ Toast.makeText(baseContext,"Saved! ${leica.name()} | ${ois.status()} | Liquid Glass",Toast.LENGTH_LONG).show() }
        })
    }
    fun startCamera(){
        val f = ProcessCameraProvider.getInstance(this)
        f.addListener({
            val cp = f.get()
            val preview = Preview.Builder().build().also{ it.setSurfaceProvider(previewView.surfaceProvider) }
            var b = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            b = ois.apply(b); imageCapture = b.build()
            try{ cp.unbindAll(); cp.bindToLifecycle(this,CameraSelector.DEFAULT_BACK_CAMERA,preview,imageCapture) }catch(_:Exception){}
        },ContextCompat.getMainExecutor(this))
    }
}
