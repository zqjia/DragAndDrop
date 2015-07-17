package com.example.draganddrop;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 * 一个DynamicListView和 
 * */

public interface DragAndDropListViewWrapper {
    
    ViewGroup getListView();
    
    View getChildAt(final int index);
    
    int getFirstVisiblePosition();
    
    int getLastVisiblePosition();
    
    int getCount();
    
    int getChildCount();
    
    int getHeaderViewsCount();
    
    int getPositionForView(final View view);
    
    ListAdapter getAdapter();
    
    void smoothScrollBy(int distance, int duration);
    
    void setDynamicOnScrollListener(AbsListView.OnScrollListener onScrollListener);
    
    int pointToPosition(int x, int y);
    
    int computeVerticalScrollOffset();
    
    int computeVerticalScrollExtent();
    
    int computeVerticalScrollRange();
}
