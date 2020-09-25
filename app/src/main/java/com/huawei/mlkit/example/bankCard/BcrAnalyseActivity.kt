package com.huawei.mlkit.example.bankCard

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult
import com.huawei.mlkit.example.R
import kotlinx.android.synthetic.main.activity_image_bcr_analyse.*

class BcrAnalyseActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_image_bcr_analyse)
        Bank_Card_image.setScaleType(ImageView.ScaleType.FIT_XY)
        detect.setOnClickListener(this)
        Bank_Card_image.setOnClickListener(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE)
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_CODE)
        }
    }

    override fun onClick(v: View) {
        text_result.text = ""
        startCaptureActivity(banCallback)
    }

    private fun formatIdCardResult(bankCardResult: MLBcrCaptureResult): String {
        val resultBuilder = StringBuilder()
        resultBuilder.append(bankCardResult.number)
        resultBuilder.append("\r\n")
        resultBuilder.append(bankCardResult.organization)
        resultBuilder.append("\r\n")
        resultBuilder.append(bankCardResult.expire)
        return resultBuilder.toString()
    }

    private fun displayFailure() {
        text_result.text = getString(R.string.onfail)
    }

    private val banCallback: MLBcrCapture.Callback = object : MLBcrCapture.Callback {
        override fun onSuccess(bankCardResult: MLBcrCaptureResult) {
            val bitmap = bankCardResult.originalBitmap
            Bank_Card_image.setImageBitmap(bitmap)
            val cardResultFront = formatIdCardResult(bankCardResult)
            text_result.text = cardResultFront
        }

        override fun onCanceled() {
        }

        override fun onFailure(recCode: Int, bitmap: Bitmap) {
            displayFailure()
        }

        override fun onDenied() {
            displayFailure()
        }
    }

    private fun startCaptureActivity(Callback: MLBcrCapture.Callback) {
        val config = MLBcrCaptureConfig.Factory().setResultType(MLBcrCaptureConfig.RESULT_ALL)
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .create()
        val bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config)
        bcrCapture.captureFrame(this, Callback)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val READ_EXTERNAL_STORAGE_CODE = 200
    }
}