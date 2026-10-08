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
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: Button
    private lateinit var btnLeica: Button
    private lateinit var btnOis: Button
    private lateinit var btnWatermark: Button
    private lateinit var btnSkin: Button
    private lateinit var btnNature: Button
    private lateinit var btnNight: Button
    private lateinit var btnTripod: Button
    private lateinit var btnUltra: Button
    private lateinit var btnWide: Button
    private lateinit var btnTele: Button
    private lateinit var btnPeri: Button
    private lateinit var btnMacro: Button
    private lateinit var txtFilter: TextView
    private lateinit var txtOis: TextView
    private lateinit var txtLens: TextView
    private lateinit var txtMode: TextView
    
    private var imageCapture: ImageCapture? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    
    private val leicaFilter = LeicaFilter()
    private val oisStabilizer = OisStabilizer()
    private val watermarkHelper = WatermarkHelper()
    private lateinit var lensManager: CameraLensManager
    private val skinEnhancer = SkinToneEnhancer()
    private val natureBooster = NatureBooster()
    private val tripodMode = TripodMode()
    private val nightDay = NightDayOptimizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        lensManager = CameraLensManager(this)
        
        previewView = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture)
        btnLeica = findViewById(R.id.btnLeica)
        btnOis = findViewById(R.id.btnOis)
        btnWatermark = findViewById(R.id.btnWatermark)
        btnSkin = findViewById(R.id.btnSkinTone)
        btnNature = findViewById(R.id.btnNature)
        btnNight = findViewById(R.id.btnNightMode)
        btnTripod = findViewById(R.id.btnTripod)
        btnUltra = findViewById(R.id.btnLensUltra)
        btnWide = findViewById(R.id.btnLensWide)
        btnTele = findViewById(R.id.btnLensTele)
        btnPeri = findViewById(R.id.btnLensPeri)
        btnMacro = findViewById(R.id.btnLensMacro)
        txtFilter = findViewById(R.id.txtFilterName)
        txtOis = findViewById(R.id.txtOisStatus)
        txtLens = findViewById(R.id.txtLensStatus)
        txtMode = findViewById(R.id.txtModeStatus)

        // عدسات الهاتف
        btnUltra.setOnClickListener { lensManager.switchTo(CameraLensManager.Lens.ULTRA_WIDE); txtLens.text = lensManager.name(); startCamera() }
        btnWide.setOnClickListener { lensManager.switchTo(CameraLensManager.Lens.WIDE); txtLens.text = lensManager.name(); startCamera() }
        btnTele.setOnClickListener { lensManager.switchTo(CameraLensManager.Lens.TELE_2X); txtLens.text = lensManager.name(); startCamera() }
        btnPeri.setOnClickListener { lensManager.switchTo(CameraLensManager.Lens.PERISCOPE_5X); txtLens.text = lensManager.name(); startCamera() }
        btnMacro.setOnClickListener { lensManager.switchTo(CameraLensManager.Lens.MACRO); txtLens.text = lensManager.name(); startCamera() }

        btnLeica.setOnClickListener { leicaFilter.next(); txtFilter.text = leicaFilter.name() }
        btnOis.setOnClickListener { val e = oisStabilizer.toggle(); txtOis.text = if(e) "OIS ON" else "OFF"; startCamera() }
        btnWatermark.setOnClickListener { watermarkHelper.toggle() }
        btnSkin.setOnClickListener { skinEnhancer.enabled = !skinEnhancer.enabled; btnSkin.text = if(skinEnhancer.enabled) "SKIN: ON" else "OFF" }
        btnNature.setOnClickListener { natureBooster.enabled = !natureBooster.enabled; btnNature.text = if(natureBooster.enabled) "NATURE: ON" else "OFF" }
        btnNight.setOnClickListener { nightDay.next(); txtMode.text = nightDay.status(); btnNight.text = nightDay.status().take(12); startCamera() }
        btnTripod.setOnClickListener { tripodMode.toggle(); btnTripod.text = tripodMode.status() }
        btnCapture.setOnClickListener { takePhoto() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) startCamera()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        val name = "HyperCam_V8_${System.currentTimeMillis()}"
        val cv = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/HyperCam")
        }
        val output = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv).build()
        capture.takePicture(output, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) { Toast.makeText(baseContext, "Failed", Toast.LENGTH_SHORT).show() }
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(baseContext, "Saved! ${leicaFilter.name()} | ${lensManager.name()} | ${nightDay.status()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            var builder = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            builder = oisStabilizer.apply(builder)
            builder = tripodMode.apply(builder)
            builder = nightDay.apply(builder)
            imageCapture = builder.build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try { cameraProvider.unbindAll(); cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture) } catch (e: Exception) {}
        }, ContextCompat.getMainExecutor(this))
    }
}
