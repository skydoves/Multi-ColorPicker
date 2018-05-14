/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.multicolorpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout

abstract class FlagView(context: Context, layout: Int) : RelativeLayout(context) {

    init {
        initializeLayout(layout)
    }

    private fun initializeLayout(layout: Int) {
        val inflated = LayoutInflater.from(context).inflate(layout, this)
        inflated.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        inflated.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        inflated.layout(0, 0, inflated.measuredWidth, inflated.measuredHeight)
    }

    fun visible() {
        visibility = View.VISIBLE
    }

    fun gone() {
        visibility = View.GONE
    }

    abstract fun onRefresh(envelope: ColorEnvelope)
}