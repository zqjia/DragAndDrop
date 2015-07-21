
package com.example.draganddrop;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAndDropAdapter extends BaseAdapter implements Swappable {

    private static final String TAG = DragAndDropAdapter.class.getSimpleName();

    private ArrayList<BookmarkBean> mDataList = new ArrayList<BookmarkBean>();
    private LayoutInflater mLayoutInflater;

    private static final int INVALID_STATE = -1;
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

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        BookmarkBean item = (BookmarkBean)getItem(position);
        if (item.getIsEdit()) {
            return IN_EDIT_STATE;
        } else {
            return NOT_IN_EDIT_STATE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final BookmarkBean item = (BookmarkBean)getItem(position);
        int viewType = INVALID_STATE;
        if (item.getIsEdit()) {
            viewType = IN_EDIT_STATE;
        } else {
            viewType = NOT_IN_EDIT_STATE;
        }

        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (viewType) {
                case IN_EDIT_STATE:
                    convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                    viewHolder.bookmarkText = (TextView)convertView
                            .findViewById(R.id.bookmark_edit_text);
                    viewHolder.bookmarkCheckbox = (CheckBox)convertView
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
                    LogUtil.e(TAG, "view type is not correct");
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final String bookmarkTitle = item.getTitle();
        viewHolder.bookmarkText.setText(bookmarkTitle);

        return convertView;
    }
    
    @Override
    public void swapItems(int positionOne, int positionTwo) {
        BookmarkBean firstItem = mDataList.set(positionOne, (BookmarkBean)getItem(positionTwo));
        mDataList.set(positionTwo, firstItem);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox bookmarkCheckbox;
        ImageView bookmarkIndicator;
        TextView bookmarkText;
        ImageView bookmarkEdit;
        ImageView bookmarkDrag;
    }

}
