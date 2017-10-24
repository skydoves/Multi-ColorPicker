package com.skydoves.multicolorpickerdemo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.skydoves.multicolorpicker.ColorEnvelope;
import com.skydoves.multicolorpicker.FlagView;

/**
 * Developed by skydoves on 2017-10-24.
 * Copyright (c) 2017 skydoves rights reserved.
 */

public class CustomFlag extends FlagView {

    private TextView textView;
    private View view;

    public CustomFlag(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        view = findViewById(R.id.flag_color_layout);
    }

    @Override
    public void onRefresh(ColorEnvelope envelope) {
        textView.setText("#" + envelope.getHtmlCode());
        view.setBackgroundColor(envelope.getColor());
    }
}
