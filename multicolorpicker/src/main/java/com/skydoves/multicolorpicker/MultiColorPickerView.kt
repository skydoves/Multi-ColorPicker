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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.skydoves.multicolorpicker.listeners.ColorListener
import com.skydoves.multicolorpicker.listeners.SelectorListener
import java.util.*

@Suppress("WeakerAccess", "unchecked", "unused", "deprecated")
class MultiColorPickerView : FrameLayout {

    private var palette: ImageView? = null
    private var mainSelector: Selector? = null

    private var paletteDrawable: Drawable? = null
    private var selectorList: MutableList<Selector> = ArrayList()

    private var flagView: FlagView? = null
    private var flagMode = FlagMode.ALWAYS
    private var flipable = true

    private var alpha_selector = 0.5f

    private val color: Int
        get() = mainSelector!!.color

    private val colorHtml: String
        get() = String.format("%06X", 0xFFFFFF and color)

    private val colorRGB: IntArray
        get() {
            val rgb = IntArray(3)
            val color = java.lang.Long.parseLong(String.format("%06X", 0xFFFFFF and color), 16).toInt()
            rgb[0] = color shr 16 and 0xFF
            rgb[1] = color shr 8 and 0xFF
            rgb[2] = color shr 0 and 0xFF
            return rgb
        }

    private val selectorListener = object : SelectorListener {
        override fun onMove(selector: Selector, x: Int, y: Int) {
            selector.selector.x = x.toFloat()
            selector.selector.y = y.toFloat()
            selector.setPoint(x, y)
        }

        override fun onMoveCenter(selector: Selector) {
            val centerX = measuredWidth / 2 - selector.selector.width / 2
            val centerY = measuredHeight / 2 - selector.selector.height / 2
            selector.selector.x = centerX.toFloat()
            selector.selector.y = centerY.toFloat()
            selector.setPoint(centerX, centerY)
        }

        override fun onSelect(selector: Selector) {
            selector.color = getColorFromBitmap(selector.x.toFloat(), selector.y.toFloat())
            selector.colorListener.onColorSelected(ColorEnvelope(selector.color, colorHtml, colorRGB))
        }

        override fun onSelect(selector: Selector, x: Int, y: Int) {
            selector.selector.x = x.toFloat()
            selector.selector.y = y.toFloat()
            selector.setPoint(x, y)
            selector.color = getColorFromBitmap(x.toFloat(), y.toFloat())
            selector.colorListener.onColorSelected(ColorEnvelope(selector.color, colorHtml, colorRGB))
        }
    }

    val selectorsSize: Int
        get() = selectorList.size

    val colorEnvelope: ColorEnvelope
        get() = ColorEnvelope(color, colorHtml, colorRGB)

    enum class FlagMode {
        ALWAYS, LAST, NONE
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        getAttrs(attrs)
        onCreate()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
        getAttrs(attrs)
        onCreate()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
        getAttrs(attrs)
        onCreate()
    }

