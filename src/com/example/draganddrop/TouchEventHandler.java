package com.example.draganddrop;

import android.view.MotionEvent;

public interface TouchEventHandler {
    boolean onTouchEvent(MotionEvent event);
    boolean isInteracting();
}
