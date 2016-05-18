package com.helloants.mm.helloants1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.SpinerAdapter;
import com.helloants.mm.helloants1.adapters.SpinerAdapter2;
import com.helloants.mm.helloants1.data.type.ISType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ModifyDelete extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    Date mDate;
    String mRight;
    String mLeft;
    String mtype;
    String mPart;
    String mPrice;
    String mWhere;
    ArrayList[] mList;
    ArrayList mAssetList;
    ArrayList mDebtList;
    String mPhoneNum;
    String mCardName;
    int dis;
    private EditText editPrice;
    private EditText editWhere;
    private DatePicker mDatePick;
    private TimePicker mTimePick;
    private Button modify;
    private Spinner sp;
    private Spinner sp2;
    private ArrayList beforeList;
    private ArrayList resultList;
    private TextView mChoiceTxv;
    TextView mNewTxv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_delete);

        //툴바 타이틀 텍스트뷰
        TextView txvTitle = (TextView) findViewById(R.id.txv_title_md);
        txvTitle.setText("수정 & 삭제");

        //툴바 이미지 백 버튼
        ImageButton btnBack = (ImageButton) findViewById(R.id.img_btn_md);
        btnBack.setImageResource(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyDelete.this.onBackPressed();
            }
        });

        //데이터 받아오기
        Intent intent = getIntent();
        mDate = (Date) intent.getExtras().get("date");
        mRight = intent.getExtras().getString("right");
        mLeft = intent.getExtras().getString("left");
        mPart = intent.getExtras().getString("part");
        mtype = intent.getExtras().getString("type");
        mPrice = intent.getExtras().getString("price");
        mWhere = intent.getExtras().getString("where");
        mPhoneNum = intent.getExtras().getString("phoneNum");
        mCardName = intent.getExtras().getString("cardName");
        //삭제시 변수에 영향안받게
        final Date dDate = mDate;
        final String dRight = mRight;
        final String dLeft = mLeft;
        final String dPart = mPart;
        final String dType = mtype;

        beforeList = new ArrayList();
        ISType beforeType = new ISType();
        beforeType.mPart = mPart;
        beforeType.mRight = mRight;
        beforeType.mLeft = mLeft;
        beforeType.mDate = mDate;
        beforeType.mType = mtype;
        beforeType.mPrice = mPrice;
        beforeType.mWhere = mWhere;
        beforeType.mPhoneNum = mPhoneNum;
        beforeType.mCardName = mCardName;
        beforeList.add(beforeType);

        //삭제하기
        Button delete = (Button) findViewById(R.id.btn_delete_modifydelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BsDB.INSTANCE.isRemove(dDate, dLeft, dRight, dType, dPart);
                Intent intent = new Intent(ModifyDelete.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //수정하기
        editPrice = (EditText) findViewById(R.id.edit_price_modifydelete);
        editPrice.setText(mPrice);

        editWhere = (EditText) findViewById(R.id.edit_where_modifydelete);
        editWhere.setText(mWhere);

        //데이트앤 타임
        mDatePick = (DatePicker) findViewById(R.id.datepick_isdate_modifydelete);
        mDatePick.updateDate(Integer.parseInt(new SimpleDateFormat("yyyy").format(mDate)), Integer.parseInt(new SimpleDateFormat("MM").format(mDate)) - 1, Integer.parseInt(new SimpleDateFormat("dd").format(mDate)));
        mTimePick = (TimePicker) findViewById(R.id.timepick_isdate_modifydelete);
        mTimePick.setCurrentHour(Integer.parseInt(new SimpleDateFormat("HH").format(mDate)));
        mTimePick.setCurrentMinute(Integer.parseInt(new SimpleDateFormat("mm").format(mDate)));

        modify = (Button) findViewById(R.id.btn_modify_modifydelete);
        mChoiceTxv = (TextView) findViewById(R.id.txv_choice_modifydelete);
        mNewTxv = (TextView) findViewById(R.id.txv_new_modifydelete);
        //일단 뉴버튼으로 만들어진 새항목을 보여줄 텍스트뷰 안보이게
        mNewTxv.setVisibility(View.GONE);

        //첫번째 스피너
        sp = (Spinner) findViewById(R.id.spiner_left_modifydelete);
        //두번째 스피너
        sp2 = (Spinner) findViewById(R.id.spiner_right_modifydelete);
        populateSpinners();


        //첫번째 스피너 클릭될때 실행될 메소드
        sp.setOnItemSelectedListener(spinSelectedlistener);
        //두번째 스피너 클릭될때 실행될 메소드
        sp2.setOnItemSelectedListener(this);
    }

    private void populateSpinners() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("돈 벌었어요.(월급 제외)");
        list.add("현금을 썼어요.");
        list.add("돈 빌렸어요.(대출포함)");
        list.add("돈 갚았어요.(대출포함)");
        list.add("돈 빌려줬어요.");
        list.add("빌려준 돈 받았어요.");
        list.add("집을 샀어요. Or 보증금 올렸어요.");
        list.add("저축 했어요.(적금, 펀드, 주식, 보험 등)");
        list.add("자산을 팔았어요.");
        list.add("체크카드를 썼어요");
        list.add("신용카드를 썼어요");

        SpinerAdapter fAdapter = new SpinerAdapter();
        fAdapter.setList(list);
        sp.setAdapter(fAdapter);

        //원래 거래타입 초기값으로 셋팅해주기
        switch (mtype) {
            case "income":
                sp.setSelection(0);
                break;
            case "cashExpend":
                sp.setSelection(1);
                break;
            case "loan":
                sp.setSelection(2);
                break;
            case "repay":
                sp.setSelection(3);
                break;
            case "lend":
                sp.setSelection(4);
                break;
            case "receiveLend":
                sp.setSelection(5);
                break;
            case "house":
                sp.setSelection(6);
                break;
            case "save":
                sp.setSelection(7);
                break;
            case "sell":
                sp.setSelection(8);
                break;
            case "check":
                sp.setSelection(9);
                break;
            case "credit":
                sp.setSelection(10);
                break;
        }
    }

    private void populateSubSpinners(int itemNum) {
        //DB에서 자산 부채 항목들 가져오기
        mList = BsItem.INSTANCE.BsItemFind();
        mAssetList = mList[0]; //자산항목리스트
        mDebtList = mList[1];//부채항목리스트

        switch (itemNum) {
            case 0:
            case 7:
                resultList = mAssetList;
                resultList.add("NEW+");
                break;
            case 1:
            case 6:
            case 8:
            case 9:
                resultList = mAssetList;
                break;
            case 2:
            case 4:
                resultList = mDebtList;
                resultList.add("NEW+");
                break;
            case 3:
            case 5:
            case 10:
                resultList = mDebtList;
                break;
        }
        SpinerAdapter fAdapter = new SpinerAdapter2();
        fAdapter.setList(resultList);
        sp2.setAdapter(fAdapter);

        //원래 거래에서 찍혀있었던 내용 초기값으로 선택해주기
        String left = mLeft.substring(0,mLeft.length()-1);
        String right = mRight.substring(0,mRight.length()-1);
        for (int i = 0; i < resultList.size(); i++) {
            String tempResult = String.valueOf(resultList.get(i));
            String result = tempResult.substring(0,tempResult.length()-1);
            if (left.equals(result) || right.equals(result)) {
                sp2.setSelection(i);
                fAdapter.notifyDataSetChanged();
            }
        }
    }

    //첫번째 스피너 선택하면 일어나는 일
    private AdapterView.OnItemSelectedListener spinSelectedlistener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mNewTxv.setVisibility(View.GONE);
            sp2.setVisibility(View.VISIBLE);
            dis = position;
            switch (position) {
                case 0:
                    mtype = "income";
                    mRight = "수입+";
                    mChoiceTxv.setText("2. 어떤 자산이 늘어났습니까?\n새로운 자산이 늘었다면 NEW");
                    populateSubSpinners(0);
                    break;
                case 1:
                    mtype = "cashExpend";
                    mLeft = "지출+";
                    mChoiceTxv.setText("2. 어떤 자산으로 소비하셨습니까?");
                    populateSubSpinners(1);
                    break;
                case 2:
                    mtype = "loan";
                    mLeft = "현금+";
                    mChoiceTxv.setText("2. 어떤 부채가 늘어났습니까?\n새로운 부채가 늘었다면 NEW");
                    populateSubSpinners(2);
                    break;
                case 3:
                    mtype = "repay";
                    mRight = "현금-";
                    mChoiceTxv.setText("2. 어떤 부채를 갚았습니까?");
                    populateSubSpinners(3);
                    break;
                case 4:
                    mtype = "lend";
                    mRight = "현금-";
                    mChoiceTxv.setText("2. 누구에게 빌려줬습니까?\n새로운 사람이면 NEW");
                    populateSubSpinners(4);
                    break;
                case 5:
                    mtype = "receiveLend";
                    mLeft = "현금+";
                    mChoiceTxv.setText("2. 누구에게 돌려 받았습니까?");
                    populateSubSpinners(5);
                    break;
                case 6:
                    mtype = "house";
                    mLeft = "집값 혹은 보증금+";
                    mChoiceTxv.setText("2. 어떤 자산으로 구입하셨습니까?");
                    populateSubSpinners(6);
                    break;
                case 7:
                    mtype = "save";
                    mRight = "현금-";
                    mChoiceTxv.setText("2. 어디에 저축(펀드,주식,보험,적금)하셨습니까?\n새로운 저축은 NEW");
                    populateSubSpinners(7);
                    break;
                case 8:
                    mtype = "sell";
                    mRight = "현금+";
                    mChoiceTxv.setText("2. 어떤 자산을 파셨습니까?");
                    populateSubSpinners(8);
                    break;
                case 9:
                    mtype = "check";
                    mLeft = "지출+";
                    mChoiceTxv.setText("2. 어떤 카드로 소비하셨습니까?");
                    populateSubSpinners(9);
                    break;
                case 10:
                    mtype = "credit";
                    mLeft = "지출+";
                    mChoiceTxv.setText("2. 어떤 카드로 소비하셨습니까?");
                    populateSubSpinners(10);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    //투번째 스피너 선택후 일어나는일
    @Override
    public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
        if (parent.getItemAtPosition(position).equals("NEW")) {
            final EditText AssetName = new EditText(ModifyDelete.this);
            new AlertDialog.Builder(ModifyDelete.this)
                    .setTitle("항목 추가")
                    .setMessage("새로운 항목을 추가해 주세요.")
                    .setView(AssetName)
                    .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //추가 버튼 눌렀을때
                            String name = AssetName.getText().toString();
                            switch (dis) {
                                case 0:
                                case 4:
                                case 7:
                                    mLeft = name + "+";
                                    break;
                                case 2:
                                    mRight = name + "+";
                                    break;

                            }
                            sp2.setVisibility(View.GONE);
                            mNewTxv.setVisibility(View.VISIBLE);
                            mNewTxv.setText(name);
                        }
                    }).show();
        } else {
            String tempName = (String) parent.getItemAtPosition(position);
            String name = tempName.substring(0,tempName.length()-1);
            switch (dis) {
                case 0:
                case 4:
                case 7:
                    mLeft = name + "+";
                    break;
                case 1:
                case 5:
                case 6:
                case 9:
                    mRight = name + "-";
                    break;
                case 3:
                case 8:
                    mLeft = name + "-";
                    break;
                case 2:
                case 10:
                    mRight = name + "+";
                    break;
            }
        }

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrice = editPrice.getText().toString();
                mWhere = editWhere.getText().toString();
                mDate = new Date(mDatePick.getYear() - 1900, mDatePick.getMonth(), mDatePick.getDayOfMonth(), mTimePick.getCurrentHour(), mTimePick.getCurrentMinute());

                if (parent.getItemAtPosition(position).equals("NEW+")) {
                    if (dis == 0 || dis == 4 || dis == 7) {
                        //assetList에 넣어라
                        BsItem.INSTANCE.insertOne(mLeft, 0);
                    } else if (dis == 2) {
                        //debtList에 넣어라
                        BsItem.INSTANCE.insertOne(mRight, 1);
                    }

                }

                //수정된 걸 받아서 에프터리스트에 넣고 넘김
                ArrayList afterList = new ArrayList();
                ISType afterType = new ISType();
                afterType.mPart = mPart;
                afterType.mRight = mRight;
                afterType.mLeft = mLeft;
                afterType.mDate = mDate;
                afterType.mType = mtype;
                afterType.mPrice = mPrice;
                afterType.mWhere = mWhere;
                afterType.mCardName = mCardName;
                afterType.mPhoneNum = mPhoneNum;
                afterList.add(afterType);

                BsDB.INSTANCE.isModify(beforeList, afterList);
                Intent intent = new Intent(ModifyDelete.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
}