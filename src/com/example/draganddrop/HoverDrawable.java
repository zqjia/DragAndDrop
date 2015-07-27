package com.example.draganddrop;

import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

/**
 * A Drawable which represents a dragging {@link View}.
 */
public class HoverDrawable extends BitmapDrawable{

    private static final String TAG = HoverDrawable.class.getSimpleName();
    
    /**
     * The original y coordinate of the top of given {@code View}.
     * it will be changed while the {@code ListView} is scrolling
     */
    private float mOriginalY;

    /**
     * The original y coordinate of the position that was touched.
     */
    private float mDownY;

    /**
     * The distance the {@code ListView} has been scrolling while this {@code HoverDrawable} is alive.
     */
    private float mScrollDistance;

    /**
     * Creates a new {@code HoverDrawable} for given {@link View}, using given {@link MotionEvent}.
     *
     * @param view the {@code View} to represent.
     * @param ev   the {@code MotionEvent} to use as down position.
     */
    HoverDrawable(final View view, final MotionEvent ev) {
        this(view, ev.getY());
    }

    /**
     * Creates a new {@code HoverDrawable} for given {@link View}, using given {@link MotionEvent}.
     *
     * @param view  the {@code View} to represent.
     * @param downY the y coordinate of the down event.
     */
    HoverDrawable(final View view, final float downY) {
        super(view.getResources(), BitmapUtils.getBitmapFromView(view));
        mOriginalY = view.getTop();
        mDownY = downY;

        setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    /**
     * Calculates the new position for this {@code HoverDrawable} using given {@link MotionEvent}.
     *
     * @param ev the {@code MotionEvent}.
     *           {@code ev.getActionMasked()} should typically equal {@link MotionEvent#ACTION_MOVE}.
     */
    void handleMoveEvent(final MotionEvent ev) {
        /* when the DynamicListView is not scroll, the ev.getY() is changed */
        /* and dragging the item to the top or the bottom, the ev.getY() change little and now the mScrollDistance change */
        
        //(mOriginY - mDownY)则没有变化
        
//        LogUtil.e(TAG, "event y is " + ev.getY() + "\nmOriginalY - mDownY is " + (mOriginalY - mDownY) 
//                + "\n"
//                + "mScrollDistance is " + mScrollDistance); 
        
        //FIXME
//        int top = (int) (mOriginalY - mDownY + ev.getY() + mScrollDistance);
        int top = (int) (mOriginalY - mDownY + ev.getY() + mScrollDistance);        
        setTop(top);
    }

    /**
     * Updates the original y position of the view, and calculates the scroll distance.
     *
     * @param mobileViewTopY the top y coordinate of the mobile view this {@code HoverDrawable} represents.
     */
    void onScroll(final float mobileViewTopY) {
        mScrollDistance += mOriginalY - mobileViewTopY;
        mOriginalY = mobileViewTopY;
        LogUtil.d(TAG, "on scroll----------> mOriginalY is " + mOriginalY
                + " and scroll distance is " + mScrollDistance);
        LogUtil.e(TAG, "onScroll-------->mobileViewTopY is " + mobileViewTopY);
    }

    /**
     * Returns whether the user is currently dragging this {@code HoverDrawable} upwards.
     *
     * @return true if dragging upwards.
     */
    boolean isMovingUpwards() {
        //mOriginalY是在onScroll中动态改变，即在ListView的onScrollListener中调用改变
        //setBounds()则是在handleMoveEvent中改变，是在AbsListView中的onTouchEvent中调用
        
        //因为ListView的onScrollListener需要在super.onTouchEvent中才调用，所以setBounds()会先调用
        //如果getBounds().top变小了，此时mOriginalY是不变的，那么就是向上移动了
        return mOriginalY > getBounds().top;
    }

    /**
     * Returns the number of pixels between the original y coordinate of the view, and the current y coordinate.
     * A negative value means this {@code HoverDrawable} is moving upwards.
     *
     * @return the number of pixels.
     */
    int getDeltaY() {
//        LogUtil.e(TAG, "top is " + getBounds().top + " and mOriginalY is " + mOriginalY);
        return (int) (getBounds().top - mOriginalY);
    }

    /**
     * Returns the top coordinate of this {@code HoverDrawable}.
     */
    int getTop() {
        return getBounds().top;
    }

    /**
     * Sets the top coordinate of this {@code HoverDrawable}.
     */
    void setTop(final int top) {
        setBounds(getBounds().left, top, getBounds().left + getIntrinsicWidth(), top + getIntrinsicHeight());
//        LogUtil.e(TAG, "setTop-------------> the bounds top is " + getBounds().top);
    }

    /**
     * Shifts the original y coordinates of this {@code HoverDrawable} {code height} pixels upwards or downwards,
     * depending on the move direction.
     *
     * @param height the number of pixels this {@code HoverDrawable} should be moved. Should be positive.
     */
    void shift(final int height) {
        int shiftSize = isMovingUpwards() ? -height : height;
        mOriginalY += shiftSize;
        mDownY += shiftSize;
    }
}
