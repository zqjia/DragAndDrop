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


public class DragAndDropAdapter1 extends BaseAdapter implements Swappable {
   private static final String TAG = DragAndDropAdapter.class.getSimpleName();
    
    private ArrayList<String> mDataList = new ArrayList<String>();
    private LayoutInflater mLayoutInflater;
    private boolean mIsEdited = false;
    
    public DragAndDropAdapter1(Context context, ArrayList<String> list) {
        if (list != null && !list.isEmpty()) {
            mDataList = list;
        }
        mLayoutInflater = LayoutInflater.from(context);
    }
    
    public void setData(ArrayList<String> list) {
        mDataList = list;
        notifyDataSetChanged();
    }
    
    public void setEditEnable(boolean isEdited) {
        mIsEdited = isEdited;
    }
    
    public boolean getEditEnable() {
        return mIsEdited;
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
//        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
/*        if (!mIsEdited) {
            LogUtil.e(TAG, "------>not in edit state");
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LogUtil.e(TAG, "inflate drag_item layout");
                convertView = mLayoutInflater.inflate(R.layout.drag_item, parent, false);
                viewHolder.bookmarkText = (TextView)convertView.findViewById(R.id.bookmark_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
                if (viewHolder.bookmarkCheckbox != null) {
                    LogUtil.e(TAG, "in no edit state convert view can't reuser");
                }
                
                convertView = mLayoutInflater.inflate(R.layout.drag_item, parent, false);
                viewHolder.bookmarkText = (TextView)convertView.findViewById(R.id.bookmark_text);
                convertView.setTag(viewHolder);
                
            }
        } else {
            LogUtil.e(TAG, "------------>in edit state");
            if (convertView == null) {
                
                viewHolder = new ViewHolder();
                LogUtil.e(TAG, "---------->inflate drag_item_edit layout");
                convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                
                viewHolder.bookmarkText = (TextView)convertView.findViewById(R.id.bookmark_edit_text);
                viewHolder.bookmarkCheckbox = (CheckBox)convertView.findViewById(R.id.bookmark_edit_checkbox);
                viewHolder.bookmarkIndicator = (ImageView)convertView.findViewById(R.id.bookmark_edit_indicator);
                viewHolder.bookmarkEdit = (ImageView)convertView.findViewById(R.id.bookmark_edit_editor);
                viewHolder.bookmarkDrag = (ImageView)convertView.findViewById(R.id.bookmark_edit_drag);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
                if (viewHolder.bookmarkCheckbox == null) {
                    LogUtil.e(TAG, "in edit state convert view can't reuse");
                }
                
                convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
                viewHolder.bookmarkText = (TextView)convertView.findViewById(R.id.bookmark_edit_text);
                viewHolder.bookmarkCheckbox = (CheckBox)convertView.findViewById(R.id.bookmark_edit_checkbox);
                viewHolder.bookmarkIndicator = (ImageView)convertView.findViewById(R.id.bookmark_edit_indicator);
                viewHolder.bookmarkEdit = (ImageView)convertView.findViewById(R.id.bookmark_edit_editor);
                viewHolder.bookmarkDrag = (ImageView)convertView.findViewById(R.id.bookmark_edit_drag);
                convertView.setTag(viewHolder);
            }
        }
        
        
        final String text = (String)mDataList.get(position);
        viewHolder.bookmarkText.setText(text);
        return convertView;*/
        
    
        //FIXME
        //下面是没有编辑状态改变的代码
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.drag_item_edit, parent, false);
            viewHolder.bookmarkText = (TextView)convertView.findViewById(R.id.bookmark_edit_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        
        final String item = mDataList.get(position);
        viewHolder.bookmarkText.setText(item);
        
        return convertView;
    }

    @Override
    public void swapItems(int positionOne, int positionTwo) {
        String firstItem = mDataList.set(positionOne, (String)getItem(positionTwo));
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
