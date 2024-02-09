/*
 * Copyright (C) 2008 ZXing authors
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

package com.thumbSlapper;

 
import android.content.Context;
import android.graphics.Canvas;
// import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
// import android.graphics.YuvImage;
import android.util.AttributeSet;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    private int _width, _height;
    private final Rect _bounds = new Rect();

    public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalTextView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // vise versa
        _height = getMeasuredWidth();
        _width = getMeasuredHeight();
        setMeasuredDimension(_width, _height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(_width, _height);
        canvas.rotate(-90);
        Paint paint = new Paint();
        paint.setColor(getTextColors().getDefaultColor());

        String text = text();
        paint.setTextSize((float)38);
	    // paint.setARGB(220, 140, 240, 240);
        // set paint color to white
        paint.setColor(Color.BLUE);
        
        paint.getTextBounds(text, 0, text.length(), _bounds);
        // draw text with yellow shadow
        // draw the text centered on the canvas

        canvas.drawText(text, getCompoundPaddingLeft() + 10, (_bounds.height() - _width) / 2, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(text, getCompoundPaddingLeft() + 11, ((_bounds.height() - _width) / 2) + 1, paint);

        canvas.restore();
    }

    private String text() {
        return super.getText().toString();
    }



}
