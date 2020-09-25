package com.huawei.mlkit.example.face

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting
import com.huawei.mlkit.example.R
import com.huawei.mlkit.example.camera.GraphicOverlay
import com.huawei.mlkit.example.camera.LensEnginePreview
import kotlinx.android.synthetic.main.activity_live_face_analyse.*
import java.io.IOException

class LiveFaceAnalyseActivity : AppCompatActivity(), View.OnClickListener {
    private var analyzer: MLFaceAnalyzer? = null
    private lateinit var mLensEngine: LensEngine
    private lateinit var mPreview: LensEnginePreview
    private lateinit var mOverlay: GraphicOverlay
    private var lensType = LensEngine.BACK_LENS
    private var isFront = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_live_face_analyse)
        if (savedInstanceState != null) {
            lensType = savedInstanceState.getInt("lensType")
        }
        mPreview = preview
        mOverlay = overlay
        facingSwitch.setOnClickListener(this);
        createFaceAnalyzer()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            createLensEngine()
        } else {
            requestCameraPermission()
        }
    }

    private fun createFaceAnalyzer() {
        val setting = MLFaceAnalyzerSetting.Factory()
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                .setMinFaceProportion(0.2f)
                .allowTracing()
                .create()
        analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting)
        analyzer?.setTransactor(FaceAnalyzerTransactor(mOverlay))
    }

    private fun createLensEngine() {
        val context = this.applicationContext
        mLensEngine = LensEngine.Creator(context, analyzer)
                .setLensType(lensType)
                .applyDisplayDimension(640, 480)
                .applyFps(25.0f)
                .enableAutomaticFocus(true)
                .create()
    }

    override fun onResume() {
        super.onResume()
        startLensEngine()
    }

    private fun startLensEngine() {
        try {
            mPreview.start(mLensEngine, mOverlay)
        } catch (e: IOException) {
            mLensEngine.release()
        }
    }

    override fun onPause() {
        super.onPause()
        mPreview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLensEngine.release()
        if (analyzer != null) {
            try {
                analyzer!!.stop()
            } catch (e: IOException) {
            }
        }
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != CAMERA_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createLensEngine()
            return
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("lensType", lensType)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onClick(v: View) {
        isFront = !isFront
        if (isFront) {
            lensType = LensEngine.FRONT_LENS
        } else {
            lensType = LensEngine.BACK_LENS
        }
        mLensEngine.close()
        createLensEngine()
        startLensEngine()
    }

    companion object {
        const val CAMERA_PERMISSION_CODE = 2
    }
}