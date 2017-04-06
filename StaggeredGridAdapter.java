package com.mss.edyx.adapters;

/**
 * Created by mss on 11/3/17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mss.edyx.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class StaggeredGridAdapter extends BaseAdapter {
    private Context context;
    private final String[] mobileValues;

    public StaggeredGridAdapter(Context context, String[] mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
    }


    public static class ViewHolder {
        public ImageView imageView;
        public TextView textView, textViewGlanceCount, textViewLikeCount;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        ViewHolder views;
        if (convertView == null) {
            views = new ViewHolder();
            gridView = inflater.inflate(R.layout.list_staggered, null);

            views.textView = (TextView) gridView
                    .findViewById(R.id.tv_name_list_subscription);
            views.textViewGlanceCount = (TextView) gridView
                    .findViewById(R.id.tv_glancecount_list_staggered);
            views.textViewLikeCount = (TextView) gridView
                    .findViewById(R.id.tv_likecount_list_staggered);
            views.imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

            gridView.setTag(views);
        } else {
            views = (ViewHolder) convertView.getTag();
            gridView = convertView;
        }
        if (position == 0) {
            gridView.findViewById(R.id.ll_dummy).setVisibility(View.VISIBLE);
            gridView.findViewById(R.id.ll_data).setVisibility(View.GONE);
        } else {
            gridView.findViewById(R.id.ll_dummy).setVisibility(View.GONE);
            gridView.findViewById(R.id.ll_data).setVisibility(View.VISIBLE);
            views.textView.setText(mobileValues[position - 1]);
        }
        return gridView;
    }

    @Override
    public int getCount() {
        return mobileValues.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private Drawable getBitmapFromUrl(String urlImg) {
        URL url;
        Bitmap bmp = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);
            url = new URL(urlImg);
            bmp = BitmapFactory.decodeStream(url.openConnection()
                    .getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = new BitmapDrawable(
                context.getResources(), bmp);
        return drawable;
    }


    private void uploadImage(String url, final ImageView img_deal) {
        // TODO Auto-generated method stub
        if (url != null && !url.equalsIgnoreCase("")) {
            try {
                Picasso.with((Activity) context).load(url)
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.loading)
                        .into(img_deal, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                img_deal.setImageResource(R.mipmap.loading);
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
                img_deal.setImageResource(R.mipmap.loading);
            }
        } else {
            img_deal.setImageResource(R.mipmap.loading);
        }
    }

}