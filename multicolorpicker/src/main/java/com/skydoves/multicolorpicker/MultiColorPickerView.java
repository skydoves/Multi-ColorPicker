
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.skydoves.multicolorpicker.listeners.ColorListener;
import com.skydoves.multicolorpicker.listeners.SelectorListener;

import java.util.ArrayList;
import java.util.List;

public class MultiColorPickerView extends FrameLayout {

    private ImageView palette;
    private Selector mainSelector;

    private Drawable paletteDrawable;

    private List<Selector> selectorList;

    private float alpha = 0.5f;

    private FlagView flagView;

    public enum FlagMode {ALWAYS, LAST, NONE}
    private FlagMode flagMode = FlagMode.ALWAYS;
    private boolean flipable = true;

    public MultiColorPickerView(Context context) {
        super(context);
    }

    public MultiColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(attrs);
        onCreate();
    }

    public MultiColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(attrs);
        onCreate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        getAttrs(attrs);
        onCreate();
    }

    private void init() {
        selectorList = new ArrayList<>();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onFirstLayout();
            }
        });
    }

    private void onFirstLayout() {
        selectCenter();
        loadListeners();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiColorPickerView);
        try {
            if (a.hasValue(R.styleable.MultiColorPickerView_palette))
                paletteDrawable = a.getDrawable(R.styleable.MultiColorPickerView_palette);
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);

        palette = new ImageView(getContext());
        if (paletteDrawable != null) {
            palette.setImageDrawable(paletteDrawable);

            LayoutParams paletteParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            paletteParams.gravity = Gravity.CENTER;
            addView(palette, paletteParams);
        }
    }

    private void loadListeners() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mainSelector != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if(flagView != null && flagMode == FlagMode.LAST) flagView.gone();
                            mainSelector.getSelector().setPressed(true);
                            return onTouchReceived(event);
                        case MotionEvent.ACTION_MOVE:
                            mainSelector.getSelector().setPressed(true);
                            return onTouchReceived(event);
                        case MotionEvent.ACTION_UP :
                            if(flagView != null && flagMode == FlagMode.LAST) flagView.visible();
                            return onTouchReceived(event);
                        default:
                            mainSelector.getSelector().setPressed(false);
                            return false;
                    }
                } else
                    return false;
            }
        });
    }

    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = new Point((int)event.getX(), (int)event.getY());
        mainSelector.setColor(getColorFromBitmap(snapPoint.x, snapPoint.y));

        if(getColor() != Color.TRANSPARENT) {
            Point centerPoint = getCenterPoint(snapPoint.x, snapPoint.y);
            mainSelector.getSelector().setX(centerPoint.x);
            mainSelector.getSelector().setY(centerPoint.y);
            mainSelector.setPoint(snapPoint.x, snapPoint.y);
            fireColorListener(getColor());

            if (flagView != null && (flagMode == FlagMode.ALWAYS || flagMode == FlagMode.LAST)) {
                if(centerPoint.y - flagView.getHeight() > 0) {
                    flagView.setRotation(0);
                    if (flagView.getVisibility() == View.GONE) flagView.visible();
                    flagView.setX(centerPoint.x - flagView.getWidth() / 2 + mainSelector.getSelector().getWidth() / 2);
                    flagView.setY(centerPoint.y - flagView.getHeight());
                    flagView.onRefresh(getColorEnvelope());
                } else if(flipable) {
                    flagView.setRotation(180);
                    if (flagView.getVisibility() == View.GONE) flagView.visible();
                    flagView.setX(centerPoint.x - flagView.getWidth() / 2 + mainSelector.getSelector().getWidth() / 2);
                    flagView.setY(centerPoint.y + flagView.getHeight() - mainSelector.getSelector().getHeight() / 2);
                    flagView.onRefresh(getColorEnvelope());
                }
            }
            return true;
        } else
            return false;
    }

    private int getColorFromBitmap(float x, float y) {
        if (paletteDrawable == null) return 0;

        Matrix invertMatrix = new Matrix();
        palette.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (palette.getDrawable() != null && palette.getDrawable() instanceof BitmapDrawable &&
                mappedPoints[0] > 0 && mappedPoints[1] > 0 &&
                mappedPoints[0] < palette.getDrawable().getIntrinsicWidth() && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

            invalidate();
            return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel((int) mappedPoints[0], (int) mappedPoints[1]);
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void fireColorListener(int color) {
        if (mainSelector.getColorListener() != null) {
            mainSelector.getColorListener().onColorSelected(getColorEnvelope());
        }
    }

    private Point getCenterPoint(int x, int y) {
       return new Point(x - (mainSelector.getSelector().getMeasuredWidth() / 2), y - (mainSelector.getSelector().getMeasuredHeight() / 2));
    }

    private int getColor() {
        return mainSelector.getColor();
    }

    private String getColorHtml() {
        return String.format("%06X", (0xFFFFFF & getColor()));
    }

    private int[] getColorRGB() {
        int[] rgb = new int[3];
        int color = (int) Long.parseLong(String.format("%06X", (0xFFFFFF & getColor())), 16);
        rgb[0] = (color >> 16) & 0xFF;
        rgb[1] = (color >> 8) & 0xFF;
        rgb[2] = (color >> 0) & 0xFF;
        return rgb;
    }

    public Selector addSelector(Drawable drawable, ColorListener colorListener) {
        if(drawable == null || colorListener == null) return null;

        final ImageView selectorImage = new ImageView(getContext());
        selectorImage.setImageDrawable(drawable);

        final Selector selector = new Selector(selectorImage, colorListener);
        LayoutParams thumbParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        thumbParams.gravity = Gravity.CENTER;

        selectorImage.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        swapAlpha(selector);
                        mainSelector = selector;

                        Matrix invertMatrix = new Matrix();
                        palette.getImageMatrix().invert(invertMatrix);

                        float[] mappedPoints = new float[]{motionEvent.getX(), motionEvent.getY()};
                        invertMatrix.mapPoints(mappedPoints);

                        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, motionEvent.getX(), motionEvent.getY(), 0);
                        dispatchTouchEvent(event);
                        break;
                }
                return false;
            }
        });

        addView(selector.getSelector(), thumbParams);
        swapAlpha(selector);
        selector.setSelectorListener(selectorListener);
        selector.onMoveCenter();
        selectorList.add(selector);
        mainSelector = selector;
        return selector;
    }

    private void swapAlpha(Selector selector) {
        if(mainSelector != null && selectorList.size() > 1) {
            mainSelector.getSelector().setAlpha(1.0f);
            selector.getSelector().setAlpha(alpha);
        }
    }

    private SelectorListener selectorListener = new SelectorListener() {
        @Override
        public void onMove(Selector selector, int x, int y) {
            selector.getSelector().setX(x);
            selector.getSelector().setY(y);
            selector.setPoint(x, y);
        }

        @Override
        public void onMoveCenter(Selector selector) {
            int centerX = getMeasuredWidth() / 2 - selector.getSelector().getWidth() / 2;
            int centerY = getMeasuredHeight() / 2- selector.getSelector().getHeight() / 2;
            selector.getSelector().setX(centerX);
            selector.getSelector().setY(centerY);
            selector.setPoint(centerX, centerY);
        }

        @Override
        public void onSelect(Selector selector) {
            selector.setColor(getColorFromBitmap(selector.getX(), selector.getY()));
            selector.getColorListener().onColorSelected(new ColorEnvelope(selector.getColor(), getColorHtml(), getColorRGB()));
        }

        @Override
        public void onSelect(Selector selector, int x, int y) {
            selector.getSelector().setX(x);
            selector.getSelector().setY(y);
            selector.setPoint(x, y);
            selector.setColor(getColorFromBitmap(x, y));
            selector.getColorListener().onColorSelected(new ColorEnvelope(selector.getColor(), getColorHtml(), getColorRGB()));
        }
    };

    private void setSelectorPoint(int x, int y) {
        if(mainSelector != null) {
            mainSelector.getSelector().setX(x);
            mainSelector.getSelector().setY(y);
            mainSelector.setPoint(x, y);
            mainSelector.setColor(getColorFromBitmap(x, y));
            fireColorListener(getColor());
        }
    }

    private void selectCenter() {
        if(mainSelector != null)
            setSelectorPoint(getMeasuredWidth() / 2 - mainSelector.getSelector().getWidth() / 2, getMeasuredHeight() / 2- mainSelector.getSelector().getHeight() / 2);
    }

    public void setSelectedAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getSelectorsSize() {
        if(selectorList != null)
            return selectorList.size();
        else
            return 0;
    }

    public void setPaletteDrawable(Drawable drawable) {
        removeAllViews();
        palette = new ImageView(getContext());
        paletteDrawable = drawable;
        palette.setImageDrawable(paletteDrawable);
        addView(palette);

        for(int i=0; i< selectorList.size(); i++) {
            if(i == 0) mainSelector = selectorList.get(i);
            addView(selectorList.get(i).getSelector());
            selectorList.get(i).onMoveCenter();
        }

        addView(flagView);
    }

    public ColorEnvelope getColorEnvelope() {
        return new ColorEnvelope(getColor(), getColorHtml(), getColorRGB());
    }

    public int getMixedColor(float ratio) {
        if(ratio > 1 || ratio < 0) return 0;
        if(selectorList.size() == 0) return 0;
        if(selectorList.size() == 1) return selectorList.get(0).getColor();
        return mixColor(selectorList.size(), selectorList.get(0).getColor(), ratio);
    }

    public void setFlagView(FlagView flagView) {
        flagView.gone();
        addView(flagView);
        this.flagView = flagView;
    }

    public void setFlagFlipable(boolean flipable) {
        this.flipable = flipable;
    }

    public void setFlagMode(FlagMode flagMode) {
        this.flagMode = flagMode;
    }

    private int mixColor(int index, int mixedColor, float ratio) {
        if(index == 1) return mixedColor;
        return mixColor(index-1, blendARGB(mixedColor, selectorList.get(index-1).getColor(), ratio), ratio);
    }

    private int blendARGB(int color1, int color2, float ratio) {
        final float inverseRatio = 1 - ratio;
        float a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio;
        float r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio;
        float g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio;
        float b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio;
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }
}