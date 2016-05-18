package com.helloants.mm.helloants1.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;


/**
 * Created by park on 2016-04-27.
 */
public class IsDateViewHolder extends RecyclerView.ViewHolder {
    public TextView txtDate;
    public TextView txtSum;

    public IsDateViewHolder(View itemView) {
        super(itemView);

        txtDate = (TextView) itemView.findViewById(R.id.txv_date_is_item_list);
        txtSum = (TextView) itemView.findViewById(R.id.txv_sum_is_item_list);
    }
}
