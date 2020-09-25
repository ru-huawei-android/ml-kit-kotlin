package com.huawei.mlkit.example.camera

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.huawei.hms.mlsdk.common.LensEngine
import java.io.IOException

class LensEnginePreview(private val mContext: Context, attrs: AttributeSet?) : ViewGroup(mContext, attrs) {

    private var mSurfaceView: SurfaceView
    private var mStartRequested = false
    private var mSurfaceAvailable = false
    private lateinit var mLensEngine: LensEngine
    private lateinit var mOverlay: GraphicOverlay

    @Throws(IOException::class)
    fun start(lensEngine: LensEngine) {
        mLensEngine = lensEngine
        mStartRequested = true
        startIfReady()
    }

    @Throws(IOException::class)
    fun start(lensEngine: LensEngine, overlay: GraphicOverlay) {
        mOverlay = overlay
        this.start(lensEngine)
    }

    fun stop() {
        mLensEngine.close()
    }

    @Throws(IOException::class)
    private fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mLensEngine.run(mSurfaceView.holder)
            val size = mLensEngine.displayDimension
            val min = Math.min(size.width, size.height)
            val max = Math.max(size.width, size.height)
            if (isPortraitMode) {
                mOverlay.setCameraInfo(min, max, mLensEngine.lensType)
            } else {
                mOverlay.setCameraInfo(max, min, mLensEngine.lensType)
            }
            mOverlay.clear()
            mStartRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            mSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = 320
        var previewHeight = 240
        val size = mLensEngine.displayDimension
        if (size != null) {
            previewWidth = size.width
            previewHeight = size.height
        }

        if (isPortraitMode) {
            val tmp = previewWidth
            previewWidth = previewHeight
            previewHeight = tmp
        }
        val viewWidth = right - left
        val viewHeight = bottom - top
        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }
        for (i in 0 until this.childCount) {
            getChildAt(i).layout(-1 * childXOffset, -1 * childYOffset, childWidth - childXOffset,
                    childHeight - childYOffset)
        }
        try {
            startIfReady()
        } catch (e: IOException) {
        }
    }

    private val isPortraitMode: Boolean
        get() {
            val orientation = mContext.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            return false
        }

    init {
        mSurfaceView = SurfaceView(mContext)
        mSurfaceView.holder.addCallback(SurfaceCallback())
        this.addView(mSurfaceView)
    }
}