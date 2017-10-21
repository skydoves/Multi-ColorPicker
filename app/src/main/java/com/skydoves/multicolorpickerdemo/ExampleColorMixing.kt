package com.skydoves.multicolorpickerdemo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.skydoves.multicolorpicker.ColorEnvelope
import com.skydoves.multicolorpicker.listeners.ColorListener
import kotlinx.android.synthetic.main.activity_example_color_mixing.*
import java.io.FileNotFoundException


class ExampleColorMixing : AppCompatActivity() {

    var flag: Boolean = true
    val RESULT_LOAD_IMG = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_color_mixing)

        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector0_colorListener)
        multiColorPickerView.addSelector(ContextCompat.getDrawable(this, R.drawable.wheel), selector1_colorListener)
        palette.setOnClickListener {
            if(flag) {
                flag = false
                multiColorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.drawable.watercolor))
            } else {
                flag = true
                multiColorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this, R.drawable.palette))
            }}
        gallery.setOnClickListener {
            var  photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG) }
        mixing.setOnClickListener { mixed.setBackgroundColor(multiColorPickerView.getMixedColor(0.6f)) }
    }

    private val selector0_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            color1.setBackgroundColor(envelope.color)
        }
    }

    private val selector1_colorListener = object : ColorListener {
        override fun onColorSelected(envelope: ColorEnvelope) {
            color2.setBackgroundColor(envelope.color)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val drawable = BitmapDrawable(resources, selectedImage)
                multiColorPickerView.setPaletteDrawable(drawable)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}
