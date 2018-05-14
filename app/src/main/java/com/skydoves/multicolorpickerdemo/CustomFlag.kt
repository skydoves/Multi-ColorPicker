package com.skydoves.multicolorpickerdemo

import android.content.Context
import android.view.View
import android.widget.TextView

import com.skydoves.multicolorpicker.ColorEnvelope
import com.skydoves.multicolorpicker.FlagView

/**
 * Developed by skydoves on 2017-10-24.
 * Copyright (c) 2017 skydoves rights reserved.
 */

class CustomFlag(context: Context, layout: Int) : FlagView(context, layout) {

    private val textView: TextView
    private val view: View

    init {
        this.textView = findViewById(R.id.flag_color_code)
        this.view = findViewById(R.id.flag_color_layout)
    }

    override fun onRefresh(envelope: ColorEnvelope) {
        this.textView.text = "#" + envelope.htmlCode
        this.view.setBackgroundColor(envelope.color)
    }
}