    private fun init() {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                onFirstLayout()
            }
        })
    }

    private fun onFirstLayout() {
        selectCenter()
        loadListeners()
    }

    private fun getAttrs(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MultiColorPickerView)
        try {
            if (a.hasValue(R.styleable.MultiColorPickerView_palette))
                paletteDrawable = a.getDrawable(R.styleable.MultiColorPickerView_palette)
        } finally {
            a.recycle()
        }
    }

    private fun onCreate() {
        setPadding(0, 0, 0, 0)

        palette = ImageView(context)
        paletteDrawable?.let {
            palette?.setImageDrawable(it)

            val paletteParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            paletteParams.gravity = Gravity.CENTER
            addView(palette, paletteParams)
        }
    }

    private fun loadListeners() {
        setOnTouchListener { v, event ->
            mainSelector?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (flagView != null && flagMode == FlagMode.LAST) flagView?.gone()
                        it.selector.isPressed = true
                        onTouchReceived(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        it.selector.isPressed = true
                        onTouchReceived(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (flagView != null && flagMode == FlagMode.LAST) flagView?.visible()
                        onTouchReceived(event)
                    }
                    else -> {
                        it.selector.isPressed = false
                        false
                    }
                }
            } ?: false
        }
    }

    private fun onTouchReceived(event: MotionEvent): Boolean {
        val snapPoint = Point(event.x.toInt(), event.y.toInt())
        mainSelector?.color = getColorFromBitmap(snapPoint.x.toFloat(), snapPoint.y.toFloat())

        if (color != Color.TRANSPARENT) {
            val centerPoint = getCenterPoint(snapPoint.x, snapPoint.y)
            mainSelector?.let {
                it.selector.x = centerPoint.x.toFloat()
                it.selector.y = centerPoint.y.toFloat()
                it.setPoint(snapPoint.x, snapPoint.y)
                fireColorListener()

                flagView?.let {
                    if((flagMode == FlagMode.ALWAYS || flagMode == FlagMode.LAST)) {
                        if (centerPoint.y - it.height > 0) {
                            it.rotation = 0f
                            if (it.visibility == View.GONE) it.visible()
                            it.x = (centerPoint.x - it.width / 2 + mainSelector!!.selector.width / 2).toFloat()
                            it.y = (centerPoint.y - it.height).toFloat()
                            it.onRefresh(colorEnvelope)
                        } else if (flipable) {
                            it.rotation = 180f
                            if (it.visibility == View.GONE) it.visible()
                            it.x = (centerPoint.x - it.width / 2 + mainSelector!!.selector.width / 2).toFloat()
                            it.y = (centerPoint.y + it.height - mainSelector!!.selector.height / 2).toFloat()
                            it.onRefresh(colorEnvelope)
                        }
                    }
                }
            }
            return true
        } else
            return false
    }

    private fun getColorFromBitmap(x: Float, y: Float): Int {
        if (paletteDrawable == null) return 0

        val invertMatrix = Matrix()
        palette!!.imageMatrix.invert(invertMatrix)

        val mappedPoints = floatArrayOf(x, y)
        invertMatrix.mapPoints(mappedPoints)

        if (palette!!.drawable != null && palette!!.drawable is BitmapDrawable &&
                mappedPoints[0] > 0 && mappedPoints[1] > 0 &&
                mappedPoints[0] < palette!!.drawable.intrinsicWidth && mappedPoints[1] < palette!!.drawable.intrinsicHeight) {

            invalidate()

            val rect = palette!!.drawable.bounds
            val scaleX = mappedPoints[0] / rect.height()
            val x1 = (scaleX * (palette!!.drawable as BitmapDrawable).bitmap.height).toInt()
            val scaleY = mappedPoints[1] / rect.width()
            val y1 = (scaleY * (palette!!.drawable as BitmapDrawable).bitmap.width).toInt()
            return (palette!!.drawable as BitmapDrawable).bitmap.getPixel(x1, y1)
        }
        return 0
    }

    private fun fireColorListener() {
        mainSelector?.colorListener?.let {
            it.onColorSelected(colorEnvelope)
        }
    }

    private fun getCenterPoint(x: Int, y: Int): Point {
        return Point(x - mainSelector!!.selector.measuredWidth / 2, y - mainSelector!!.selector.measuredHeight / 2)
    }

    @SuppressWarnings("ClickableViewAccessibility")
    fun addSelector(drawable: Drawable?, colorListener: ColorListener?): Selector? {
        if (drawable == null || colorListener == null) return null

        val selectorImage = ImageView(context)
        selectorImage.setImageDrawable(drawable)

        val selector = Selector(selectorImage, colorListener)
        val thumbParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        thumbParams.gravity = Gravity.CENTER

        selectorImage.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    swapAlpha(selector)
                    mainSelector = selector

                    val invertMatrix = Matrix()
                    palette!!.imageMatrix.invert(invertMatrix)

                    val mappedPoints = floatArrayOf(motionEvent.x, motionEvent.y)
                    invertMatrix.mapPoints(mappedPoints)

                    val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, motionEvent.x, motionEvent.y, 0)
                    dispatchTouchEvent(event)
                }
            }
            false
        }

        addView(selector.selector, thumbParams)
        swapAlpha(selector)
        selector.setSelectorListener(selectorListener)
        selector.onMoveCenter()
        selectorList.add(selector)
        mainSelector = selector
        return selector
    }

    private fun swapAlpha(selector: Selector) {
        if (mainSelector != null && selectorList.size > 1) {
            mainSelector!!.selector.alpha = 1.0f
            selector.selector.alpha = alpha_selector
        }
    }

    private fun setSelectorPoint(x: Int, y: Int) {
        mainSelector?.let {
            it.selector.x = x.toFloat()
            it.selector.y = y.toFloat()
            it.setPoint(x, y)
            it.color = getColorFromBitmap(x.toFloat(), y.toFloat())
            fireColorListener()
        }
    }

    private fun selectCenter() {
        mainSelector?.let {
            setSelectorPoint(measuredWidth / 2 - mainSelector!!.selector.width / 2, measuredHeight / 2 - mainSelector!!.selector.height / 2)
        }
    }

    fun setSelectedAlpha(alpha: Float) {
        this.alpha_selector = alpha
    }

    fun setPaletteDrawable(drawable: Drawable) {
        removeAllViews()
        palette = ImageView(context)
        paletteDrawable = drawable
        palette?.setImageDrawable(paletteDrawable)
        addView(palette)

        for (i in selectorList.indices) {
            if (i == 0) mainSelector = selectorList[i]
            addView(selectorList[i].selector)
            selectorList[i].onMoveCenter()
        }

        flagView?.let { addView(flagView) }
    }

    fun getMixedColor(ratio: Float): Int {
        if (ratio > 1 || ratio < 0) return 0
        if (selectorList.size == 0) return 0
        return if (selectorList.size == 1) selectorList[0].color else mixColor(selectorList.size, selectorList[0].color, ratio)
    }

    fun setFlagView(flagView: FlagView) {
        flagView.gone()
        addView(flagView)
        this.flagView = flagView
    }

    fun setFlagFlipable(flipable: Boolean) {
        this.flipable = flipable
    }

    fun setFlagMode(flagMode: FlagMode) {
        this.flagMode = flagMode
    }

    private fun mixColor(index: Int, mixedColor: Int, ratio: Float): Int {
        return when(index == 1) {
            true -> mixedColor
            false -> mixColor(index - 1, blendARGB(mixedColor, selectorList[index - 1].color, ratio), ratio)
        }
    }

    private fun blendARGB(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}