package com.example.draganddrop;

import android.view.View;

public class DraggableManager {
    private int mTouchViewResId;

    public DraggableManager(int touchViewResId) {
        mTouchViewResId = touchViewResId;
    }

    public boolean isDraggable(final View view, final int position, final float x, final float y) {
        View touchView = view.findViewById(mTouchViewResId);
        if (touchView != null) {
            boolean xHit = touchView.getLeft() <= x && touchView.getRight() >= x;
            boolean yHit = touchView.getTop() <= y && touchView.getBottom() >= y;
            return xHit && yHit;
        } else {
            return false;
        }
    }
    
    public int getTouchViewResId() {
        return mTouchViewResId;
    }
}
