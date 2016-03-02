package com.icareu.imovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

/**
 * Created by Tony on 2016/2/27.
 * refer to http://www.jianshu.com/p/1cec183729f6
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecycledViewHolder> {
    public List<?> mList;

    private Context mContext;

    public BaseAdapter(Context context, List<?> mList) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public RecycledViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(mContext).inflate(onCreateViewLayoutID(viewType), null);

        return new RecycledViewHolder(parentView);
    }

    public abstract int onCreateViewLayoutID(int viewType);


    @Override
    public void onViewRecycled(final RecycledViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final RecycledViewHolder holder, final int position) {

        onBindViewHolder(holder.getViewHolder(), position);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, holder.getAdapterPosition(), holder.getItemId());
                }
            });
        }

    }

    public abstract void onBindViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}