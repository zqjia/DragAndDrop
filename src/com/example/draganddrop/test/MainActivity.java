package com.example.draganddrop.test;

import java.util.ArrayList;

import com.example.draganddrop.DraggableManager;
import com.example.draganddrop.DynamicListView;
import com.example.draganddrop.LogUtil;
import com.example.draganddrop.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.CheckBox;
import android.widget.ImageView;
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
        
        initData();
        mEditBookmark = (TextView)findViewById(R.id.edit_bookmark);
        
        mDynamicListView = (DynamicListView)findViewById(
                R.id.activity_dynamiclistview_listview);
        mDragAndDropAdapter = new DragAndDropAdapter(this, mDataList);
        mDynamicListView.setAdapter(mDragAndDropAdapter);
        
        mEditBookmark.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (mEditBookmark.getText().equals(getApplicationContext().getResources().getString(R.string.bookmark_edit))) {
                    mEditBookmark.setText(getApplication().getResources().getString(R.string.bookmark_edit_finish));
                    mShouldAnimator = true;
                    
                    mDynamicListView.enableDragAndDrop();
                    mDynamicListView.setDraggableManager(new DraggableManager(R.id.bookmark_edit_drag));
                    LogUtil.enableLog();
                    LogUtil.disableLogDebug();
                    changeDataToEditState(mShouldAnimator);
                } else {
                    mEditBookmark.setText(getApplication().getResources().getString(R.string.bookmark_edit));
                    mShouldAnimator = false;
                    changeDataToEditState(mShouldAnimator);
                }
                mDragAndDropAdapter.notifyDataSetChanged();
                
                final ViewTreeObserver vto = mDynamicListView.getViewTreeObserver();
                vto.addOnPreDrawListener(new OnPreDrawListener() {
                    
                    @Override
                    public boolean onPreDraw() {
                        vto.removeOnPreDrawListener(this);
                        if (mShouldAnimator && mEditBookmark.getText().equals(getApplicationContext().getResources().getString(R.string.bookmark_edit_finish))) {
                            mShouldAnimator = false;
                            animatorListView();
                        }
                        return false;
                    }
                });
                
            }
        });
    }

    private void animatorListView() {
        for(int i=0, total=mDynamicListView.getChildCount(); i<total; ++i) {
            View itemView = mDynamicListView.getChildAt(i);
            ImageView checkBox = (ImageView)itemView.findViewById(R.id.bookmark_edit_checkbox);
            ImageView editor = (ImageView)itemView.findViewById(R.id.bookmark_edit_editor);
            ImageView drag = (ImageView)itemView.findViewById(R.id.bookmark_edit_drag);
            
            ObjectAnimator checkBoxMoveIn = ObjectAnimator.ofFloat(checkBox, "translationX", -100f, 0f);
            ObjectAnimator checkBoxFadeIn = ObjectAnimator.ofFloat(checkBox, "alpha", 0f, 1f);
            
            ObjectAnimator editorMoveIn = ObjectAnimator.ofFloat(editor, "translationX", 100f, 0f);
            ObjectAnimator editorFadeIn = ObjectAnimator.ofFloat(editor, "alpha", 0f, 1f);
            
            ObjectAnimator dragMoveIn = ObjectAnimator.ofFloat(drag, "translationX", 100f, 0f);
            ObjectAnimator dragFadeIn = ObjectAnimator.ofFloat(drag, "alpha", 0f, 1f);
            
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(checkBoxMoveIn).with(checkBoxFadeIn).with(editorMoveIn).with(editorFadeIn).with(dragMoveIn).with(dragFadeIn);
            animatorSet.setDuration(150l);
            animatorSet.start();
            
            
//            checkBox.setTranslationX(-100);
//            checkBox.setVisibility(View.VISIBLE);
//            checkBox.setAlpha(0f);
//            checkBox.animate().translationX(0f).alpha(1f).setDuration(150).start();
//            
//            editor.setTranslationX(100);
//            editor.setVisibility(View.VISIBLE);
//            editor.setAlpha(0f);
//            editor.animate().translationX(0f).alpha(1f).setDuration(150).start();
//            
//            drag.setTranslationX(100f);
//            drag.setVisibility(View.VISIBLE);
//            drag.setAlpha(0f);
//            drag.animate().translationX(0f).alpha(1f).setDuration(150l).start();
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
