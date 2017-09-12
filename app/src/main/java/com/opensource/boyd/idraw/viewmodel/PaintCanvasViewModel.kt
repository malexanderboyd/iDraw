package com.opensource.boyd.idraw.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.content.ContextCompat
import com.opensource.boyd.idraw.R
import com.opensource.boyd.idraw.model.Brush
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

/**
 * Created by Boyd on 9/8/2017.
 */
class PaintCanvasViewModel : ViewModel() {

    private var brush : Brush = Brush()
    private var brushPath : Path = Path()
    private var TOUCH_TOLERENCE = 4

    fun  startPainting(posX: Float, posY: Float) {
        brushPath.reset()
        brushPath.moveTo(posX, posY)
        brush.posX = posX
        brush.posY = posY
    }

    fun moveBrush(posX: Float, posY: Float) {
        val currX = brush.posX ?: 0f
        val currY = brush.posY ?: 0f
        val dimenX = Math.abs(posX - currX)
        val dimenY = Math.abs(posY - currY)
        if(dimenX >= TOUCH_TOLERENCE || dimenY >= TOUCH_TOLERENCE) {
            brushPath.quadTo(currX, currY, (posX + currX)/2, (posY + currY)/2)
            brush.posX = posX
            brush.posY = posY
        }
    }

    fun liftBrush() : Path {
        val endX = brush.posX ?: 0f
        val endY = brush.posY ?: 0f
        brushPath.lineTo(endX, endY)
        return brushPath
    }

    fun resetBrushPath() {
        brushPath.reset()
    }

    fun getBrushPath() : Path {
        return brushPath
    }

    fun getPaint() : Paint {
        return brush.paint
    }

    fun  setPaintColor(color: Int) {
        brush.paint.color = color
    }

    fun  getAllColors(context : Context): Array<Int> {
        return arrayOf(
                ContextCompat.getColor(context,R.color.red),
                ContextCompat.getColor(context,R.color.pink),
                ContextCompat.getColor(context,R.color.purple),
                ContextCompat.getColor(context,R.color.deep_purple),
                ContextCompat.getColor(context,R.color.indigo),
                ContextCompat.getColor(context,R.color.blue),
                ContextCompat.getColor(context,R.color.light_blue),
                ContextCompat.getColor(context,R.color.cyan),
                ContextCompat.getColor(context,R.color.teal),
                ContextCompat.getColor(context,R.color.green),
                ContextCompat.getColor(context,R.color.light_green),
                ContextCompat.getColor(context,R.color.lime),
                ContextCompat.getColor(context,R.color.yellow),
                ContextCompat.getColor(context,R.color.amber),
                ContextCompat.getColor(context,R.color.orange),
                ContextCompat.getColor(context,R.color.deep_orange),
                ContextCompat.getColor(context,R.color.brown),
                ContextCompat.getColor(context,R.color.white),
                ContextCompat.getColor(context,R.color.blue_grey),
                ContextCompat.getColor(context,R.color.black)
        )


    }

    fun  setBrushWidth(width: Float) {
        brush.paint.strokeWidth = width
    }

    fun getBrushWidth() : Float {
        return brush.paint.strokeWidth
    }

    fun  bitMapToByteArray(bitmap: Bitmap?): ByteArray? {
        bitmap?.let {
            val byteStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
            return byteStream.toByteArray()
        }
        return null
    }

}