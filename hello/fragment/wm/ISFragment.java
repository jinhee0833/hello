package com.helloants.mm.helloants1.fragment.wm;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.adapters.IsRecyclerAdapter;
import com.helloants.mm.helloants1.data.type.ISType;
import com.helloants.mm.helloants1.db.bs.BsDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ISFragment extends Fragment {
    View v, mHeader;
    String[] tabNames;
    TabLayout tabLayout;
    NestedScrollView mScrollView;
    RecyclerView mView;
    IsRecyclerAdapter mIsAdapter;
    Comparator<ISType> mComparator;
    int mCount;
    int tabNamesSize;
    int mDaySum;
    int mMonthIncomeSum;
    int mMonthSum;
    Map<String, ArrayList<ISType>> mDateMap;
    ArrayList<String> mDaySumList;
    ArrayList allList;
    ArrayList<String> mTabList;
    Calendar mCalendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_is, container, false);

        initLayout();
        initData();
        initTap(inflater);
        initRecycler();

        return v;
    }

    private void initRecycler() {
        mView.setNestedScrollingEnabled(false);
        mView.setHasFixedSize(true);
        mView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mView.setAdapter(mIsAdapter);
    }

    private void initLayout() {
        mScrollView = (NestedScrollView) v.findViewById(R.id.scroll_isfrag);
        mView = (RecyclerView) v.findViewById(R.id.rv_is_monthfrag);
        tabLayout = (TabLayout) v.findViewById(R.id.tab_tablayout_is);
        mIsAdapter = new IsRecyclerAdapter(getActivity(), allList, mView);
    }

    private void initTap(LayoutInflater inflater) {
        tabLayout.setOnTabSelectedListener(new MyTapListener());
        tabNamesSize = tabNames.length;
        for (int i = 0; i < tabNamesSize; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            RelativeLayout layout = (RelativeLayout)
                    LayoutInflater.from(getActivity()).inflate(R.layout.item_tablayout_is_fragment, tabLayout, false);
            TextView txv = (TextView) layout.findViewById(R.id.txv_item_tablayout_is);
            if(tabNames[i].length() == 2) tabNames[i] = " " + tabNames[i] + " ";
            txv.setText(tabNames[i]);
            tab.setCustomView(layout);
            tabLayout.addTab(tab);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(tabNamesSize - 1).select();
            }
        }, 100);
    }

    private void initData() {
        mDateMap = BsDB.INSTANCE.monthDataFind(getActivity());

        if (mDateMap == null) {
            mDateMap = new HashMap<String, ArrayList<ISType>>();
        }

        mCount = mDateMap.size();

        Iterator<String> iter = mDateMap.keySet().iterator();
        List list = new ArrayList();

        while (iter.hasNext()) {
            String str = iter.next();

            if (str.length() == 8) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.insert(6, 0);
                str = sb.toString();
            }
            list.add(str);
        }

        Collections.sort(list);
        iter = list.iterator();

        int i = 0;
        mTabList = new ArrayList();
        while (iter.hasNext()) {
            String str = iter.next();

            if (str.charAt(6) == '0') {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.deleteCharAt(6);
                str = sb.toString();
            }
            mTabList.add(str);
        }

        int size = mTabList.size();
        tabNames = new String[size];
        for (int count = 0; count < size; ++count) {
            tabNames[count] = mTabList.get(count).toString().split("년 ")[1];
        }

        mComparator = new Comparator<ISType>() {
            @Override
            public int compare(ISType lhs, ISType rhs) {
                Date d1;
                Date d2;

                d1 = lhs.mDate;
                d2 = rhs.mDate;

                return (d1.getTime() > d2.getTime() ? -1 : 1);
            }
        };
    }

    private class MyTapListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mScrollView.scrollTo(0, 0);

            String position = "";
            try {
                position = mTabList.get(tabLayout.getSelectedTabPosition());
            } catch (ArrayIndexOutOfBoundsException e) {}

            ArrayList<ISType> list = (mDateMap.get(position) == null) ?
                    new ArrayList<ISType>() :
                    mDateMap.get(position);

            Collections.sort(list, mComparator);

            mDaySumList = new ArrayList<String>();
            mDaySum = mMonthSum = mMonthIncomeSum = 0;
            int size = list.size();
            for (int j = 0; j < size; j++) {
                String type1 = list.get(j).mType;
                if (type1.equals("credit") || type1.equals("check") || type1.equals("cashExpend")) {
                    try {
                        mMonthSum += Integer.parseInt(list.get(j).mPrice);
                    } catch (Exception e) {}

                    try {
                        if (j == 0 || list.get(j).mDate.getDate() == list.get(j - 1).mDate.getDate()) {
                            mDaySum += Integer.parseInt(list.get(j).mPrice);
                        } else {
                            mDaySumList.add(String.valueOf(mDaySum));
                            mDaySum = 0;
                            mDaySum += Integer.parseInt(list.get(j).mPrice);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        mDaySum += Integer.parseInt(list.get(j).mPrice);
                    } catch (NumberFormatException e) {}
                } else if (type1.equals("income")) {
                    try {
                        mMonthIncomeSum += Integer.parseInt(list.get(j).mPrice);
                    } catch (Exception e) {}
                }
            }
            mDaySumList.add(String.valueOf(mDaySum));

            mCalendar = Calendar.getInstance();
            int day;
            int preDay = 0;
            int count = 0;
            allList = new ArrayList<ISType>();
            for (int i = 0; i < size; ++i) {
                ISType type = list.get(i);
                String type1 = type.mType;

                mCalendar.setTime(type.mDate);
                day = mCalendar.get(Calendar.DATE);

                if (i != 0) {
                    mCalendar.setTime(list.get(i - 1).mDate);
                    preDay = mCalendar.get(Calendar.DATE);
                }

                if (type1.equals("credit") || type1.equals("check") || type1.equals("cashExpend")) {
                    if (i == 0) {
                        ISType t = new ISType();
                        String[] arr = position.split("년 ");
                        t.mPrice = String.format("%,d", mMonthSum) + "원";
                        t.mCardName = String.format("%,d", mMonthIncomeSum) + "원";
                        t.mPhoneNum = arr[0];
                        t.mWhere = arr[1];
                        t.mType = "header";
                        allList.add(t);

                        t = new ISType();
                        t.mPrice = mDaySumList.get(count++);
                        t.mWhere = String.valueOf(day);
                        t.mType = "date";
                        allList.add(t);
                    } else if (day != preDay) {
                        ISType t = new ISType();
                        t.mPrice = mDaySumList.get(count++);
                        t.mWhere = String.valueOf(day);
                        t.mType = "date";
                        allList.add(t);
                    }
                } else if (type1.equals("income")) {
                    if (i == 0) {
                        ISType t = new ISType();
                        String[] arr = position.split("년 ");
                        t.mPrice = String.format("%,d", mMonthSum) + "원";
                        t.mCardName = String.format("%,d", mMonthIncomeSum) + "원";
                        t.mPhoneNum = arr[0];
                        t.mWhere = arr[1];
                        t.mType = "header";
                        allList.add(t);

                        t = new ISType();
                        t.mPrice = mDaySumList.get(count++);
                        t.mWhere = String.valueOf(day);
                        t.mType = "date";
                        allList.add(t);
                    } else if (day != preDay) {
                        ISType t = new ISType();
                        t.mPrice = mDaySumList.get(count++);
                        t.mWhere = String.valueOf(day);
                        t.mType = "date";
                        allList.add(t);
                    }
                } else {
                    if (i == 0) {
                        ISType t = new ISType();
                        String[] arr = position.split("년 ");
                        t.mPrice = String.format("%,d", mMonthSum) + "원";
                        t.mCardName = String.format("%,d", mMonthIncomeSum) + "원";
                        t.mPhoneNum = arr[0];
                        t.mWhere = arr[1];
                        t.mType = "header";
                        allList.add(t);

                        t = new ISType();
                        t.mPrice = mDaySumList.get(count++);
                        t.mWhere = String.valueOf(day);
                        t.mType = "date";
                        allList.add(t);
                    }
                }
                allList.add(type);
            }

            mIsAdapter.mList = allList;
            mIsAdapter.size = allList.size();
            mIsAdapter.notifyDataSetChanged();
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}
        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    }
}