package com.skydoves.multicolorpickerdemo

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.skydoves.multicolorpicker.ColorEnvelope
import com.skydoves.multicolorpicker.MultiColorPickerView
import com.skydoves.multicolorpicker.listeners.ColorListener
import kotlinx.android.synthetic.main.activity_multi_color_picker_view_example.*

/**
 * Created by skydoves on 2017-10-21.
 * Copyright (c) 2017 skydoves rights reserved.
 */

class ExampleMultiColorPickerView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_color_picker_view_example)

        multiColorPickerView.setSelectedAlpha(0.6f)
        multiColorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.drawable.watercolor1))
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector0_colorListener)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector1_colorListener)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector2_colorListener)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector3_colorListener)
        multiColorPickerView.setFlagView(CustomFlag(this, R.layout.layout_flag))
    }

    private val selector0_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            textView0.text = "#${envelope.htmlCode}"
            linearLayout0.setBackgroundColor(envelope.color)
        }
    }

    private val selector1_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            textView1.text = "#${envelope.htmlCode}"
            linearLayout1.setBackgroundColor(envelope.color)
        }
    }

    private val selector2_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            textView2.text = "#${envelope.htmlCode}"
            linearLayout2.setBackgroundColor(envelope.color)
        }
    }

    private val selector3_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            textView3.text = "#${envelope.htmlCode}"
            linearLayout3.setBackgroundColor(envelope.color)
        }
    }
}