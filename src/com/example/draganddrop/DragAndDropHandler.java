package com.example.draganddrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;


public class DragAndDropHandler implements TouchEventHandler{

    private static final String TAG = DragAndDropHandler.class.getSimpleName();
    
    private static final int INVALID_ID = -1;
    
    /**
     * decided if an item can be dragged.
     * */
    private DraggableManager mDraggableManager;
    
    private DragAndDropListViewWrapper mDragAndDropListViewWrapper;
    
    /**
     * handles scrolling when dragging an item.
     * */
    private ScrollHandler mScrollHandler;
    
    /**
     * animate the switch view
     * */
    private SwitchViewAnimator mSwitchViewAnimator;
    
    /**
     * The minimum distance in pixels that should be moved before starting vertical item movement.
     */
    private int mSlop;
    
    /**
     * the adapter the DynamicListView associated
     * */
    private ListAdapter mAdapter;
    
    /**
     * The Drawable that is drawn when the user is dragging an item.
     * This value is null if and only if the user is not dragging.
     */
    private HoverDrawable mHoverDrawable;
    
    /**
     * The View that is represented by {@link #mHoverDrawable}.
     * When this value is not null, the View should be invisible.
     * This value is null if and only if the user is not dragging.
     */
    @Nullable
    private View mMobileView;

    /**
     * The id of the item view that is being dragged.
     * This value is {@value #INVALID_ID} if and only if the user is not dragging.
     */
    private long mMobileItemId;

    /**
     * The y coordinate of the last non-final {@code MotionEvent}.
     */
    private float mLastMotionEventY = -1;

    /**
     * The original position of the view that is being dragged.
     * This value is {@value android.widget.AdapterView#INVALID_POSITION} if and only if the user is not dragging.
     */
    private int mOriginalMobileItemPosition = AdapterView.INVALID_POSITION;
    
    /**
     * The raw x coordinate of the down event.
     */
    private float mDownX;

    /**
     * The raw y coordinate of the down event.
     */
    private float mDownY;

    /**
     * Specifies whether or not the hover drawable is currently being animated as result of an up / cancel event.
     */
    private boolean mIsSettlingHoverDrawable;
    
    public DragAndDropHandler(DynamicListView dynamicListView) {
        this(new DynamicListViewWrapper(dynamicListView));
    }
    
    public DragAndDropHandler(DragAndDropListViewWrapper dragAndDropWrapper) {
        mDragAndDropListViewWrapper = dragAndDropWrapper;
        if (mDragAndDropListViewWrapper.getAdapter() != null) {
            setAdapter(mDragAndDropListViewWrapper.getAdapter());
        }
        
        mScrollHandler = new ScrollHandler();
        mDragAndDropListViewWrapper.setDynamicOnScrollListener(mScrollHandler);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mSwitchViewAnimator = new KitKatSwitchViewAnimator();
        } else {
            mSwitchViewAnimator = new LSwitchViewAnimator();
        }

