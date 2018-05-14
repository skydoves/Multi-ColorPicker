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

import android.widget.ImageView

import com.skydoves.multicolorpicker.listeners.ColorListener
import com.skydoves.multicolorpicker.listeners.SelectorListener

class Selector(val selector: ImageView, val colorListener: ColorListener) {

    var x: Int = 0
        private set
    var y: Int = 0
        private set

    var color: Int = 0
    private var selectorListener: SelectorListener? = null

    val colorHtml: String
        get() = String.format("%06X", 0xFFFFFF and color)

    val colorRGB: IntArray
        get() {
            val rgb = IntArray(3)
            val color = java.lang.Long.parseLong(String.format("%06X", 0xFFFFFF and color), 16).toInt()
            rgb[0] = color shr 16 and 0xFF
            rgb[1] = color shr 8 and 0xFF
            rgb[2] = color shr 0 and 0xFF
            return rgb
        }

    fun setPoint(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun setSelectorListener(selectorListener: SelectorListener) {
        this.selectorListener = selectorListener
    }

    fun onMove(x: Int, y: Int) {
        selectorListener!!.onMove(this, x, y)
    }

    fun onMoveCenter() {
        selectorListener!!.onMoveCenter(this)
    }

    fun onSelect() {
        selectorListener!!.onSelect(this)
    }

    fun onSelect(x: Int, y: Int) {
        selectorListener!!.onSelect(this, x, y)
    }
}