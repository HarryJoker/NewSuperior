package gov.android.com.superior.gather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;

public class GatherFragment extends BaseFragment {


    private int unitId;

    private String unitName;

    private BarChart barChart;

    public GatherFragment() {

    }

    public static GatherFragment newInstance(int id, String name) {
        GatherFragment gatherFragment = new GatherFragment();
        Bundle arg = new Bundle();
        arg.putInt("unitId", id);
        arg.putString("unitName", name);
        gatherFragment.setArguments(arg);
        return gatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.unitId = getArguments().getInt("unitId", 0);
        this.unitName = getArguments().getString("unitName", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gather, container, false);

        barChart = view.findViewById(R.id.barchart);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        barChart.setOnChartValueSelectedListener(valueSelectedListener);

        asycGetUnitGather();

    }

    final String[] quarters = new String[] { "J1", "J2", "J3", "J4", "J5", "J6", "J7" };

    private IAxisValueFormatter formatter = new IAxisValueFormatter() {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return quarters[(int) value];
        }
    };


    private void asycGetUnitGather() {
        OkGo.<List<Map>>get(Config.GATHER_UNIT + "/" + unitId).tag(this).execute(jsonCallback);
    }

    private JsonCallback<List<Map>> jsonCallback = new JsonCallback<List<Map>>() {
        @Override
        public void onSuccess(Response<List<Map>> response) {
            invlidateChat(response.body());
        }
    };

    private void invlidateChat(List<Map> maps) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {

            if (i >= maps.size()) {
                entries.add(new BarEntry(i, 100f));
            } else {
                Map map = maps.get(i);
                Logger.d(i + "  " + map);
                float score = 100.0f - Float.parseFloat(map.get("cutScore").toString());
                entries.add(new BarEntry(i, score));
            }
        }

        BarDataSet set = new BarDataSet(entries, unitName == null ? "" : unitName);

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); //设置自定义条形宽度
        barChart.setData(data);
        barChart.setFitBars(true); //使x轴完全适合所有条形

        barChart.setNoDataText("生成统计图表中...");

        barChart.getDescription().setEnabled(true);
        Description description = new Description();
        description.setText("工作统计");
        description.setTextColor(Color.RED);

        //设置右下角的描述文字
        barChart.setDescription(description);

        barChart.setDrawGridBackground(false);

        //得到X轴，设置X轴的样式
        XAxis xAxis = barChart.getXAxis();
        //设置位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置特定的标签类型
//        xAxis.setTypeface(mTfLight);
        //设置是否绘制网格线
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        //设置最小的区间，避免标签的迅速增多
//        xAxis.setGranularity(5f); // only intervals of 1 day
        //设置进入时的标签数量
        xAxis.setLabelCount(7);
//        xAxis.setDrawLabels(true);

        xAxis.setAxisLineWidth(2f);

        //设置数据格式,         //X轴的数据格式
        xAxis.setValueFormatter(formatter);

        YAxis yAxis = barChart.getAxisLeft();
//        yAxis.setDrawZeroLine(true);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setLabelCount(10, false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        barChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格

        //设置注解的位置在左下方
        barChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        barChart.invalidate(); // refresh
    }


    private OnChartValueSelectedListener valueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            int category = ((int)e.getX()) + 1;
            Intent intent = new Intent(getActivity(), GatherDetailActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("unitId", unitId);
            startActivity(intent);
        }

        @Override
        public void onNothingSelected() {
            Logger.d("onNothingSelected");
        }
    };

}
