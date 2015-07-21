package com.example.draganddrop;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnDrawListener;
import android.widget.TextView;


public class MainActivity extends Activity {

    private DynamicListView mDynamicListView;
    private ArrayList<BookmarkBean> mDataList = new ArrayList<BookmarkBean>();
    private TextView mEditBookmark;
    private DragAndDropAdapter mDragAndDropAdapter;
    
    private boolean mShouldAnimator = true;
    
    private static final int DATA_COUNT = 20;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mEditBookmark = (TextView)findViewById(R.id.edit_bookmark);
        mDynamicListView = (DynamicListView)findViewById(R.id.activity_dynamiclistview_listview);
        
        initData();
        mDragAndDropAdapter = new DragAndDropAdapter(this, mDataList);
        mDynamicListView.setAdapter(mDragAndDropAdapter);
        mDynamicListView.enableDragAndDrop();
        mDynamicListView.setDraggableManager(new DraggableManager(R.id.bookmark_edit_drag));
        
        mEditBookmark.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (mEditBookmark.getText().equals(getApplicationContext().getResources().getString(R.string.bookmark_edit))) {
                    mEditBookmark.setText(getApplication().getResources().getString(R.string.bookmark_edit_finish));
                    mShouldAnimator = true;
                    
                    changeDataToEditState(mShouldAnimator);
                } else {
                    mEditBookmark.setText(getApplication().getResources().getString(R.string.bookmark_edit));
                    mShouldAnimator = false;
                    changeDataToEditState(mShouldAnimator);
                }
                mDragAndDropAdapter.notifyDataSetChanged();
                
                mDynamicListView.getViewTreeObserver().addOnDrawListener(new OnDrawListener() {
                    
                    @Override
                    public void onDraw() {
                        if (mShouldAnimator && mEditBookmark.getText().equals(getApplicationContext().getResources().getString(R.string.bookmark_edit_finish))) {
                            mShouldAnimator = false;
                            animatorListView();
                        }
                    }
                });
                
            }
        });
        
    }

    private void animatorListView() {
        for(int i=0, total=mDynamicListView.getChildCount(); i<total; ++i) {
            View itemView = mDynamicListView.getChildAt(i);
            View checkBox = itemView.findViewById(R.id.bookmark_edit_checkbox);
            View editor = itemView.findViewById(R.id.bookmark_edit_editor);
            View drag = itemView.findViewById(R.id.bookmark_edit_drag);
            
            checkBox.setTranslationX(-100);
            checkBox.setAlpha(0f);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.animate().translationX(0f).alpha(1f).setDuration(150).start();
            
            editor.setTranslationX(100);
            editor.setAlpha(0f);
            editor.setVisibility(View.VISIBLE);
            editor.animate().translationX(0f).alpha(1f).setDuration(150).start();
            
            drag.setTranslationX(100f);
            drag.setAlpha(0f);
            drag.setVisibility(View.VISIBLE);
            drag.animate().translationX(0f).alpha(1f).setDuration(150l).start();
        }
    }    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initData() {
        for (int i = 0; i < DATA_COUNT; i++) {
            String title = "this is title " + i;
            String url = "this is url " + i;
            boolean isEdit = false;
            mDataList.add(new BookmarkBean(title, url, isEdit));
        }
    }
    
    private void changeDataToEditState(boolean isEdit) {
        for (int i=0; i<DATA_COUNT; ++i) {
            ((BookmarkBean)mDataList.get(i)).setIsEdit(isEdit);
        }
    }
}
