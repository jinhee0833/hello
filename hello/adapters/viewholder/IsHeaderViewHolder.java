package com.helloants.mm.helloants1.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;

/**
 * Created by park on 2016-05-02.
 */
public class IsHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView mYear;
    public TextView mMonth;
    public TextView mIncome;
    public TextView mConsume;

    public IsHeaderViewHolder(View itemView) {
        super(itemView);
        mYear = (TextView) itemView.findViewById(R.id.txv_year_is_header);
        mMonth = (TextView) itemView.findViewById(R.id.txv_month_is_header);
        mIncome = (TextView) itemView.findViewById(R.id.txv_income_data_is_header);
        mConsume = (TextView) itemView.findViewById(R.id.txv_consume_data_is_header);
    }
}
