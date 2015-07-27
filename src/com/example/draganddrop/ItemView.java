package com.example.draganddrop;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;


public class ItemView extends LinearLayout {
    
    private static final String TAG = ItemView.class.getSimpleName();
    
    public ItemView(Context context) {
        super(context, null);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.INVISIBLE) {
            LogUtil.e(TAG, "setVisibility---------->");
            Exception ex = new Exception();
            ex.printStackTrace();
        }
    }

}
