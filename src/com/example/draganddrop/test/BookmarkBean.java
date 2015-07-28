package com.example.draganddrop.test;

public class BookmarkBean {

    private String mTitle;
    private String mUrl;
    private boolean mIsInEdit;
    
    public BookmarkBean() {
        
    }
    
    public BookmarkBean(BookmarkBean bookmarkBean) {
        mTitle = bookmarkBean.getTitle();
        mUrl = bookmarkBean.getUrl();
        mIsInEdit = bookmarkBean.getIsEdit();
    }
    
    public BookmarkBean(String title, String url, boolean isEdit) {
        mTitle = title;
        mUrl = url;
        mIsInEdit = isEdit;
    }
    
    public void setBookmarkBean(BookmarkBean bookmarkBean) {
        mTitle = bookmarkBean.getTitle();
        mUrl = bookmarkBean.getUrl();
        mIsInEdit = bookmarkBean.getIsEdit();
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setUrl(String url) {
        mUrl  = url;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public void setIsEdit(boolean isEdit) {
        mIsInEdit = isEdit;
    }
    
    public boolean getIsEdit() {
        return mIsInEdit;
    }
}