        mMobileItemId = INVALID_ID;
        ViewConfiguration vc = ViewConfiguration.get(mDragAndDropListViewWrapper.getListView().getContext());
        mSlop = vc.getScaledTouchSlop();
    }
    
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof Swappable)) {
            LogUtil.e(TAG, "the adapter have not implement the Swappable interface");
            return ;
        }
        mAdapter = adapter;
    }
    
    /**
     * Sets the scroll speed when dragging an item. Defaults to {@code 1.0f}.
     *
     * @param speed {@code <1.0f} to slow down scrolling, {@code >1.0f} to speed up scrolling.
     */
    public void setScrollSpeed(final float speed) {
        mScrollHandler.setScrollSpeed(speed);
    }
    
    /**
     * Starts dragging the item at given position. User must be touching this {@code DynamicListView}.
     *
     * @param position the position of the item in the adapter to start dragging.
     *
     * @throws java.lang.IllegalStateException if the user is not touching this {@code DynamicListView},
     *                                         or if there is no adapter set.
     */
    public void startDragging(final int position) {
        if (mMobileItemId != INVALID_ID) {
            /* We are already dragging */
            return;
        }

        if (mLastMotionEventY < 0) {
            throw new IllegalStateException("User must be touching the DynamicListView!");
        }

        if (mAdapter == null) {
            throw new IllegalStateException("This DynamicListView has no adapter set!");
        }

        if (position < 0 || position >= mAdapter.getCount()) {
            /* Out of bounds */
            return;
        }

        mMobileView = mDragAndDropListViewWrapper.getChildAt(position - mDragAndDropListViewWrapper.getFirstVisiblePosition() + mDragAndDropListViewWrapper.getHeaderViewsCount());
        if (mMobileView != null) {
            mOriginalMobileItemPosition = position;
            mMobileItemId = mAdapter.getItemId(position);
            mHoverDrawable = new HoverDrawable(mMobileView, mLastMotionEventY);
            mMobileView.setVisibility(View.INVISIBLE);
        } else {
            LogUtil.e(TAG, "mobile view is null");
        }
        
        
    }
    
    /**
     * Sets the {@link DraggableManager} to be used for determining whether an item should be dragged when the user issues a down {@code MotionEvent}.
     */
    public void setDraggableManager(final DraggableManager draggableManager) {
        mDraggableManager = draggableManager;
    }
    
    public DraggableManager getDraggableManager() {
        return mDraggableManager;
    }
    
    /**
     * indicate if we are dragging
     * */
    public boolean isInteracting() {
        return mMobileItemId != INVALID_ID;
    }
    
    /**
     * Dispatches the {@link android.view.MotionEvent}s to their proper methods if applicable.
     *
     * @param event the {@code MotionEvent}.
     *
     * @return {@code true} if the event was handled, {@code false} otherwise.
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        /* We are in the process of animating the hover drawable back, do not start a new drag yet. */
        if (!mIsSettlingHoverDrawable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionEventY = event.getY();
                    handled = handleDownEvent(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mLastMotionEventY = event.getY();
                    handled = handleMoveEvent(event);
                    break;
                case MotionEvent.ACTION_UP:
                    handled = handleUpEvent();
                    mLastMotionEventY = -1;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    handled = handleCancelEvent();
                    mLastMotionEventY = -1;
                    break;
                default:
                    handled = false;
                    break;
            }
        }
        return handled;
    }
    
    /**
     * Handles the down event.
     * <p/>
     * Finds the position and {@code View} of the touch point and, if allowed by the {@link com.nhaarman.listviewanimations.itemmanipulation.dragdrop.DraggableManager},
     * starts dragging the {@code View}.
     *
     * @param event the {@link android.view.MotionEvent} that was triggered.
     *
     * @return {@code true} if we have started dragging, {@code false} otherwise.
     */
    private boolean handleDownEvent(final MotionEvent event) {
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        return true;
    }
    
    /**
     * Handles the up event.
     * <p/>
     * Animates the hover drawable to its final position, and finalizes our drag properties when the animation has finished.
     * Will also notify the {@link com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener} set if applicable.
     *
     * @return {@code true} if the event was handled, {@code false} otherwise.
     */
    private boolean handleUpEvent() {
        if (mMobileView == null) {
            return false;
        }
        assert mHoverDrawable != null;

        //ofInt中两个参数是设置动画的起始位置和结束位置
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mHoverDrawable.getTop(), (int) mMobileView.getY());
        SettleHoverDrawableAnimatorListener listener = new SettleHoverDrawableAnimatorListener(mHoverDrawable, mMobileView);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.addListener(listener);
        valueAnimator.start();

        return true;
    }
    
    /**
     * Handles the cancel event.
     *
     * @return {@code true} if the event was handled, {@code false} otherwise.
     */
    private boolean handleCancelEvent() {
        return handleUpEvent();
    }
    
    public void dispatchDraw(final Canvas canvas) {
        if (mHoverDrawable != null) {
            mHoverDrawable.draw(canvas);
        }
    }
    
    /**
     * Handles the move events.
     * <p/>
     * Applies the {@link MotionEvent} to the hover drawable, and switches {@code View}s if necessary.
     *
     * @param event the {@code MotionEvent}.
     *
     * @return {@code true} if the event was handled, {@code false} otherwise.
     */
    private boolean handleMoveEvent(final MotionEvent event) {
        boolean handled = false;

        float deltaX = event.getRawX() - mDownX;
        float deltaY = event.getRawY() - mDownY;

        if (mHoverDrawable == null && Math.abs(deltaY) > mSlop && Math.abs(deltaY) > Math.abs(deltaX)) {
            int position = mDragAndDropListViewWrapper.pointToPosition((int) event.getX(), (int) event.getY());
            if (position != AdapterView.INVALID_POSITION) {
                View downView = mDragAndDropListViewWrapper.getChildAt(position - mDragAndDropListViewWrapper.getFirstVisiblePosition());
                assert downView != null;
                if (mDraggableManager.isDraggable(downView, position - mDragAndDropListViewWrapper.getHeaderViewsCount() , event.getX() - downView.getX(), event.getY() - downView.getY())) {
                    startDragging(position - mDragAndDropListViewWrapper.getHeaderViewsCount());
                    handled = true;
                }
            }
        } else if (mHoverDrawable != null) {
            mHoverDrawable.handleMoveEvent(event);  
            
            switchIfNecessary();
            mDragAndDropListViewWrapper.getListView().invalidate();
            handled = true;
        }

        return handled;
    }
    
    /**
     * Finds the {@code View} that is a candidate for switching, and executes the switch if necessary.
     */
    private void switchIfNecessary() {
        if (mHoverDrawable == null || mAdapter == null) {
            return;
        }

        int position = getPositionForId(mMobileItemId);    //mMobileItemId指代view对应的hashCode，这个函数通过这个id获取其在ListView中的位置
        long aboveItemId = position - 1 - mDragAndDropListViewWrapper.getHeaderViewsCount() >= 0 
                        ? mAdapter.getItemId(position - 1 - mDragAndDropListViewWrapper.getHeaderViewsCount()) 
                        : INVALID_ID;
        long belowItemId = position + 1 - mDragAndDropListViewWrapper.getHeaderViewsCount() < mAdapter.getCount()
                           ? mAdapter.getItemId(position + 1 - mDragAndDropListViewWrapper.getHeaderViewsCount())
                           : INVALID_ID;
                           LogUtil.e(TAG, "above id is " + aboveItemId + " below id is " + belowItemId);

        final long switchId = mHoverDrawable.isMovingUpwards() ? aboveItemId : belowItemId;
        View switchView = getViewForId(switchId);

        final int deltaY = mHoverDrawable.getDeltaY();  
        //getIntrinsicHeight获取drawable对象的高度
        if (switchView != null && Math.abs(deltaY) > mHoverDrawable.getIntrinsicHeight()) {
            LogUtil.e(TAG, "swift view");
            switchViews(switchView, switchId, mHoverDrawable.getIntrinsicHeight() * (deltaY < 0 ? -1 : 1)); 
        }

        mScrollHandler.handleMobileCellScroll();

        mDragAndDropListViewWrapper.getListView().invalidate();
    }
    
    /**
     * Switches the item that is currently being dragged with the item belonging to given id,
     * by notifying the adapter to swap positions and that the data set has changed.
     *
     * @param switchView   the {@code View} that should be animated towards the old position of the currently dragging item.
     * @param switchId     the id of the item that will take the position of the currently dragging item.
     * @param translationY the distance in pixels the {@code switchView} should animate - i.e. the (positive or negative) height of the {@code View} corresponding to the currently
     *                     dragging item.
     */
    private void switchViews(final View switchView, final long switchId, final float translationY) {
        assert mHoverDrawable != null;
        assert mAdapter != null;
        assert mMobileView != null;
        
        final int switchViewPosition = mDragAndDropListViewWrapper.getPositionForView(switchView);
        
        int mobileViewPosition = mDragAndDropListViewWrapper.getPositionForView(mMobileView);
        LogUtil.e(TAG, "switch view position is " + switchViewPosition 
                + " and mobile view position is" +  mobileViewPosition);

        ((Swappable) mAdapter).swapItems(switchViewPosition - mDragAndDropListViewWrapper.getHeaderViewsCount(), 
                    mobileViewPosition - mDragAndDropListViewWrapper.getHeaderViewsCount());
        ((BaseAdapter) mAdapter).notifyDataSetChanged();

        mHoverDrawable.shift(switchView.getHeight());
        mSwitchViewAnimator.animateSwitchView(switchId, translationY);
    }
    
    /**
     * A class which handles scrolling for this {@code DynamicListView} when dragging an item.
     * <p/>
     * The {@link #handleMobileCellScroll()} method initiates the scroll and should typically be called on a move {@code MotionEvent}.
     * <p/>
     * The {@link #onScroll(android.widget.AbsListView, int, int, int)} method then takes over the functionality {@link #handleMoveEvent(android.view.MotionEvent)} provides.
     */
    private class ScrollHandler implements AbsListView.OnScrollListener {

        private static final int SMOOTH_SCROLL_DP = 3;

        /**
         * The default scroll amount in pixels.
         */
        private final int mSmoothScrollPx;

        /**
         * The factor to multiply {@link #mSmoothScrollPx} with for scrolling.
         */
        private float mScrollSpeedFactor = 1.0f;

        /**
         * The previous first visible item before checking if we should switch.
         */
        private int mPreviousFirstVisibleItem = -1;

        /**
         * The previous last visible item before checking if we should switch.
         */
        private int mPreviousLastVisibleItem = -1;

        /**
         * The current first visible item.
         */
        private int mCurrentFirstVisibleItem;

        /**
         * The current last visible item.
         */
        private int mCurrentLastVisibleItem;

        ScrollHandler() {
            Resources r = mDragAndDropListViewWrapper.getListView().getResources();
            mSmoothScrollPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SMOOTH_SCROLL_DP, r.getDisplayMetrics());
        }

        /**
         * Sets the scroll speed when dragging an item. Defaults to {@code 1.0f}.
         *
         * @param scrollSpeedFactor {@code <1.0f} to slow down scrolling, {@code >1.0f} to speed up scrolling.
         */
        void setScrollSpeed(final float scrollSpeedFactor) {
            mScrollSpeedFactor = scrollSpeedFactor;
        }

        /**
         * Scrolls the {@code DynamicListView} if the hover drawable is above or below the bounds of the {@code ListView}.
         */
        void handleMobileCellScroll() {
            if (mHoverDrawable == null || mIsSettlingHoverDrawable) {
                return;
            }

            Rect r = mHoverDrawable.getBounds();
            int offset = mDragAndDropListViewWrapper.computeVerticalScrollOffset();
            int height = mDragAndDropListViewWrapper.getListView().getHeight();
            int extent = mDragAndDropListViewWrapper.computeVerticalScrollExtent();
            int range = mDragAndDropListViewWrapper.computeVerticalScrollRange();
            int hoverViewTop = r.top;
            int hoverHeight = r.height();

            int scrollPx = (int) Math.max(1, mSmoothScrollPx * mScrollSpeedFactor);
            if (hoverViewTop <= 0 && offset > 0) {     //滑动到顶端，上面还有item可以显示的情况
                mDragAndDropListViewWrapper.smoothScrollBy(-scrollPx, 0);
            } else if (hoverViewTop + hoverHeight >= height && offset + extent < range) {   //滑动到顶端，底部还有item可以显示的情况
                mDragAndDropListViewWrapper.smoothScrollBy(scrollPx, 0);
            }
        }

        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentLastVisibleItem = firstVisibleItem + visibleItemCount;

            mPreviousFirstVisibleItem = mPreviousFirstVisibleItem == -1 ? mCurrentFirstVisibleItem : mPreviousFirstVisibleItem;
            mPreviousLastVisibleItem = mPreviousLastVisibleItem == -1 ? mCurrentLastVisibleItem : mPreviousLastVisibleItem;

            if (mHoverDrawable != null) {
                assert mMobileView != null;
                float y = mMobileView.getY();
                mHoverDrawable.onScroll(y);
            }

            if (!mIsSettlingHoverDrawable) {   //如果没有再放置HoverDrawable的时候，即没有松手的时候
                checkAndHandleFirstVisibleCellChange();    //处理向上拖动到顶部的情况
                checkAndHandleLastVisibleCellChange();     //处理向下拖动到底部的情况
            }

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousLastVisibleItem = mCurrentLastVisibleItem;
        }

        @Override
        public void onScrollStateChanged(@NonNull final AbsListView view, final int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && mHoverDrawable != null) {
                handleMobileCellScroll();
            }
        }

        /**
         * Determines if the listview scrolled up enough to reveal(显示) a new cell(单元，这里理解为item比较好) at the
         * top of the list. If so, switches the newly shown view with the mobile view.
         */
        private void checkAndHandleFirstVisibleCellChange() {
            if (mHoverDrawable == null || mAdapter == null || mCurrentFirstVisibleItem >= mPreviousFirstVisibleItem) {
                return;
            }

            int position = getPositionForId(mMobileItemId);
            if (position == AdapterView.INVALID_POSITION) {
                return;
            }

            long switchItemId = position - 1 - mDragAndDropListViewWrapper.getHeaderViewsCount()>= 0 
                                   ? mAdapter.getItemId(position - 1 - mDragAndDropListViewWrapper.getHeaderViewsCount()) 
                                   : INVALID_ID;
            View switchView = getViewForId(switchItemId);
            if (switchView != null) {
                switchViews(switchView, switchItemId, -switchView.getHeight());
            }
        }

        /**
         * Determines if the listview scrolled down enough to reveal a new cell at the
         * bottom of the list. If so, switches the newly shown view with the mobile view.
         */
        private void checkAndHandleLastVisibleCellChange() {
            if (mHoverDrawable == null || mAdapter == null || mCurrentLastVisibleItem <= mPreviousLastVisibleItem) {
                return;
            }

            int position = getPositionForId(mMobileItemId);
            if (position == AdapterView.INVALID_POSITION) {
                return;
            }

            long switchItemId = position + 1 -mDragAndDropListViewWrapper.getHeaderViewsCount() < mAdapter.getCount()
                                ? mAdapter.getItemId(position + 1 - mDragAndDropListViewWrapper.getHeaderViewsCount())
                                : INVALID_ID;
            View switchView = getViewForId(switchItemId);
            if (switchView != null) {
                switchViews(switchView, switchItemId, switchView.getHeight());
            }
        }
    }
    
    /**
     * A {@link SwitchViewAnimator} for versions KitKat and below.
     * This class immediately updates {@link #mMobileView} to be the newly mobile view.
     */
    private class KitKatSwitchViewAnimator implements SwitchViewAnimator {

        @Override
        public void animateSwitchView(final long switchId, final float translationY) {
            assert mMobileView != null;
            mDragAndDropListViewWrapper.getListView().getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(mMobileView, switchId, translationY));
            mMobileView = getViewForId(mMobileItemId);
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private final View mPreviousMobileView;
            private final long mSwitchId;
            private final float mTranslationY;

            AnimateSwitchViewOnPreDrawListener(final View previousMobileView, final long switchId, final float translationY) {
                mPreviousMobileView = previousMobileView;
                mSwitchId = switchId;
                mTranslationY = translationY;
            }

            @Override
            public boolean onPreDraw() {
                mDragAndDropListViewWrapper.getListView().getViewTreeObserver().removeOnPreDrawListener(this);

                View switchView = getViewForId(mSwitchId);
                if (switchView != null) {
                    switchView.setTranslationY(mTranslationY);
                    switchView.animate().translationY(0).start();
                }
                mPreviousMobileView.setVisibility(View.VISIBLE);

                if (mMobileView != null) {
                    mMobileView.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        }
    }

    /**
     * A {@link SwitchViewAnimator} for versions L and above.
     * This class updates {@link #mMobileView} only after the next frame has been drawn.
     */
    private class LSwitchViewAnimator implements SwitchViewAnimator {

        @Override
        public void animateSwitchView(final long switchId, final float translationY) {
            mDragAndDropListViewWrapper.getListView().getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(switchId, translationY));
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private final long mSwitchId;
            private final float mTranslationY;

            AnimateSwitchViewOnPreDrawListener(final long switchId, final float translationY) {
                mSwitchId = switchId;
                mTranslationY = translationY;
            }

            @Override
            public boolean onPreDraw() {
                mDragAndDropListViewWrapper.getListView().getViewTreeObserver().removeOnPreDrawListener(this);

                View switchView = getViewForId(mSwitchId);
                if (switchView != null) {
                    switchView.setTranslationY(mTranslationY);
                    switchView.animate().translationY(0).start();
                }

                assert mMobileView != null;
                mMobileView.setVisibility(View.VISIBLE);
                mMobileView = getViewForId(mMobileItemId);
                assert mMobileView != null;
                mMobileView.setVisibility(View.INVISIBLE);
                return true;
            }
        }
    }
    
    /**
     * Retrieves the position in the list corresponding to itemId.
     *
     * @return the position of the item in the list, or {@link android.widget.AdapterView#INVALID_POSITION} if the {@code View} corresponding to the id was not found.
     */
    private int getPositionForId(final long itemId) {
        View v = getViewForId(itemId);
        if (v == null) {
            return AdapterView.INVALID_POSITION;
        } else {
            return mDragAndDropListViewWrapper.getPositionForView(v);
        }
    }

    /**
     * Retrieves the {@code View} in the list corresponding to itemId.
     *
     * @return the {@code View}, or {@code null} if not found.
     */
    @Nullable
    private View getViewForId(final long itemId) {
        ListAdapter adapter = mAdapter;
        if (itemId == INVALID_ID || adapter == null) {
            return null;
        }

        int firstVisiblePosition = mDragAndDropListViewWrapper.getFirstVisiblePosition();

        View result = null;
        for (int i = 0, total = mDragAndDropListViewWrapper.getChildCount(); i < total && result == null; i++) {
            int position = firstVisiblePosition + i;
            if (position - mDragAndDropListViewWrapper.getHeaderViewsCount() >= 0) {
                long id = adapter.getItemId(position - mDragAndDropListViewWrapper.getHeaderViewsCount());
                
                if (id == itemId) {
                    result = mDragAndDropListViewWrapper.getChildAt(i);
                }
            }
        }
        return result;
    }
    
    /**
     * Updates the hover drawable's bounds with the animated values.
     * When the animation has finished, it will reset all the drag properties.
     */
    private class SettleHoverDrawableAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

        private final HoverDrawable mAnimatingHoverDrawable;

        private final View mAnimatingMobileView;

        private SettleHoverDrawableAnimatorListener(final HoverDrawable animatingHoverDrawable, final View animatingMobileView) {
            mAnimatingHoverDrawable = animatingHoverDrawable;
            mAnimatingMobileView = animatingMobileView;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            mIsSettlingHoverDrawable = true;
        }

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            mAnimatingHoverDrawable.setTop((Integer) animation.getAnimatedValue());
            mDragAndDropListViewWrapper.getListView().postInvalidate();
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            mAnimatingMobileView.setVisibility(View.VISIBLE);

            mHoverDrawable = null;
            mMobileView = null;
            mMobileItemId = INVALID_ID;
            mOriginalMobileItemPosition = AdapterView.INVALID_POSITION;

            mIsSettlingHoverDrawable = false;
        }
    }
    
    private interface SwitchViewAnimator {
        void animateSwitchView(final long switchId, final float translationY);
    }
}
