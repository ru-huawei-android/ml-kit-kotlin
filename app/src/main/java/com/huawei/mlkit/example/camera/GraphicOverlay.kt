package com.huawei.mlkit.example.camera

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.huawei.hms.mlsdk.common.LensEngine
import java.util.*

class GraphicOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mPreviewWidth = 0
    private var mWidthScaleFactor = 1.0f
    private var mPreviewHeight = 0
    private var mHeightScaleFactor = 1.0f
    private var mFacing = LensEngine.BACK_LENS

    abstract class Graphic(private val mOverlay: GraphicOverlay?) {
        abstract fun draw(canvas: Canvas)
        fun scaleX(horizontal: Float): Float {
            return horizontal * mOverlay!!.mWidthScaleFactor
        }

        fun scaleY(vertical: Float): Float {
            return vertical * mOverlay!!.mHeightScaleFactor
        }

        fun translateX(x: Float): Float {
            return if (mOverlay!!.mFacing == LensEngine.FRONT_LENS) {
                mOverlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        fun translateY(y: Float): Float {
            return scaleY(y)
        }
    }

    fun clear() {
        synchronized(mLock) { mGraphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(mLock) { mGraphics.add(graphic) }
        postInvalidate()
    }

    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(mLock) {
            mPreviewWidth = previewWidth
            mPreviewHeight = previewHeight
            mFacing = facing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(mLock) {
            if (mPreviewWidth != 0 && mPreviewHeight != 0) {
                mWidthScaleFactor = width.toFloat() / mPreviewWidth.toFloat()
                mHeightScaleFactor = height.toFloat() / mPreviewHeight.toFloat()
            }
            for (graphic in mGraphics) {
                graphic.draw(canvas)
            }
        }
    }

    companion object {
        private val mGraphics: MutableSet<Graphic> = HashSet()
        private val mLock = Any()
    }
}