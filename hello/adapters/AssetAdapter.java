package com.helloants.mm.helloants1.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;
import com.helloants.mm.helloants1.fragment.wm.BSFragment;

import java.util.ArrayList;
import java.util.Date;


public class AssetAdapter extends BaseAdapter {
    // 문자열을 보관 할 ArrayList
    private ArrayList<BSType> m_List;
    private int tagIndex;
    private String tag;
    private String left;
    private String right;
    private String where;
    private Date date;
    private String part;
    private String type;
    private String price;


    // 생성자
    public AssetAdapter() {
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

    // 현재 아이템의 수를 리턴
    @Override
    public int getCount() {
        return m_List.size();
    }

    // 현재 아이템의 오브젝트를 리턴, Object를 상황에 맞게 변경하거나 리턴받은 오브젝트를 캐스팅해서 사용
    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    // 아이템 position의 ID 값 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 출력 될 아이템 관리
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewGroup aparent = parent;

        if (convertView == null) {
            // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_bs_list, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(R.id.txv_name_list);
        String name = m_List.get(position).getName();
        text.setText(name.substring(0,name.length()-1));

        TextView values = (TextView) convertView.findViewById(R.id.txv_price_list);
        values.setText(String.format("%,d", m_List.get(position).getValue()));

        final View vv = convertView;

        //리스트 아이템을 터치 했을 때 이벤트 발생
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_firstvalue_modify);
                dialog.setTitle("초기값 수정");

                final EditText AssetName = (EditText) dialog.findViewById(R.id.alert_firstvalue_title);
                String name = m_List.get(pos).getName();
                AssetName.setText(name.substring(0, name.length() - 1));
                final EditText AssetPrice = (EditText) dialog.findViewById(R.id.alert_firstvalue_content);
                AssetPrice.setText(String.valueOf(m_List.get(pos).getValue()));
                AssetPrice.requestFocus();
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
                        String strName = AssetName.getText().toString() + "+";
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

        //리스트 아이템을 길게 터치 했을 떄 이벤트 발생
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                if (tag.equals("asset")) {
                    tagIndex = 0;
                    left = "상계처리+";
                    right = m_List.get(pos).getName();
                } else if (tag.equals("debt")) {
                    tagIndex = 1;
                    left = m_List.get(pos).getName();
                    right = "상계처리+";
                }
                where = "초기값 삭제";
                date = new Date();
                part = "first";
                type = "offset";


                price = String.valueOf(BSFragment.mPriceMap.get(m_List.get(pos).getName()));
                // 터치 시 해당 아이템 이름 출력
                new AlertDialog.Builder(context)
                        .setTitle("삭제")
                        .setMessage("삭제 하시겠습니까?.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //확인 버튼 눌렀을때
                                BsDB.INSTANCE.newIsInsert(price, where, date, type, part, left, right);
                                BsItem.INSTANCE.removeOne(m_List.get(pos).getName(), tagIndex);
                                Snackbar.make(v, "삭제했습니다.", Snackbar.LENGTH_SHORT).show();
                                remove(pos);
                                notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });

        return convertView;
    }

    // 외부에서 아이템 추가 요청 시 사용
    public void add(String _msg) {
        m_List.add(new BSType(_msg));
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        m_List.remove(_position);
    }


}
