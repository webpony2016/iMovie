package com.icareu.imovie.adapters;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by Tony on 2016/2/25.
 * Refer to http://www.jianshu.com/p/1cec183729f6.
 */
@SuppressWarnings("unchecked")
public class ViewHolder {
    private final String LOG_TAG = ViewHolder.class.getSimpleName();

    private SparseArray<View> mViewSparseArray;
    private View mParentView;

    public static ViewHolder getViewHolder(View parentView) {
        ViewHolder viewHolder = (ViewHolder) parentView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(parentView);
            parentView.setTag(viewHolder);
        }
        return viewHolder;
    }

    private ViewHolder(View parentView) {
        this.mParentView = parentView;
        mViewSparseArray = new SparseArray<View>();
    }

    public <T extends View> T get(int resourceId) {
        View childView = mViewSparseArray.get(resourceId);
        if (childView == null) {
            childView = mParentView.findViewById(resourceId);
            mViewSparseArray.put(resourceId, childView);
            Log.v(LOG_TAG,"findViewById call");
        }else{
            Log.v(LOG_TAG,"fetch from SparseArray");
        }
        return (T) childView;
    }
}
