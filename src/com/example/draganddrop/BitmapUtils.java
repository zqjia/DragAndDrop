package com.example.draganddrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;


public class BitmapUtils {
    
    /**
     * can't be instance
     * */
    private BitmapUtils() {
        
    }

    /**
     * Returns a bitmap showing a screenshot of the view passed in.
     */
    @NonNull
    static Bitmap getBitmapFromView(final View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
