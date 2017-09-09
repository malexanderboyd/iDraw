package com.opensource.boyd.idraw.model

import android.graphics.Color
import android.graphics.Paint

/**
 * Created by Boyd on 9/8/2017.
 */
class Brush {

    var paint : Paint = Paint()
    var posX : Float? = null
    var posY : Float? = null

    init {
        paint?.let {
             // smooth edges
             it.isAntiAlias = true
             it.strokeJoin = Paint.Join.ROUND
             it.style = Paint.Style.STROKE

             // default color & stroke width
             it.color = Color.GREEN
             it.strokeWidth = 7f
         }
    }

}