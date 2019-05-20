package com.vuanhlevis.cropimage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.vuanhlevis.cropimage.R;
import com.vuanhlevis.cropimage.widget.SquareImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private ArrayList<String> arrFilePath;
    private Context context;

    public GridAdapter(Context context, ArrayList<String> arrFilePath) {
        this.arrFilePath = arrFilePath;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrFilePath.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_gridview, null);
        }

//        SquareImageView imageView = view.findViewById(R.id.iv_item_gridview);
//
//        Picasso.get().load(arrFilePath.get(position))
//                .placeholder(R.drawable.noimg)
//                .error(R.drawable.noimg)
//                .into(imageView);

        SquareImageView imageView = view.findViewById(R.id.iv_item_gridview);
        RequestOptions options1 = new RequestOptions();
        options1.placeholder(R.drawable.noimg);
        Glide.with(context).load(arrFilePath.get(position))
                .thumbnail(0.5f)
                .apply(options1)
                .into(imageView);


        return view;
    }
}
