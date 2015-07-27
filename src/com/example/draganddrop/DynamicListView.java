package com.example.draganddrop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;


public class DynamicListView extends ListView {
    
    private static final String TAG = DynamicListView.class.getSimpleName();
    
    private DynamicOnScrollListener mDynamicOnScrollListener;
    private DragAndDropHandler mDragAndDropHandler;
    private TouchEventHandler mCurrentTouchEventHandler;
    
    public DynamicListView(final Context context) {
        this(context, null);
    }
    
    public DynamicListView( final Context context, final AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("listViewStyle", "attr", "android"));
    }
    
    public DynamicListView(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mDynamicOnScrollListener = new DynamicOnScrollListener();
        super.setOnScrollListener(mDynamicOnScrollListener);
    }
    
    public void setDynamicOnScrollListener(OnScrollListener listener) {
        mDynamicOnScrollListener.setOnScrollListener(listener);
    }
    
    public void enableDragAndDrop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            throw new UnsupportedOperationException("Drag and drop is only supported API levels 9 and up!");
        }

        mDragAndDropHandler = new DragAndDropHandler(this);
    }
    
    public void setAdapter(final ListAdapter adapter) {
        super.setAdapter(adapter);

        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setAdapter(adapter);
        } else {
            LogUtil.e(TAG, "DragAndDropHandler is null and can't support drag now, set adapter failure");
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        
        if (mCurrentTouchEventHandler == null) {
            boolean isFirstTimeInteracting = false;
            
            if (mDragAndDropHandler != null) {
                mDragAndDropHandler.onTouchEvent(ev);
                isFirstTimeInteracting = mDragAndDropHandler.isInteracting();
                if (isFirstTimeInteracting) {
                    mCurrentTouchEventHandler = mDragAndDropHandler;
                }
            } 
            return isFirstTimeInteracting || super.dispatchTouchEvent(ev);
        }
        return onTouchEvent(ev); 
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        
        if (mCurrentTouchEventHandler == null) {
            return super.onTouchEvent(ev);
        }
        
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)ev.getX();
            int y = (int)ev.getY();
            
            int dragPosition = pointToPosition(x, y);
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.onTouchEvent(ev);
            }
            ViewGroup dragItemView = (ViewGroup)getChildAt(dragPosition);
            if (dragItemView != null && mDragAndDropHandler != null) {
                if (mDragAndDropHandler.getDraggableManager().isDraggable(dragItemView, dragPosition, x, y)) {
                    startDragging(dragPosition);
                }
            }
        }
        
        //handle touch event by mCurrentTouchEventHandler
        mCurrentTouchEventHandler.onTouchEvent(ev);

        if (ev.getActionMasked() == MotionEvent.ACTION_UP 
                || ev.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            mCurrentTouchEventHandler = null;
        }

        return mCurrentTouchEventHandler != null || super.onTouchEvent(ev);
    }
    
    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.dispatchDraw(canvas);
        }
    }
    
    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }
    
    public void setDraggableManager(final DraggableManager draggableManager) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setDraggableManager(draggableManager);
        }
    }
    
    /**
     * start drag the item at the given position
     * @param position the position of the item
     * */
    public void startDragging(final int position) {

        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.startDragging(position);
        }
    }

    /**
     * Sets the scroll speed when dragging an item. Defaults to {@code 1.0f}.
     * 
     * This method does nothing if the drag and drop functionality is not enabled.
     *
     * @param speed {@code <1.0f} to slow down scrolling, {@code >1.0f} to speed up scrolling.
     */
    public void setScrollSpeed(final float speed) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setScrollSpeed(speed);
        }
    }
    
    private class DynamicOnScrollListener implements OnScrollListener {

        private OnScrollListener mOnScrollListener;
        
        public DynamicOnScrollListener() {
            
        }
        
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
        
        public void setOnScrollListener(OnScrollListener listener) {
            mOnScrollListener = listener;
        }
    }
}
