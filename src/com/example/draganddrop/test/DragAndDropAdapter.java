
package com.example.draganddrop.test;

import java.util.ArrayList;

import com.example.draganddrop.LogUtil;
import com.example.draganddrop.R;
import com.example.draganddrop.Swappable;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAndDropAdapter extends BaseAdapter {

    private static final String TAG = DragAndDropAdapter.class.getSimpleName();

    private ArrayList<BookmarkBean> mDataList = new ArrayList<BookmarkBean>();
    private LayoutInflater mLayoutInflater;

    private static final int INVALID_STATE = -1;
    private static final int INVALID_ID = -1;
    private static final int NOT_IN_EDIT_STATE = 0;
    private static final int IN_EDIT_STATE = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public DragAndDropAdapter(Context context, ArrayList<BookmarkBean> list) {
        if (list != null && !list.isEmpty()) {
            mDataList = list;
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<BookmarkBean> list) {
        mDataList = list;
        notifyDataSetChanged();
    }
    
    public ArrayList<BookmarkBean> getData() {
        return mDataList;
    }

    @Override
    public int getCount() {
        if (mDataList != null) {
            return mDataList.size();
        } else {
            LogUtil.e(TAG, "data associated to this adapter is null");
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mDataList != null) {
            return mDataList.get(position);
        } 
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mDataList != null) {
            return getItem(position).hashCode();
        } else {
            return INVALID_ID;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BookmarkBean item = (BookmarkBean)getItem(position);
        if (item != null) {
            if (item.getIsEdit()) {
                return IN_EDIT_STATE;
            } else {
                return NOT_IN_EDIT_STATE;
            }
        } else {
            LogUtil.e(TAG, "data associated to adapter is something wrong");
            return INVALID_STATE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
//        LogUtil.e(TAG, "----------->getView");
        
        final BookmarkBean item = (BookmarkBean)getItem(position);
        if (item == null) {
            LogUtil.e(TAG, "item is null");
            return null;
        }
        
        int viewType = INVALID_STATE;
        if (item.getIsEdit()) {
            viewType = IN_EDIT_STATE;
        } else {
            viewType = NOT_IN_EDIT_STATE;
        }
        
/*        ViewHolder viewHolder = null;
        if (convertView == null || (convertView != null && Build.VERSION.SDK_INT < 14 
                && (convertView.getVisibility() == View.INVISIBLE || convertView.getAnimation() != null))) {
            if (convertView != null) {
                if (convertView.getAnimation() != null) {
                    convertView.clearAnimation();
                } 
                
                if (convertView.getVisibility() == View.INVISIBLE) {
                    convertView.setVisibility(View.VISIBLE);
                }
                
            }
            viewHolder = new ViewHolder();
            switch (viewType) {
                case IN_EDIT_STATE:
                    convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                    viewHolder.bookmarkText = (TextView)convertView
                            .findViewById(R.id.bookmark_edit_text);
                    viewHolder.bookmarkCheckbox = (ImageView)convertView
                            .findViewById(R.id.bookmark_edit_checkbox);
                    viewHolder.bookmarkDrag = (ImageView)convertView
                            .findViewById(R.id.bookmark_edit_drag);
                    viewHolder.bookmarkEdit = (ImageView)convertView
                            .findViewById(R.id.bookmark_edit_editor);
                    break;
                case NOT_IN_EDIT_STATE:
                    convertView = mLayoutInflater.inflate(R.layout.drag_item, parent, false);
                    viewHolder.bookmarkText = (TextView)convertView
                            .findViewById(R.id.bookmark_text);
                    break;
                default:
                    LogUtil.e(TAG, "view type is invalid");
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        
        final String title = item.getTitle();
        viewHolder.bookmarkText.setText(title);*/
        
        
        
        TextView textView = null;
        ViewHolder viewHolder = null;
        if (Build.VERSION.SDK_INT < 14) {
            switch(viewType) {
                case IN_EDIT_STATE:
                    convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                    textView = (TextView)convertView.findViewById(R.id.bookmark_edit_text);
                    break;
                case NOT_IN_EDIT_STATE:
                    convertView = mLayoutInflater.inflate(R.layout.drag_item, parent, false);
                    textView = (TextView)convertView.findViewById(R.id.bookmark_text);
                    break;
                default:
                    break;
            }
        } else {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                switch (viewType) {
                    case IN_EDIT_STATE:
                        convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                        viewHolder.bookmarkText = (TextView)convertView
                                .findViewById(R.id.bookmark_edit_text);
                        viewHolder.bookmarkCheckbox = (ImageView)convertView
                                .findViewById(R.id.bookmark_edit_checkbox);
                        viewHolder.bookmarkDrag = (ImageView)convertView
                                .findViewById(R.id.bookmark_edit_drag);
                        viewHolder.bookmarkEdit = (ImageView)convertView
                                .findViewById(R.id.bookmark_edit_editor);
                        break;
                    case NOT_IN_EDIT_STATE:
                        convertView = mLayoutInflater.inflate(R.layout.drag_item, parent, false);
                        viewHolder.bookmarkText = (TextView)convertView
                                .findViewById(R.id.bookmark_text);
                        break;
                    default:
                        LogUtil.e(TAG, "view type is invalid");
                        break;
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
        }

        
        
        final String title = item.getTitle();    
        if (Build.VERSION.SDK_INT < 14) {
            if (textView != null) {
                textView.setText(title);
            }
        } else {
            if (viewHolder.bookmarkText != null) {
                viewHolder.bookmarkText.setText(title);
            }
        }
        
        return convertView;
    }
    
    /**
     * swap the item when drag and need swap
     * @param positionOne the position of the first item in the list
     * @param positionTwo the position of the second item in the list
     * */
//    @Override
//    public void swapItems(int positionOne, int positionTwo) {
//        BookmarkBean firstItem = mDataList.set(positionOne, (BookmarkBean)getItem(positionTwo));
//        mDataList.set(positionTwo, firstItem);
//    }
    
    public void handleData(int firstPosition, int lastPosition) {
        //不能用下面这种方式，因为temp存储的是一个引用，下面会对该引用进行赋值，那么temp引用也就改变了，达不到暂时存储的目的
        //BookmarkBean temp = mDataList.get(firstPosition)
        BookmarkBean temp = new BookmarkBean(mDataList.get(firstPosition));
        
        if (firstPosition < lastPosition) {
            for (int i=firstPosition; i<lastPosition; ++i) {
                mDataList.get(i).setBookmarkBean(mDataList.get(i+1));
            }
        } else if (firstPosition > lastPosition){
            for (int i=firstPosition; i>lastPosition; --i) {
                mDataList.get(i).setBookmarkBean(mDataList.get(i-1));
            }
        }
        
        mDataList.get(lastPosition).setBookmarkBean(temp);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView bookmarkCheckbox;
        ImageView bookmarkIndicator;
        TextView bookmarkText;
        ImageView bookmarkEdit;
        ImageView bookmarkDrag;
    }

}
