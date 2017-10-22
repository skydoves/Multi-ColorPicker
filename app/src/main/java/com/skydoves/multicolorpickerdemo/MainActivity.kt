package com.skydoves.multicolorpickerdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by skydoves on 2017-10-21.
 * Copyright (c) 2017 skydoves rights reserved.
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        example0.setOnClickListener { startActivity(Intent(this, ExampleMultiColorPickerView::class.java)) }
        example1.setOnClickListener { startActivity(Intent(this, ExampleColorMixing::class.java)) }
    }
}