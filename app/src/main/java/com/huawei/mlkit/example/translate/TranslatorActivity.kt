package com.huawei.mlkit.example.translate

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy
import com.huawei.hms.mlsdk.translate.MLTranslateLanguage
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel
import com.huawei.mlkit.example.R
import kotlinx.android.synthetic.main.activity_translator.*

class TranslatorActivity : AppCompatActivity(), View.OnClickListener {

    var translator: MLLocalTranslator? = null
    private var langDetector: MLLocalLangDetector? = null
    var sourceText: String? = null
    lateinit var langs: Array<Any>
    var i = 0
    var downloadStrategy = MLModelDownloadStrategy.Factory().needWifi().create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_translator)

        findViewById<View>(R.id.btn_translator).setOnClickListener(this)

        MLApplication.getInstance().apiKey = getString(R.string.apikey)
        sourceText = et_input.getText().toString()
        MLTranslateLanguage.getCloudAllLanguages().addOnSuccessListener { result ->
            langs = result.toTypedArray()
            i = 0
            val d = Download()
            d.start()
        }
        val setting = MLLocalLangDetectorSetting.Factory().create()
        langDetector = MLLangDetectorFactory.getInstance().getLocalLangDetector(setting)
    }

    private fun localTranslator() {
        sourceText = et_input.text.toString()
        val task = langDetector!!.probabilityDetect(sourceText)
        task.addOnSuccessListener { result ->
            val setting = MLLocalTranslateSetting.Factory().setSourceLangCode(result[0].langCode).setTargetLangCode("ru").create()
            translator = MLTranslatorFactory.getInstance().getLocalTranslator(setting)
            val task1 = translator?.asyncTranslate(sourceText)
            task1?.addOnSuccessListener { text ->
                tv_output.text = text
            }?.addOnFailureListener { }
        }.addOnFailureListener { }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_translator -> localTranslator()
            else -> {
            }
        }
    }

    internal inner class Download : Thread() {
        var langCode: String? = null
        override fun run() {
            langCode = langs[i].toString()
            val manager = MLLocalModelManager.getInstance()
            val model = MLLocalTranslatorModel.Factory(langCode).create()
            manager.downloadModel(model, downloadStrategy).addOnSuccessListener {
                tv_output.text = tv_output.text.toString() + " " + langCode + getString(R.string.modelsuccess)
                i++
                if (i < langs.size) {
                    val d = Download()
                    d.start()
                }
            }.addOnFailureListener {
                tv_output.text = tv_output.text.toString() + " " + langCode + getString(R.string.nomodel)
                i++
                if (i < langs.size) {
                    val d = Download()
                    d.start()
                }
            }
        }
    }
}