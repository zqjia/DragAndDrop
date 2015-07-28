package com.example.draganddrop;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

public class DynamicListViewWrapper implements DragAndDropListViewWrapper {

    private final DynamicListView mDynamicListView;
    
    public DynamicListViewWrapper(DynamicListView listView) {
        mDynamicListView = listView;
    }
    
    @Override
    public ViewGroup getListView() {
        return mDynamicListView;
    }

    @Override
    public View getChildAt(final int index) {
        return mDynamicListView.getChildAt(index);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mDynamicListView.getFirstVisiblePosition();
    }

    @Override
    public int getLastVisiblePosition() {
        return mDynamicListView.getLastVisiblePosition();
    }

    @Override
    public int getCount() {
        return mDynamicListView.getCount();
    }

    @Override
    public int getChildCount() {
        return mDynamicListView.getChildCount();
    }

    @Override
    public int getHeaderViewsCount() {
        return mDynamicListView.getHeaderViewsCount();
    }

    @Override
    public int getPositionForView(final View view) {
        return mDynamicListView.getPositionForView(view);
    }

    @Override
    public ListAdapter getAdapter() {
        return mDynamicListView.getAdapter();
    }

    @Override
    public void smoothScrollBy(int distance, int duration) {
        mDynamicListView.smoothScrollBy(distance, duration);
    }

    @Override
    public void setDynamicOnScrollListener(OnScrollListener onScrollListener) {
        mDynamicListView.setDynamicOnScrollListener(onScrollListener);
    }

    @Override
    public int pointToPosition(int x, int y) {
        return mDynamicListView.pointToPosition(x, y);
    }

    @Override
    public int computeVerticalScrollOffset() {
        return mDynamicListView.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return mDynamicListView.computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {
        return mDynamicListView.computeVerticalScrollRange();
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mDynamicListView.smoothScrollToPosition(position);
    }
    
}
