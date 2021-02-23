package gov.android.com.superior.ui.unit.masses.score.radar;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.fragment.BaseContentFragment;
import com.first.orient.base.utils.JokerLog;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.view.RadarMarkerView;

/**
 * A simple {@link Fragment} subclass.
 *  部门的综合绩效
 */
public class UnitScoreRadarFragment extends BaseContentFragment {

    private int unitId;
    private int rank;

    private RadarChart chart;
    private RoundedImageView ivAvatar;
    private TextView tvUnitName;
    private TextView tvScoreLevel;
    private TextView tvScoreRank;


    private List<String> mActivitys = new ArrayList<>(Config.scoreActivitys.values());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            unitId = getArguments().getInt("unitId", 0);
            rank = getArguments().getInt("rank", 0);
        }
    }

    @Override
    public void onFindViews(View rootView) {
        chart = rootView.findViewById(R.id.chart1);
        ivAvatar = rootView.findViewById(R.id.iv_avatar);
        tvUnitName = rootView.findViewById(R.id.tv_unit_name);
        tvScoreLevel = rootView.findViewById(R.id.tv_score_level);
        tvScoreRank = rootView.findViewById(R.id.tv_score_rank);
    }

    @Override
    public void onInitViews() {
        UnitRadarActivity activity = (UnitRadarActivity)getActivity();
        Glide.with(this).load(HttpUrl.makeAttachmentUrl(activity.getUnitLogo())).placeholder(R.mipmap.ic_avatar).into(ivAvatar);
        tvUnitName.setText(activity.getUnitName());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_unit_score_radar;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestUnitScore();
    }

    private void requestUnitScore() {
        showLoading();
        OkGo.<JSONObject>post(HttpUrl.UNIT_SCORE + "/" + unitId).tag(this).execute(getJsonObjectCallback(HttpUrl.UNIT_SCORE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        tvScoreRank.setText(rank + "");
        initChatWithData(data);
    }

    private void initChatWithData(JSONObject data) {
        chart.getDescription().setEnabled(false);

        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(30);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(getActivity(), R.layout.radar_markerview);
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
                JokerLog.d("value:" + value + ", xLabel:" +  mActivitys.get((int) value % mActivitys.size()));
                return mActivitys.get((int) value % mActivitys.size());
//                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);

        YAxis yAxis = chart.getYAxis();
//        yAxis.setTypeface(tfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
//        yAxis.setAxisMaximum(110f);
        yAxis.setDrawLabels(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(50f);
        l.setTextColor(Color.BLACK);
    }

    private Map<String, Integer[]> mCategoryColors = new HashMap<String, Integer[]>() {
        {
//            put("1", new Integer[]{103, 110, 129});
//            put("2", new Integer[]{43, 110, 129});
//            put("3", new Integer[]{103, 55, 100});
//            put("4", new Integer[]{103, 20, 189});
//            put("5", new Integer[]{103, 110, 11});
//            put("6", new Integer[]{103, 110, 90});
//            put("7", new Integer[]{103, 110, 200});
//            put("8", new Integer[]{55, 99, 129});
//            put("9", new Integer[]{55, 55, 55});
            put("1", new Integer[]{248, 141, 50});
            put("2", new Integer[]{109, 168, 255});
            put("3", new Integer[]{126, 205, 255});
            put("4", new Integer[]{255, 95, 71});
            put("5", new Integer[]{238, 130, 238});
            put("6", new Integer[]{103, 110, 90});
            put("7", new Integer[]{103, 110, 200});
            put("8", new Integer[]{55, 99, 129});
            put("9", new Integer[]{50, 205, 50});
        }
    };

    private int randomNum() {
        return new Random().nextInt(50);
    }

    private void makeChatData(JSONObject data) {

        ArrayList<IRadarDataSet> radarDataSets = new ArrayList<>();

        for (String key : data.keySet()) {
            ArrayList<RadarEntry> entries = new ArrayList<>();
            JSONObject score = data.getJSONObject(key);
            double synthesis = score.getDoubleValue("synthesis");
            JSONObject statusScores = score.getJSONObject("statusScores");
            entries.add(new RadarEntry(statusScores.getFloatValue("11") - randomNum()));
            entries.add(new RadarEntry(statusScores.getFloatValue("50") - randomNum()));
            entries.add(new RadarEntry(statusScores.getFloatValue("52") - randomNum()));
            entries.add(new RadarEntry(statusScores.getFloatValue("72") - randomNum()));
            entries.add(new RadarEntry(statusScores.getFloatValue("74") - randomNum()));
            RadarDataSet set = new RadarDataSet(entries, Config.categoryTitles.get(Integer.parseInt(key)));
            Integer[] colors = mCategoryColors.get(key);
            set.setColor(Color.rgb(colors[0], colors[1], colors[2]));
            set.setFillColor(Color.rgb(colors[0], colors[1], colors[2]));
            set.setDrawFilled(true);
            set.setFillAlpha(180);
            set.setLineWidth(2f);
            set.setDrawHighlightCircleEnabled(true);
            set.setDrawHighlightIndicators(false);

            radarDataSets.add(set);
        }
        JokerLog.d(Arrays.deepToString(radarDataSets.toArray()));
        JokerLog.d(Arrays.deepToString(mActivitys.toArray()));

        RadarData radarData = new RadarData(radarDataSets);
        radarData.setValueTextSize(8f);
        radarData.setDrawValues(false);
        radarData.setValueTextColor(Color.BLACK);
        chart.setData(radarData);
        chart.invalidate();
    }
}
