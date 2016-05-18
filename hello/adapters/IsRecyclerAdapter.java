package com.helloants.mm.helloants1.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.ModifyDelete;
import com.helloants.mm.helloants1.adapters.viewholder.IsDateViewHolder;
import com.helloants.mm.helloants1.adapters.viewholder.IsHeaderViewHolder;
import com.helloants.mm.helloants1.adapters.viewholder.IsRecyclerViewHolder;
import com.helloants.mm.helloants1.data.type.ISType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by park on 2016-04-21.
 */
public class IsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_HEADER = 3;
    private final int VIEW_ITEM = 2;
    private final int VIEW_DATE = 1;
    private final int VIEW_NULL = 1;

    public ArrayList<ISType> mList;
    ViewGroup mViewGroup;
    RecyclerView mRecyclerView;
    Context mContext;
    public int size;

    public IsRecyclerAdapter(Context context, ArrayList list, RecyclerView view) {
        mContext = context;
        mList = list;
        mRecyclerView = view;
    }

    @Override
    public int getItemViewType(int position) {
        ISType type = mList.get(position);

        if(type == null) return VIEW_NULL;
        else if(type.mType.equals("header")) return VIEW_HEADER;
        else if(type.mType.equals("date")) return VIEW_DATE;
        else return VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType == VIEW_HEADER) {
            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_is_header, parent, false);
            mViewGroup = mainGroup;
            mainGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder = new IsHeaderViewHolder(mainGroup);
        } else if (viewType == VIEW_ITEM) {
            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_is_list, parent, false);
            mViewGroup = mainGroup;
            mainGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder = new IsRecyclerViewHolder(mainGroup);
            mainGroup.setOnClickListener(new MyOnClickListener());
        } else {
            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_is_date, parent, false);
            mViewGroup = mainGroup;
            mainGroup.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder = new IsDateViewHolder(mainGroup);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ISType type = mList.get(position);
        if( holder instanceof IsHeaderViewHolder) {
            ((IsHeaderViewHolder) holder).mYear.setText(type.mPhoneNum);
            ((IsHeaderViewHolder) holder).mMonth.setText(type.mWhere);
            ((IsHeaderViewHolder) holder).mIncome.setText(type.mPrice);
            ((IsHeaderViewHolder) holder).mConsume.setText(type.mCardName);
        } else if (holder instanceof IsDateViewHolder) {
            ((IsDateViewHolder) holder).txtDate.setText(type.mWhere + "일");
            try {
                ((IsDateViewHolder) holder).txtSum.setText(String.format("%,d", Integer.parseInt(type.mPrice)) + "원");
            } catch (NumberFormatException e) {
                ((IsDateViewHolder) holder).txtSum.setText("0원");
            }
        } else if (holder instanceof IsRecyclerViewHolder) {
            ((IsRecyclerViewHolder) holder).txtLeftCha.setText(type.mLeft);
            ((IsRecyclerViewHolder) holder).txtWhere.setText(type.mWhere);
            try {
                ((IsRecyclerViewHolder) holder).txtPrice.setText(String.format("%,d", Integer.parseInt(type.mPrice)) + "원");
            } catch (NumberFormatException e) {
                ((IsRecyclerViewHolder) holder).txtPrice.setText("0원");
            }
            ((IsRecyclerViewHolder) holder).txtTime.setText(new SimpleDateFormat("a hh:mm").format(type.mDate));
            ((IsRecyclerViewHolder) holder).txtRightDea.setText(type.mRight);

            switch (type.mType) {
                case "credit":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_credit);
                    break;
                case "check":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_check);
                    break;
                case "income":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_income);
                    break;
                case "cashExpend":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_cash);
                    break;
                case "loan":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_loan);
                    break;
                case "repay":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_repay);
                    break;
                case "lend":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_lend);
                    break;
                case "receiveLend":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_receive);
                    break;
                case "house":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_house);
                    break;
                case "save":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_save);
                    break;
                case "sell":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_sell);
                    break;
                case "car":
                    ((IsRecyclerViewHolder) holder).imgIcon.setImageResource(R.drawable.ic_car);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mList == null) ? 0 : size;
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.getChildPosition(v);

            String part = mList.get(itemPosition).mPart;
            if (part == null) {
            } else if (part.equals("first")) {
                Snackbar.make(v, "초기값은 마이페이지에서 수정, 삭제해 주세요.", Snackbar.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(mContext, ModifyDelete.class);
                ISType type = mList.get(itemPosition);
                intent.putExtra("date", type.mDate);
                intent.putExtra("left", type.mLeft);
                intent.putExtra("right", type.mRight);
                intent.putExtra("part", type.mPart);
                intent.putExtra("type", type.mType);
                intent.putExtra("price", type.mPrice);
                intent.putExtra("where", type.mWhere);
                intent.putExtra("phoneNum", type.mPhoneNum);
                intent.putExtra("cardName", type.mCardName);

                mContext.startActivity(intent);
            }
        }
    }
}