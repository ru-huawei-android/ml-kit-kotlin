package com.huawei.mlkit.example.face

import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.common.MLAnalyzer.MLTransactor
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.mlkit.example.camera.GraphicOverlay

class FaceAnalyzerTransactor internal constructor(private val mGraphicOverlay: GraphicOverlay?) : MLTransactor<MLFace?> {
    override fun transactResult(result: MLAnalyzer.Result<MLFace?>) {
        mGraphicOverlay!!.clear()
        val faceSparseArray = result.analyseList
        for (i in 0 until faceSparseArray.size()) {
            val graphic = MLFaceGraphic(mGraphicOverlay, faceSparseArray.valueAt(i))
            mGraphicOverlay.add(graphic)
        }
    }

    override fun destroy() {
        mGraphicOverlay!!.clear()
    }
}