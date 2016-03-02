package com.icareu.imovie.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.icareu.imovie.MovieDetailActivity;
import com.icareu.imovie.MovieDetailFragment;
import com.icareu.imovie.R;
import com.icareu.imovie.beans.Movie;

import java.util.List;

/**
 * Created by Tony on 2016/2/25.
 */
public class MovieGridAdapter extends BaseAdapter {
    private final String LOG_TAG = MovieGridAdapter.class.getSimpleName();
    private Context mContext;

    public MovieGridAdapter(Context context, List<?> list) {
        super(context, list);
        mContext = context;
    }

    public MovieGridAdapter(Activity activity, List<?> list) {
        super(activity.getApplicationContext(), list);
        mContext = activity;
    }
    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.movie_list_content;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = (Movie) mList.get(position);
        final ImageView imageView = holder.get(R.id.ivMovie);
        String url = movie.getPosterPath();
        BitmapImageViewTarget biv = new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(25);
                view.setImageDrawable(circularBitmapDrawable);
            }
        };
        assert mContext != null;
        Glide.with(mContext).load(url).asBitmap().fitCenter().into(biv);
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext,MovieDetailActivity.class);
                Movie movie = (Movie) mList.get(position);
                intent.putExtra(MovieDetailFragment.RECIEVE_DATA,movie);
                mContext.startActivity(intent);
                Log.v(LOG_TAG,movie.getTitle());
            }
        });
        Log.i(LOG_TAG,url);
    }
}
