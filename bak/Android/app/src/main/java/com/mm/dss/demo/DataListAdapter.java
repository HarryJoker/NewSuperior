package com.mm.dss.demo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import gov.android.com.superior.R;
import java.util.Calendar;

/**
 * Copyright © 2017 Dahua Technology. All rights reserved.
 *
 * @Prject: Trunk_workspace
 * @Package: com.mm.dss.device
 * @Description: ${TODO}
 * @Author 26499
 * @DateTime 2017/2/20 19:06
 */

public class DataListAdapter extends RecyclerView.Adapter{
    private Context context;
    private boolean[] mDataSet;
    private int dayCount;
    private OnItemClickLinstener onItemClickLinstener;

    public DataListAdapter(Context context){
        this.context = context;
    }

    public void setDataSet(boolean[] mDataSet, Calendar calendar){
        this.mDataSet = mDataSet;
        this.dayCount = getCurrentMonthLastDay(calendar);
    }

    public static int getCurrentMonthLastDay(Calendar calendar)
    {
        Calendar a = (Calendar)calendar.clone();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天 // set the date to the first day of the month.
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天// the date is rolled back one day, the last day.
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public void setOnItemClickLinstener(OnItemClickLinstener onItemClickLinstener){
        this.onItemClickLinstener = onItemClickLinstener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dss_data_list_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        boolean mark = false;
        if(position < mDataSet.length) {
            mark = mDataSet[position];
        }

        viewHolder.dayTime.setText(String.valueOf(position + 1));
        if(mark) {
            viewHolder.dayTime.setTextColor(Color.BLUE);
        } else {
            viewHolder.dayTime.setTextColor(Color.BLACK);
        }
        viewHolder.dayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLinstener.onItemClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dayCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView dayTime;

        public ViewHolder(View itemView) {
            super(itemView);
            dayTime = (TextView) itemView.findViewById(R.id.dayTime);
        }
    }

    public interface OnItemClickLinstener{
        public void onItemClick(int position);
    }
}
