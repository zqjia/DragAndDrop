package com.example.draganddrop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


public class DynamicListView extends ListView {
    
    private static final String TAG = DynamicListView.class.getSimpleName();
    
    private DynamicOnScrollListener mDynamicOnScrollListener;
    private DragAndDropHandler mDragAndDropHandler;
    private TouchEventHandler mCurrentTouchEventHandler;
    
    {
        //just test 
        Button button = new Button(getContext());
        LinearLayout l = new LinearLayout(getContext());
//        Adapter adapter = new Adapter();
    }
    
    public DynamicListView(final Context context) {
        this(context, null);
    }
    
    public DynamicListView( final Context context, final AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("listViewStyle", "attr", "android"));
    }
    
    public DynamicListView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mDynamicOnScrollListener = new DynamicOnScrollListener();
        super.setOnScrollListener(mDynamicOnScrollListener);
    }
    
    public void setDynamicOnScrollListener(OnScrollListener listener) {
        mDynamicOnScrollListener.setOnScrollListener(listener);
    }
    
    public void enableDragAndDrop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            throw new UnsupportedOperationException("Drag and drop is only supported API levels 14 and up!");
        }

        mDragAndDropHandler = new DragAndDropHandler(this);
    }
    
    public void setAdapter(final ListAdapter adapter) {
        super.setAdapter(adapter);

        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setAdapter(adapter);
        } else {
            LogUtil.e(TAG, "DragAndDropHandler is null");
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
        
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)ev.getX();
            int y = (int)ev.getY();
            
            int dragPosition = pointToPosition(x, y);
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.dispatchTouchEvent(ev);
            }
            ViewGroup dragItemView = (ViewGroup)getChildAt(dragPosition);
            if (dragItemView != null) {
                if (mDragAndDropHandler.getDraggableManager().isDraggable(dragItemView, dragPosition, x, y)) {
                    startDragging(dragPosition);
                }
            }
        }
        
        //这里完成对事件的处理
        if (mCurrentTouchEventHandler != null) {
            mCurrentTouchEventHandler.onTouchEvent(ev);
        }

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
/*    public void setScrollSpeed(final float speed) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setScrollSpeed(speed);
        }
    }*/
    
    private class DynamicOnScrollListener implements OnScrollListener {

        private OnScrollListener mOnScrollListener;
        
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
