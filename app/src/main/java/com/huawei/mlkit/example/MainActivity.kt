package com.huawei.mlkit.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawei.mlkit.example.bankCard.BcrAnalyseActivity
import com.huawei.mlkit.example.face.LiveFaceAnalyseActivity
import com.huawei.mlkit.example.translate.TranslatorActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_face_live).setOnClickListener(this)
        findViewById<View>(R.id.btn_translate).setOnClickListener(this)
        findViewById<View>(R.id.btn_bcr).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_face_live -> this.startActivity(Intent(this@MainActivity, LiveFaceAnalyseActivity::class.java))
            R.id.btn_translate -> this.startActivity(Intent(this@MainActivity, TranslatorActivity::class.java))
            R.id.btn_bcr -> this.startActivity(Intent(this@MainActivity, BcrAnalyseActivity::class.java))
            else -> {
            }
        }
    }
}