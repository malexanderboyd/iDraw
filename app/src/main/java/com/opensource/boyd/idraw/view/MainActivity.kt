package com.opensource.boyd.idraw.view

import android.app.AlertDialog
import android.arch.lifecycle.LifecycleActivity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.SeekBar
import com.android.colorpicker.ColorPickerDialog
import com.android.colorpicker.ColorPickerSwatch
import com.opensource.boyd.idraw.R
import com.opensource.boyd.idraw.model.Quotes

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.brush_width_view.*
import kotlinx.android.synthetic.main.brush_width_view.view.*
import kotlinx.android.synthetic.main.options_bottom_sheet.view.*

class MainActivity : LifecycleActivity(), ColorPickerSwatch.OnColorSelectedListener, DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        canvas.setBrushWidth(p1)
        p0?.rootView?.brushWidthProgress?.text = p1.toString()
}

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
       when(p1) {
           -1 -> { p0?.dismiss() }
           -2 -> p0?.dismiss()
       }
    }

    override fun onColorSelected(color: Int) {
       canvas.setPaintColor(color)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prompt_text.text = Quotes(this).getQuote()

        fab.setOnClickListener { view ->
            var optionsBehavior = BottomSheetBehavior.from(options)
            optionsBehavior?.let {
                it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
            }
        }
            setupOptionsClickListeners()
    }

    private fun setupOptionsClickListeners() {
        options.modifyColorBtn?.setOnClickListener { view ->
            canvas?.let {
                val colorPicker = ColorPickerDialog()
                val allColors: IntArray = canvas.getAllColors()
                colorPicker.initialize(R.string.color_picker_title,
                        allColors,
                        canvas.getCurrentColor(),
                        5,
                        allColors.size)
                colorPicker.show(fragmentManager, "colorpicker")
                colorPicker.setOnColorSelectedListener(this)
            }
        }

        options.clearCanvasBtn.setOnClickListener { _ ->
            canvas.clearCanvas()
            prompt_text.visibility = View.INVISIBLE
        }

        options.modifyBrushWidthBtn.setOnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
                    .setTitle(R.string.brush_width_change_title)
                    .setPositiveButton(android.R.string.ok, this)
                    .setNegativeButton(android.R.string.no, this)
                    .setView(R.layout.brush_width_view)
                    .create()

            alert.show()
            alert.brushWidthSeekBar.progress = canvas.getCurrentBrushSize()
            alert.brushWidthProgress.text = canvas.getCurrentBrushSize().toString()
            alert.brushWidthSeekBar.setOnSeekBarChangeListener(this)
        }

    }


}
