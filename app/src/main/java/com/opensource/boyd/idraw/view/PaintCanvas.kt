package com.opensource.boyd.idraw.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.opensource.boyd.idraw.viewmodel.PaintCanvasViewModel

/**
 * Created by Boyd on 9/8/2017.
 */
class PaintCanvas(context : Context, attributes : AttributeSet) : View(context, attributes) {

    var canvas : Canvas = Canvas()
    var drawPath : Path = Path()
    var bitMap : Bitmap? = null
    var bitMapPaint : Paint = Paint(Paint.DITHER_FLAG)
    var viewModel : PaintCanvasViewModel = PaintCanvasViewModel()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(bitMap == null) {
            // each pixel is 4 bytes
            bitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitMap)
        } else {
            bitMap = Bitmap.createScaledBitmap(bitMap, w, h, true)
            canvas = Canvas(bitMap)
            canvas.drawBitmap(bitMap, 0f, 0f, bitMapPaint)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            super.onDraw(canvas)
            canvas.drawBitmap(bitMap, 0f, 0f, bitMapPaint)
            canvas.drawPath(viewModel.getBrushPath(), viewModel.getPaint())
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            super.onTouchEvent(event)
            var posX = event.x
            var posY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.startPainting(posX, posY)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    canvas.drawPath(viewModel.liftBrush(), viewModel.getPaint())
                    viewModel.resetBrushPath()
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    viewModel.moveBrush(posX, posY)
                    invalidate()
                }
                else -> {
                    // extra gesture
                }
            }
        }
        return true
    }

    fun drawSavedBitMap(bitmap : Bitmap) {
       bitMap = bitmap
    }


    fun  setPaintColor(color: Int) {
        viewModel.setPaintColor(color)
    }

    fun getAllColors() : IntArray {
        return viewModel.getAllColors(this.context).toIntArray()
    }

    fun getCurrentColor() : Int {
        return viewModel.getPaint().color
    }

    fun clearCanvas() {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun  setBrushWidth(width: Float) {
        viewModel.setBrushWidth(width)
    }

    fun  getCurrentBrushSize(): Float {
        return viewModel.getBrushWidth()
    }

    fun  saveBitMap(bitmap: Bitmap?) : ByteArray? {
       return viewModel.bitMapToByteArray(bitmap)
    }

    fun  getPaint(): Paint? {
        return viewModel.getPaint()
    }


}