
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

package com.skydoves.multicolorpicker;

import android.widget.ImageView;

import com.skydoves.multicolorpicker.listeners.ColorListener;
import com.skydoves.multicolorpicker.listeners.SelectorListener;

public class Selector {

    private int x;
    private int y;

    private ImageView selector;

    private int color;

    private ColorListener colorListener;
    private SelectorListener selectorListener;

    public Selector(ImageView selector, ColorListener colorListener) {
        this.selector = selector;
        this.colorListener = colorListener;
    }

    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSelectorListener(SelectorListener selectorListener) {
        this.selectorListener = selectorListener;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public ImageView getSelector() {
        return selector;
    }

    public ColorListener getColorListener() {
        return colorListener;
    }

    public int getColor() {
        return this.color;
    }

    public String getColorHtml() {
        return String.format("%06X", (0xFFFFFF & getColor()));
    }

    public int[] getColorRGB() {
        int[] rgb = new int[3];
        int color = (int) Long.parseLong(String.format("%06X", (0xFFFFFF & getColor())), 16);
        rgb[0] = (color >> 16) & 0xFF;
        rgb[1] = (color >> 8) & 0xFF;
        rgb[2] = (color >> 0) & 0xFF;
        return rgb;
    }

    public void onMove(int x, int y) {
        selectorListener.onMove(this, x, y);
    }

    public void onMoveCenter() {
        selectorListener.onMoveCenter(this);
    }

    public void onSelect() {
        selectorListener.onSelect(this);
    }

    public void onSelect(int x, int y) {
        selectorListener.onSelect(this, x, y);
    }
}