package com.skydoves.multicolorpickerdemo

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), colorListener0)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), colorListener1)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), colorListener2)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), colorListener3)
        multiColorPickerView.setFlagView(CustomFlag(this, R.layout.layout_flag))
    }

    private val colorListener0 = ColorListener { envelope ->
        textView0.text = "#${envelope.htmlCode}"
        linearLayout0.setBackgroundColor(envelope.color)
    }

    private val colorListener1 = ColorListener { envelope ->
        textView1.text = "#${envelope.htmlCode}"
        linearLayout1.setBackgroundColor(envelope.color)
    }

    private val colorListener2 = ColorListener { envelope ->
        textView2.text = "#${envelope.htmlCode}"
        linearLayout2.setBackgroundColor(envelope.color)
    }

    private val colorListener3 = ColorListener { envelope ->
        textView3.text = "#${envelope.htmlCode}"
        linearLayout3.setBackgroundColor(envelope.color)
    }
}