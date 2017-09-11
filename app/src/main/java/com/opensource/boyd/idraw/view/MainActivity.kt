package com.opensource.boyd.idraw.view

import android.app.AlertDialog
import android.arch.lifecycle.LifecycleActivity
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import java.io.ByteArrayOutputStream

class MainActivity : LifecycleActivity(), ColorPickerSwatch.OnColorSelectedListener, DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, user: Boolean) {
        // can't have 0 size
        val size = if(progress <= 0) 1 else progress
        canvas.setBrushWidth(size)
        seekbar?.rootView?.brushWidthProgress?.text = size.toString()
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
        setActionBar(toolbar)
        prompt_text.text = Quotes(this).getQuote()

        setupOptionsClickListeners()

        savedInstanceState?.let {
            val bitmapBytes = it.getByteArray("bitmap")
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
            val brushWidth = it.getInt("brush_width")
            val brushColor = it.getInt("brush_color")
            canvas.drawSavedBitMap(bitmap)
            canvas.setPaintColor(brushColor)
            canvas.setBrushWidth(brushWidth)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        canvas?.let {
            // Bundle/Intent IPC only allow for 1mb, saving locally to reload on rotation.
            val bitmap = Bitmap.createBitmap(canvas.bitMap)
            val bitMapBytes = canvas.saveBitMap(bitmap)
            bitMapBytes?.let {
                val brushWidth = canvas.getCurrentBrushSize()
                val brushColor = canvas.getCurrentColor()
                outState?.putByteArray("bitmap", bitMapBytes)
                outState?.putInt("brush_width", brushWidth)
                outState?.putInt("brush_color", brushColor)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId) {
                R.id.menu_action_options -> { hideOptionsSheet()
                                              it.isChecked = !it.isChecked  }
                R.id.menu_action_quote -> { prompt_text.visibility = if(prompt_text.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
                                            it.isChecked = !it.isChecked }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun hideOptionsSheet() {
        val optionsBehavior = BottomSheetBehavior.from(options)
        optionsBehavior?.let {
            it.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        fab.visibility = if(fab.visibility == View.INVISIBLE) View.VISIBLE  else View.INVISIBLE
    }


    private fun setupOptionsClickListeners() {

        fab.setOnClickListener { _ ->
            val optionsBehavior = BottomSheetBehavior.from(options)
            optionsBehavior?.let {
                it.state = if (it.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
            }
        }

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
            toolbar.menu.getItem(0)?.isChecked = false
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
