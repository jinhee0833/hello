package com.helloants.mm.helloants1.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;


/**
 * Created by kingherb on 2016-04-13.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder  {
    public TextView title;
    public ImageView imageView;
    public RecyclerViewHolder(View itemView) {
        super(itemView);

        this.title = (TextView) itemView.findViewById(R.id.subTitle);
        this.imageView = (ImageView) itemView.findViewById(R.id.content_image);
    }
}