package com.helloants.mm.helloants1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.data.type.BSType;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;


public class debtAdapter extends BaseAdapter {
    private ArrayList<BSType> mList;

    public debtAdapter() {
        mList = new ArrayList<BSType>();
    }

    @Override
    public int getCount() {
        return mList.size();
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
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_firstview_list2, parent, false);

//            // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
//            convertView.setOnLongClickListener(new OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    // 터치 시 해당 아이템 이름 출력
//                    Toast.makeText(context, "리스트 롱 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
        }
        TextView AssetName = (TextView) convertView.findViewById(R.id.txv_name_fvlist);
        TextView AssetPrice = (TextView) convertView.findViewById(R.id.txv_price_fvlist);
        String name = mList.get(pos).getName();
        AssetName.setText(name.substring(0, name.length() - 1));
        AssetPrice.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", mList.get(pos).getValue()));
        if (mList.size() - pos == 1) {
            AssetName.setTypeface(null, Typeface.BOLD);
            AssetPrice.setTypeface(null, Typeface.BOLD);
            AssetName.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
            AssetPrice.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Medium);
            AssetName.setTextColor(Color.parseColor("#FF0066"));
            AssetPrice.setTextColor(Color.parseColor("#FF0066"));

        }
        return convertView;
    }


    public void setList(ArrayList<BSType> List) {
        this.mList = List;
    }

}
