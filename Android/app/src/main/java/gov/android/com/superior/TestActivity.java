package gov.android.com.superior;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gov.android.com.superior.config.Config;
import gov.android.com.superior.entity.MyTabEntity;
import gov.android.com.superior.entity.TabEntity;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.DashboardFragment;
import gov.android.com.superior.ui.unit.masses.MassesFragment;
import gov.android.com.superior.ui.unit.person.PersonFragment;
import gov.android.com.superior.view.RadarMarkerView;

public class TestActivity extends BaseToolBarActivity {

    private RadarChart chart;


    private ArrayList<CustomTabEntity> mTabEntitys = new ArrayList<CustomTabEntity>() {
        {
            add(new TabEntity("工作台", R.mipmap.ic_navigate_dashboard_selected, R.mipmap.ic_navigate_dashboard_unselect));
            add(new TabEntity("群众督", R.mipmap.ic_navigate_masses_selected, R.mipmap.ic_navigate_masses_unselect));
            add(new TabEntity("我的", R.mipmap.ic_navigate_user_selected, R.mipmap.ic_navigate_user_unselect));
        }
    };

    private List<Fragment> fragments = new ArrayList<Fragment>() {
        {
            add(new DashboardFragment());
            add(new MassesFragment());
            add(new PersonFragment());
        }
    };

    List<String> activitys = new ArrayList<>();

    @Override
    public void onInitParams() {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_test;
    }

    @Override
    protected void onFindViews() {
        chart = findViewById(R.id.chart1);
        CommonTabLayout commonTabLayout = findViewById(R.id.common_tablayout);
        commonTabLayout.setTabData(mTabEntitys);

        commonTabLayout.setCurrentTab(0);
    }

    @Override
    public void onInitView() {
        chart.setBackgroundColor(Color.rgb(60, 65, 82));

//        initChatWithData(null);
    }

    private void initChatWithData(JSONObject data) {
        chart.getDescription().setEnabled(false);

        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_markerview);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        makeChatData(data);

        chart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return activitys.get((int) value % activitys.size());
//                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = chart.getYAxis();
//        yAxis.setTypeface(tfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(80f);
        l.setTextColor(Color.WHITE);
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestScore();
    }

    private void requestScore() {
        showLoading();
        OkGo.<JSONObject>post(HttpUrl.UNIT_CATEGORY_SCORE + "/" + 1 + "/" + 151).tag(this).execute(getJsonObjectCallback(HttpUrl.USER_LOGIN));
//        OkGo.<List<JSONObject>>post(HttpUrl.UNIT_SCORE + "/" + 1 + "/" + 151).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_SCORE));

    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
    }

    @Override
    protected void  onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        initChatWithData(data);
    }

    private void makeChatData(JSONObject data) {
        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (String key : data.keySet()) {
            entries.add(new RadarEntry(data.getFloatValue(key)));
            activitys.add(Config.scoreActivitys.get(key));
        }
        JokerLog.d(Arrays.deepToString(activitys.toArray()));
        JokerLog.d(Arrays.deepToString(entries.toArray()));

//        float mul = 80;
//        float min = 20;
//        int cnt = 5;
//
//        ArrayList<RadarEntry> entries1 = new ArrayList<>();
//        ArrayList<RadarEntry> entries2 = new ArrayList<>();
//
//        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
//        // the chart.
//        for (int i = 0; i < cnt; i++) {
//            float val1 = (float) (Math.random() * mul) + min;
//            entries1.add(new RadarEntry(val1));
//
//            float val2 = (float) (Math.random() * mul) + min;
//            entries2.add(new RadarEntry(val2));
//        }

        RadarDataSet set1 = new RadarDataSet(entries, "任务类型");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

//        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
//        set2.setColor(Color.rgb(121, 162, 175));
//        set2.setFillColor(Color.rgb(121, 162, 175));
//        set2.setDrawFilled(true);
//        set2.setFillAlpha(180);
//        set2.setLineWidth(2f);
//        set2.setDrawHighlightCircleEnabled(true);
//        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> radarDataSets = new ArrayList<>();
        radarDataSets.add(set1);
//        radarDataSets.add(set2);
        RadarData radarData = new RadarData(radarDataSets);
        radarData.setValueTextSize(8f);
        radarData.setDrawValues(false);
        radarData.setValueTextColor(Color.WHITE);
        chart.setData(radarData);
        chart.invalidate();
    }

}
