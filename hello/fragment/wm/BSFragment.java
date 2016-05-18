package com.helloants.mm.helloants1.fragment.wm;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.MainActivity;
import com.helloants.mm.helloants1.adapters.bsAdapter;
import com.helloants.mm.helloants1.adapters.debtAdapter;
import com.helloants.mm.helloants1.data.DeviceSize;
import com.helloants.mm.helloants1.data.type.BSType;
import com.helloants.mm.helloants1.data.type.ISType;
import com.helloants.mm.helloants1.db.bs.BsDB;
import com.helloants.mm.helloants1.db.bs.BsItem;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BSFragment extends Fragment {
    View v;
    private ArrayList<BSType> mAssetList = new ArrayList<>();
    private ArrayList<BSType> mDebtList = new ArrayList<>();
    private TextView mAssetTxv;
    private TextView mDebtTxv;
    private TextView mEquityTxv;

    LinearLayout Debt;
    private ArrayList<ISType> mBSList;
    private Date mFirstDate;
    private String[] mAssetArray;
    private String[] mDebtArray;
    private int mAssetLeft;
    private int mAssetRight;
    private int mDebtLeft;
    private int mDebtRight;
    private int mDebtSum = 0;
    private int mAssetSum = 0;
    private TextView mTotalEquity;
    private TextView mTotalEquity2;
    private TextView mTotalEquity1;
    private TextView mTotalWon;
    private TextView mTotalWon1;
    private TextView mTotalWon2;
    private LinearLayout Equity;
    private RelativeLayout rel;
    private RelativeLayout rel1;
    private RelativeLayout rel2;
    private PieChart mChart;
    private String[] xData = { "", ""};

    private int weightDebtNum;
    private int weightEquityNum;
    private TextView mAssetText;
    private TextView mEquityText;
    private TextView mDebtText;
    private TextView mEquityText2;
    private TextView mDebtText2;
    private View rel_view;
    private View rel_view1;
    private View rel_view2;
    private long TotalAsset;
    private long TotalEquity;
    public static Map<String, Integer> mPriceMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bs, container, false);

        mPriceMap = new HashMap<>();
        //DB에서 이 이용자가 가지고있는 자산항목과 부채항목 가지고 오기
        Thread t = new Thread() {
            @Override
            public void run() {
                BsItem.INSTANCE.setArray();
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //디비에 있는 자산부채항목 가져와서 뿌리기(누적)
        final bsAdapter assetBsAdapter = new bsAdapter();
        final debtAdapter debtBsAdapter = new debtAdapter();

        //모든 거래내역 가져오기
        mBSList = BsDB.INSTANCE.initData();
        //초기값 입력한 날짜
        mFirstDate = BsDB.INSTANCE.firstDate();
        //BsItem에 들어있는 자산 부채 목록
        mAssetArray = BsItem.INSTANCE.getAssetArrayy();
        mDebtArray = BsItem.INSTANCE.getDebtArray();


        //앱을 설치하고 초기값 입력한 날짜 이후의 거래내역들이 반영되어야함. 걸러내서 리스트에 집어넣음
        ArrayList<ISType> compareList = new ArrayList();
        for (ISType i : mBSList) {
            if (i.mDate.equals(mFirstDate) || i.mDate.after(mFirstDate)) {
                ISType type = i;
                compareList.add(type);
            }
        }

        //자산리스트에 들어갈 데이터 세팅
        for (String j : mAssetArray) {
            for (ISType i : compareList) {
                String aR = i.mRight;
                String aL = i.mLeft;
                String afterStrRight = aR.substring(0, aR.length() - 1);
                String afterStrLeft = aL.substring(0, aL.length() - 1);
                String afterJ = j.substring(0,j.length() - 1);
                if (afterStrLeft.equals(afterJ)) {
                    try {
                        mAssetLeft += Integer.parseInt(i.mPrice);
                    } catch (NumberFormatException e) {}
                } else if (afterStrRight.equals(afterJ)) {
                    try {
                        mAssetRight += Integer.parseInt(i.mPrice);
                    } catch (NumberFormatException e) {}
                }
            }
            BSType bsType = new BSType();
            bsType.setName(j);
            int asset = mAssetLeft - mAssetRight;
            mPriceMap.put(j, asset);
            bsType.setValue(asset);
            mAssetSum += asset;
            mAssetList.add(bsType);
            mAssetRight = 0;
            mAssetLeft = 0;
        }

        //부채리스트에 들어갈 데이터 세팅
        for (String h : mDebtArray) {
            for (ISType i : compareList) {
                String aR = i.mRight;
                String aL = i.mLeft;
                String afterStrRight = aR.substring(0, aR.length() - 1);
                String afterStrLeft = aL.substring(0, aL.length() - 1);
                String afterH = h.substring(0,h.length() - 1);
                if (afterStrLeft.equals(afterH)) {
                    try {
                        mDebtLeft += Integer.parseInt(i.mPrice);
                    } catch (NumberFormatException e) {}
                } else if (afterStrRight.equals(afterH)) {
                    try {
                        mDebtRight += Integer.parseInt(i.mPrice);
                    } catch (NumberFormatException e) {}
                }

            }
            BSType bsType = new BSType();
            bsType.setName(h);
            int debt = mDebtRight - mDebtLeft;
            mPriceMap.put(h, debt);
            bsType.setValue(debt);
            mDebtSum += debt;
            mDebtList.add(bsType);
            mDebtLeft = 0;
            mDebtRight = 0;
        }


        //합계넣기
        BSType bsTypeAssetSum = new BSType();
        bsTypeAssetSum.setName("총자산+");
        bsTypeAssetSum.setValue(mAssetSum);
        mAssetSum = 0;
        mAssetList.add(bsTypeAssetSum);
        BSType bsTypeDebtSum = new BSType();
        bsTypeDebtSum.setName("빚+");
        bsTypeDebtSum.setValue(mDebtSum);
        mDebtSum = 0;
        mDebtList.add(bsTypeDebtSum);

        //자산 리스트뷰
        ListView assetListView = (ListView) v.findViewById(R.id.list_asset_bsfrag);
        assetBsAdapter.setList(mAssetList);
        assetListView.setAdapter(assetBsAdapter);

        //부채 리스트뷰
        ListView debtListView = (ListView) v.findViewById(R.id.list_debt_bsfrag);
        debtBsAdapter.setList(mDebtList);
        debtListView.setAdapter(debtBsAdapter);

        //자산리스트뷰 UI
        int numberOfItems = assetBsAdapter.getCount();
        int totalItemsHeight = 0;
        for (int g = 0; g < numberOfItems; g++) {
            View item = assetBsAdapter.getView(g, null, assetListView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }
        int totalDividersHeight = assetListView.getDividerHeight() * (numberOfItems - 1);
        ViewGroup.LayoutParams params = assetListView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        assetListView.setLayoutParams(params);
        assetListView.requestLayout();

        //부채리스트뷰 UI
        int numberOfItems2 = debtBsAdapter.getCount();
        int totalItemsHeight2 = 0;
        for(int g = 0; g < numberOfItems2; g++){
            View item = debtBsAdapter.getView(g, null,debtListView);
            item.measure(0,0);
            totalItemsHeight2 += item.getMeasuredHeight();
        }
        int totalDividersHeight2 =  debtListView.getDividerHeight() * (numberOfItems2-1);
        ViewGroup.LayoutParams params2 = debtListView.getLayoutParams();
        params2.height = totalItemsHeight2 + totalDividersHeight2;
        debtListView.setLayoutParams(params2);
        debtListView.requestLayout();

        //맨위에 총자산 대비 부채 내돈 %로 표현하기
        TotalAsset = mAssetList.get(mAssetList.size() - 1).getValue();
        long TotalDebt = mDebtList.get(mDebtList.size() - 1).getValue();
        TotalEquity = TotalAsset - TotalDebt;
        weightDebtNum = (int) Math.round((double) TotalDebt / (double) TotalAsset * 100);
        weightEquityNum = 100 - weightDebtNum;

        mAssetText = (TextView) v.findViewById(R.id.asset_text);
        mEquityText = (TextView) v.findViewById(R.id.equity_text);
        mDebtText = (TextView) v.findViewById(R.id.debt_text);
        mEquityText2 = (TextView) v.findViewById(R.id.equity_text2);
        mDebtText2 = (TextView) v.findViewById(R.id.debt_text2);


        mAssetText.setText("총자산  ::  " + String.format("%,d", TotalAsset) + "원");
        mAssetText.setTextColor(Color.parseColor("#0066CC"));

        mEquityText.setText("순자산");
        mEquityText.setTextColor(Color.parseColor("#00CC99"));
        mEquityText2.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", TotalEquity));

        mEquityText2.setTextColor(Color.parseColor("#00CC99"));
        mDebtText.setText("빚");
        mDebtText.setTextColor(Color.parseColor("#FF0066"));
        mDebtText2.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", TotalDebt));

        mDebtText2.setTextColor(Color.parseColor("#FF0066"));

        //파이차트 그리기
        mChart = (PieChart) v.findViewById(R.id.pie_chart);
        mChart.setMinimumWidth(DeviceSize.mWidth/3);
        mChart.setMinimumHeight(DeviceSize.mWidth/3);
        mChart.setHoleRadius(70);
        mChart.setDrawSliceText(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDescription("");
        mChart.setRotationEnabled(false);
        addData();

//        mAssetTxv = (TextView) v.findViewById(R.id.txv_allasset_bsfrag);
//        mAssetTxv.setText("총 자산 : " + String.format("%,d", TotalAsset) + "원");

//        mDebtTxv = (TextView) v.findViewById(R.id.txv_debt_bsfrag);
//        mDebtTxv.setText("부채 : " + weightDebtNum + "%");

//        mEquityTxv = (TextView) v.findViewById(R.id.txv_equity_bsfrag);
//        mEquityTxv.setText("내돈 : " + weightEquityNum + "%");

//        LinearLayout.LayoutParams weightDebt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, weightDebtNum);
//        Debt = (LinearLayout) v.findViewById(R.id.linear_debt_bsfrag);
//        Debt.setLayoutParams(weightDebt);

//        LinearLayout.LayoutParams weightEquity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, weightEquityNum);
//        Equity = (LinearLayout) v.findViewById(R.id.linear_equity_bsfrag);
//        Equity.setLayoutParams(weightEquity);




        //내돈영역 위치와 크기 계산해 넣기
        mTotalEquity = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag);
        mTotalEquity1 = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag1);
        mTotalEquity2 = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag2);
        mTotalWon = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag_1);
        mTotalWon1 = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag1_1);
        mTotalWon2 = (TextView) v.findViewById(R.id.txv_totalequity_bsfrag2_1);
        mTotalEquity.setText("순자산 : ");
        mTotalEquity1.setText("순자산 : ");
        mTotalEquity2.setText("순자산 : ");
        mTotalWon.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", TotalEquity));
        mTotalWon1.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", TotalEquity) );
        mTotalWon2.setText(Currency.getInstance(Locale.KOREA).getSymbol() + " " + String.format("%,d", TotalEquity));
        mTotalEquity.setTextColor(Color.parseColor("#00CC99"));
        mTotalEquity1.setTextColor(Color.parseColor("#00CC99"));
        mTotalEquity2.setTextColor(Color.parseColor("#00CC99"));
        mTotalWon.setTextColor(Color.parseColor("#00CC99"));
        mTotalWon1.setTextColor(Color.parseColor("#00CC99"));
        mTotalWon2.setTextColor(Color.parseColor("#00CC99"));
        rel = (RelativeLayout) v.findViewById(R.id.bs_relative);
        rel1 = (RelativeLayout) v.findViewById(R.id.bs_relative1);
        rel2 = (RelativeLayout) v.findViewById(R.id.bs_relative2);
        rel_view = v.findViewById(R.id.bs_relative_view);
        rel_view1 = v.findViewById(R.id.bs_relative1_view);
        rel_view2 = v.findViewById(R.id.bs_relative2_view);
        LinearLayout equityLayout = (LinearLayout) v.findViewById(R.id.equity_linear);
        LinearLayout debtLayout = (LinearLayout) v.findViewById(R.id.debt_liniear);


        if(params.height > params2.height){//자산항목이 많으면
            rel_view.setVisibility(View.GONE);
            rel_view1.setVisibility(View.GONE);
            rel2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTotalEquity.setVisibility(View.GONE);
            mTotalEquity1.setVisibility(View.GONE);
            mTotalWon.setVisibility(View.GONE);
            mTotalWon1.setVisibility(View.GONE);
        }else if(params.height < params2.height){//부채항목이 많으면
            rel_view.setVisibility(View.GONE);
            rel_view2.setVisibility(View.GONE);
            rel1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mTotalEquity.setVisibility(View.GONE);
            mTotalEquity2.setVisibility(View.GONE);
            mTotalWon.setVisibility(View.GONE);
            mTotalWon2.setVisibility(View.GONE);
        }else if(params.height == params2.height){//자산 부채 항목갯수가 같으면
            rel_view1.setVisibility(View.GONE);
            rel_view2.setVisibility(View.GONE);
            mTotalEquity2.setVisibility(View.GONE);
            mTotalEquity1.setVisibility(View.GONE);
            mTotalWon1.setVisibility(View.GONE);
            mTotalWon2.setVisibility(View.GONE);
        }

        //스크롤 맨위로
        final NestedScrollView scrollView = (NestedScrollView)v.findViewById(R.id.scroll_bsfrag);

        MainActivity activity = (MainActivity) getActivity();
        ViewPager vp = (ViewPager) activity.findViewById(R.id.viewpager);

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                BSType.setmCheck2(true);
                scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {

                        if (position == 0) {
                            BSType.setmCheck2(false);
                        }

                        if (position == 1) {
                            if (scrollView.getScrollY() != 0 && BSType.ismCheck2()) {
                                scrollView.scrollTo(0, 0);
                            }
                        }
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return v;
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        if(TotalEquity< 0){
            yVals1.add(new Entry(100,0));
            yVals1.add(new Entry(0,1));
        }else{
            yVals1.add(new Entry(weightDebtNum,0));
            yVals1.add(new Entry(weightEquityNum,1));
            Log.v("진희", String.valueOf(weightDebtNum));
            Log.v("진희2", String.valueOf(weightEquityNum));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] LIBERTY_COLORS = new int[]{ Color.parseColor("#70FF0066"),Color.parseColor("#7000CC99")};
        for (int c : LIBERTY_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAssetList.clear();
        mDebtList.clear();
    }
}
