package com.opensource.boyd.idraw.view

import android.app.AlertDialog
import android.arch.lifecycle.LifecycleActivity
import android.content.DialogInterface
import android.graphics.*
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

class MainActivity : LifecycleActivity(), ColorPickerSwatch.OnColorSelectedListener, SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, user: Boolean) {
        // can't have 0 size
        val size = if (progress <= 0) 1f else progress.toFloat()
        seekbar?.rootView?.brushWidthProgress?.text = size.toString()
        drawBrushWidthCircle(size, seekbar?.rootView)
        this.canvas.setBrushWidth(size)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    override fun onColorSelected(color: Int) {
        canvas.setPaintColor(color)
    }


    private var quoteEnabled: Boolean? = null
    private var optionsEnabled: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(toolbar)
        prompt_text.text = Quotes(this).getQuote()

        setupOptionsClickListeners()

        savedInstanceState?.let {
            val bitmapBytes = it.getByteArray("bitmap")
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
            val brushWidth = it.getFloat("brush_width")
            val brushColor = it.getInt("brush_color")
            quoteEnabled = it.getBoolean("quote_enabled")
            quoteEnabled?.let {
                prompt_text.visibility = if (it) View.VISIBLE else View.INVISIBLE
            }
            optionsEnabled = it.getBoolean("options_enabled")
            optionsEnabled?.let {
                if (!it)
                    hideOptionsSheet()
            }

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
                // not cleanest way to retrieve menu items, but quick&dirty
                val quoteEnabled = toolbar.menu.getItem(0).isChecked
                val optionsEnabled = toolbar.menu.getItem(1).isChecked
                outState?.putByteArray("bitmap", bitMapBytes)
                outState?.putFloat("brush_width", brushWidth)
                outState?.putInt("brush_color", brushColor)
                outState?.putBoolean("quote_enabled", quoteEnabled)
                outState?.putBoolean("options_enabled", optionsEnabled)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.let {
            val menuInflater = menuInflater
            menuInflater.inflate(R.menu.menu_main, it)
            // restore options/quote if needed
            quoteEnabled?.let {
                menu.getItem(0).isChecked = it
            }
            optionsEnabled?.let {
                menu.getItem(1).isChecked = it
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.menu_action_options -> {
                    hideOptionsSheet()
                    it.isChecked = !it.isChecked
                }
                R.id.menu_action_quote -> {
                    prompt_text.visibility = if (prompt_text.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
                    it.isChecked = !it.isChecked
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun drawBrushWidthCircle(size: Float, view: View?) {
        view?.let {
            val preview = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
            val tempCanvas = Canvas(preview)
            val tempPaint = Paint()
            tempPaint.style = Paint.Style.FILL
            tempPaint.color = canvas.getCurrentColor()
            tempCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
            tempCanvas.drawCircle(75f, 75f, size, tempPaint)
            view.brush_width_circle?.setImageBitmap(preview)
        }
    }

    private fun drawBrushWidthCircle(size: Float, dialog: AlertDialog) {
        val preview = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(preview)
        val tempPaint = Paint()
        tempPaint.style = Paint.Style.FILL
        tempPaint.color = canvas.getCurrentColor()
        tempCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        tempCanvas.drawCircle(75f, 75f, size, tempPaint)
        dialog.brush_width_circle?.setImageBitmap(preview)
    }

    private fun hideOptionsSheet() {
        val optionsBehavior = BottomSheetBehavior.from(options)
        optionsBehavior?.let {
            it.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        fab.visibility = if (fab.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
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
            val alert = AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_clear_canvas)
                    .setPositiveButton(android.R.string.yes) {
                    dialog, whichButton ->
                        canvas.clearCanvas()
                        prompt_text.visibility = View.INVISIBLE
                        toolbar.menu.getItem(0)?.isChecked = false
                    }
                    .setNegativeButton(android.R.string.no) {
                     dialog, whichButton -> dialog.dismiss()
                    }
                    .setMessage(R.string.dialog_are_you_sure)
                    .create()
            alert.show()
        }

        options.modifyBrushWidthBtn.setOnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
                    .setTitle(R.string.brush_width_change_title)
                    .setView(R.layout.brush_width_view)
                    .create()
            alert.show()
            alert.brushWidthSeekBar.progress = canvas.getCurrentBrushSize().toInt()
            alert.brushWidthProgress.text = canvas.getCurrentBrushSize().toString()
            alert.brushWidthSeekBar.setOnSeekBarChangeListener(this)
            drawBrushWidthCircle(canvas.getCurrentBrushSize(), alert)
        }

    }


}
