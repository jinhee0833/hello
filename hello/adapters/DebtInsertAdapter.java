package com.helloants.mm.helloants1.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.data.type.BSType;

import java.util.ArrayList;

/**
 * Created by park on 2016-05-13.
 */
public class DebtInsertAdapter extends BaseAdapter {
    // 문자열을 보관 할 ArrayList
    private ArrayList<BSType> m_List;
    private int tagIndex;
    private String tag;

    // 생성자
    public DebtInsertAdapter() {
        m_List = new ArrayList<BSType>();
    }

    public void setList(ArrayList<BSType> list) {
        m_List = list;
    }
    public ArrayList<BSType> getList() {
        return m_List;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int getCount() {
        return m_List.size();
    }
    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewGroup aparent = parent;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_debt_list, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(R.id.txv_name_list);
        text.setText(m_List.get(position).getName());

        TextView values = (TextView) convertView.findViewById(R.id.txv_price_list);
        values.setText(String.format("%,d", m_List.get(position).getValue()));

        final View vv = convertView;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_firstvalue_modify);
                dialog.setTitle("초기값 수정");

                final EditText AssetName = (EditText) dialog.findViewById(R.id.alert_firstvalue_title);
                AssetName.setText(m_List.get(pos).getName());
                final EditText AssetPrice = (EditText) dialog.findViewById(R.id.alert_firstvalue_content);
                AssetPrice.setText(String.valueOf(m_List.get(pos).getValue()));
                Button btnInsert = (Button) dialog.findViewById(R.id.alert_firstvaluet_insert);
                Button btnCalcel = (Button) dialog.findViewById(R.id.alert_firstvalue_cancel);

                final Dialog DIALOG = dialog;

                btnCalcel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DIALOG.dismiss();
                    }
                });
                btnInsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strName = AssetName.getText().toString();
                        Long longPrice = Long.parseLong(AssetPrice.getText().toString());

                        if (strName.equals("") || longPrice == null) {
                            Snackbar.make(aparent, "내용을 입력해 주세요.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            //추가 버튼 눌렀을때
                            String name = strName;
                            long value = longPrice;

                            m_List.get(pos).setName(name);
                            m_List.get(pos).setValue(value);

                            TextView names = (TextView) vv.findViewById(R.id.txv_name_list);
                            TextView values = (TextView) vv.findViewById(R.id.txv_price_list);
                            names.setText(m_List.get(pos).getName());
                            values.setText(String.format("%,d", m_List.get(pos).getValue()));
                            DIALOG.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }

    public void add(String _msg) {
        m_List.add(new BSType(_msg));
    }
    public void remove(int _position) {
        m_List.remove(_position);
    }
}