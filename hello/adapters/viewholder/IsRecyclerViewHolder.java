package com.helloants.mm.helloants1.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;

/**
 * Created by park on 2016-04-21.
 */
public class IsRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView txtWhere;
    public TextView txtPrice;
    public TextView txtTime;
    public TextView txtRightDea;
    public ImageView imgIcon;
    public TextView txtLeftCha;

    public IsRecyclerViewHolder(View itemView) {
        super(itemView);

        imgIcon = (ImageView) itemView.findViewById(R.id.img_icon_isfrag);
        txtWhere = (TextView) itemView.findViewById(R.id.txv_where_isfrag);
        txtPrice = (TextView) itemView.findViewById(R.id.txv_price_isfrag);
        txtTime = (TextView) itemView.findViewById(R.id.txv_time_isfrag);
        txtLeftCha = (TextView) itemView.findViewById(R.id.txv_leftdea_isfrag);
        txtRightDea = (TextView) itemView.findViewById(R.id.txv_rightcha_isfrag);
    }
}