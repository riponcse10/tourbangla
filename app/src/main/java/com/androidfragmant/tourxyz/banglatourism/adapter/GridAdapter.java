package com.androidfragmant.tourxyz.banglatourism.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidfragmant.tourxyz.banglatourism.R;
import com.androidfragmant.tourxyz.banglatourism.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * @author Ripon
 */
public class GridAdapter extends BaseAdapter {
    private Activity context;
    private String[] web;
    private String[] picname;

    public GridAdapter(Activity paramActivity, String[] paramArrayOfString, String[] pics) {
        this.context = paramActivity;
        this.web = paramArrayOfString;
        this.picname = pics;
    }

    @Override
    public int getCount() {
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View localView = convertView;
        if (localView == null) {
            localView = this.context.getLayoutInflater().inflate(R.layout.griditem, parent, false);
        }
        TextView localTextView = (TextView) localView.findViewById(R.id.textdescription);
        ImageView localImageView = (ImageView) localView.findViewById(R.id.picture);
        localTextView.setText(this.web[position]);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SOLAIMAN_LIPI_FONT);
        localTextView.setTypeface(tf);
        Picasso.with(context).load( this.picname[position] ).placeholder(R.drawable.noimage).into(localImageView);
        return localView;
    }
}
