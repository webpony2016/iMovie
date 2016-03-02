package com.icareu.imovie.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tony on 2016/2/27.
 * Refer to http://www.jianshu.com/p/1cec183729f6
 */
public class RecycledViewHolder extends RecyclerView.ViewHolder {

    private ViewHolder viewHolder;

    public RecycledViewHolder(View itemView) {
        super(itemView);
        viewHolder=ViewHolder.getViewHolder(itemView);
    }


    public ViewHolder getViewHolder() {
        return viewHolder;
    }

}