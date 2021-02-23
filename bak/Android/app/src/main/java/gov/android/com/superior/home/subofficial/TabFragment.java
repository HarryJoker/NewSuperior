package gov.android.com.superior.home.subofficial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseBannerFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.unit.task.TaskListActivity;
import gov.android.com.superior.http.Config;

/**
 *
 * 政府工作报告
 *
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends BaseBannerFragment {

    private int category;

    private LinearLayout layout_gather;
    private LinearLayout layout_detail;

    private TextView tv_name;

    private TextView tv_gather;

    private TextView tv_red;
    private TextView tv_yellow;
    private TextView tv_zi;
    private TextView tv_green;

    private TextView tv_report;
    private TextView tv_all;

    private GridView gv_sequence;

    private SequenceAdapter sequenceAdapter;

    private PullToRefreshScrollView refreshScrollView;

    public TabFragment() {

    }

    public static TabFragment newInstance(int category) {
        TabFragment tabFragment =  new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    private View.OnClickListener progressTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Logger.d("v tag" + v.getTag());

            int progress = Integer.parseInt(v.getTag() == null ? "0" : v.getTag().toString());
            if (progress <= 0 || progress > 4) return;
            Intent intent = new Intent(getActivity(), TaskListActivity.class);
            intent.putExtra("unitId", Integer.parseInt(User.getInstance().get("unitId").toString()));
            intent.putExtra("progress", Integer.parseInt(v.getTag().toString()));
            startActivity(intent);

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("category", 0);
    }

    private void asyncGetTaskCount() {
        OkGo.<Map>get(Config.TASK_COUNT + "/" + User.getInstance().get("unitId") + "/" + category).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<Map> response) {
            refreshView(response.body());
            refreshScrollView.onRefreshComplete();
        }
    };

    private void refreshReportGather(List<Map<String,String>> stateCounts) {
        if (stateCounts == null) return;
        tv_green.setText("0");
        tv_zi.setText("0");
        tv_yellow.setText("0");
        tv_red.setText("0");

        for (Map<String, String> stateCount : stateCounts) {
            int progress = Integer.parseInt(stateCount.get("progress"));
            //1：完成，2：快速，3：正常，4：缓慢
            switch (progress) {
                case 1:
                    tv_green.setText(stateCount.get("count"));
                    tv_green.setTag("1");
                    break;
                case 2:
                    tv_zi.setText(stateCount.get("count"));
                    tv_green.setTag("2");
                    break;
                case 3:
                    tv_yellow.setText(stateCount.get("count"));
                    tv_yellow.setTag("3");
                    break;
                case 4:
                    tv_red.setText(stateCount.get("count"));
                    tv_red.setTag("4");
                    break;
            }
        }
    }

    private void refreshType(List<Map<String,String>> types) {
        if (types == null) return;

        for (Map<String, String> type : types) {
            int childType = Integer.parseInt(type.get("childType"));
            //1：人大，2：政协
            switch (childType) {
                case 1:
                    ((TextView)getView().findViewById(R.id.tv_rd_count)).setText(type.get("count"));
                    break;
                case 2:
                    ((TextView)getView().findViewById(R.id.tv_zx_count)).setText(type.get("count"));
                    break;
            }
        }
    }

    private void refreshView(Map<String, Object> data) {

        String sumCount = ((Map)data.get("sumCount")).get("count").toString();
        sumCount = Integer.parseInt(sumCount) > 225 ? "225" : sumCount;
        SpannableString spannableString = new SpannableString("任务统计 " + sumCount + " 项");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 5, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_gather.setText(spannableString);

        if (category == 1) {
            refreshReportGather((List<Map<String,String>>) data.get("stateCount"));
        } else if (category == 2){
            getView().findViewById(R.id.layout_group).setVisibility(View.GONE);
            GridView gv_sequence = getView().findViewById(R.id.gv_sequence);
            gv_sequence.setVisibility(View.VISIBLE);
            sequenceAdapter.update((List) data.get("sequence"));
            sequenceAdapter.notifyDataSetChanged();
        } else if (category == 3) {
            refreshType((List<Map<String,String>>) data.get("type"));
        }

        Map<String, String> reportCount = (Map<String, String>) data.get("reportCount");
        spannableString = new SpannableString("需审阅的任务\n" + reportCount.get("reportCount") + "个待审阅");
        //0.7f表示默认字体大小的0.7倍
        spannableString.setSpan(new RelativeSizeSpan(0.7f), 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_report.setText(spannableString);

        spannableString = new SpannableString("分管的任务\n" + sumCount + "个任务");
        //0.7f表示默认字体大小的0.7倍
        spannableString.setSpan(new RelativeSizeSpan(0.7f), 5, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_all.setText(spannableString);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fxz_tab, container, false);

        refreshScrollView = view.findViewById(R.id.pull_refresh_scrollview);

        layout_gather = view.findViewById(R.id.layout_gather);
        layout_detail = view.findViewById(R.id.layout_detail);

        if (category == 2) {
            view.findViewById(R.id.layout_group).setVisibility(View.GONE);
            gv_sequence = view.findViewById(R.id.gv_sequence);
            gv_sequence.setVisibility(View.VISIBLE);
            sequenceAdapter = new SequenceAdapter();
            gv_sequence.setAdapter(sequenceAdapter);
        }

        if (category == 3) {
            view.findViewById(R.id.layout_group).setVisibility(View.GONE);
            gv_sequence = view.findViewById(R.id.gv_sequence);
            gv_sequence.setVisibility(View.GONE);
            view.findViewById(R.id.layout_type).setVisibility(View.VISIBLE);
        }
        if (category > 3) {
            view.findViewById(R.id.layout_group).setVisibility(View.GONE);
            view.findViewById(R.id.gv_sequence).setVisibility(View.GONE);
            view.findViewById(R.id.layout_type).setVisibility(View.GONE);
        }

        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(User.getInstance().get("duty") + "（" + User.getInstance().get("name") + "）");

        tv_gather = view.findViewById(R.id.tv_gather);

        tv_red = view.findViewById(R.id.tv_red);
        tv_red.setOnClickListener(progressTask);
        tv_yellow = view.findViewById(R.id.tv_yellow);
        tv_yellow.setOnClickListener(progressTask);
        tv_zi = view.findViewById(R.id.tv_zi);
        tv_zi.setOnClickListener(progressTask);
        tv_green = view.findViewById(R.id.tv_green);
        tv_green.setOnClickListener(progressTask);

        tv_all = view.findViewById(R.id.tv_all);
        tv_report = view.findViewById(R.id.tv_report);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_all.setOnClickListener(clickListener);
        tv_report.setOnClickListener(clickListener);
        layout_gather.setOnClickListener(clickListener);
        layout_detail.setOnClickListener(clickListener);

        refreshScrollView.setOnRefreshListener(refreshListener);

        startBanner((Banner) getView().findViewById(R.id.banner));

        asyncGetTaskCount();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        asyncGetTaskCount();
    }

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetTaskCount();
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.layout_detail:
                case R.id.layout_gather:
                    Intent intent = new Intent(getActivity(), ReportActivity.class);
//                    intent.putExtra("taskCount", Eutils.map2Bundle(taskCount));
                    intent.putExtra("curCategory", category);
                    startActivity(intent);
                    break;
                case R.id.tv_report:
                    intent = new Intent(getActivity(), TaskListActivity.class);
                    intent.putExtra("curCategory", category);
                    intent.putExtra("leaderId", User.getInstance().getUserId());
                    startActivity(intent);
                    break;
                case R.id.tv_all:
                    intent = new Intent(getActivity(), TaskListActivity.class);
                    intent.putExtra("curCategory", category);
                    intent.putExtra("unitId", Integer.parseInt(User.getInstance().get("unitId").toString()));
                    startActivity(intent);
                    break;
            }
        }
    };


    class SequenceAdapter extends BaseAdapter {

        private List<Map> sequences = new ArrayList();

        public void update(List list) {
            if (list != null) sequences.clear();
            sequences.addAll(list);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public String getItem(int i) {
            if (sequences.size() == 0) {
                return "0";
            } else {
                for (Map sequence : sequences) {
                    if (Integer.parseInt(sequence.get("sequence").toString()) - 1 == i)
                    {
                        return sequence.get("count").toString();
                    }
                }
            }
            return "0";
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getActivity().getLayoutInflater().inflate(R.layout.item_squence, viewGroup ,false);
            ((AvatarImageView)view.findViewById(R.id.lv_sequence)).setTextAndColor((i+1) + "", Color.parseColor("#f8f8ff"));
            ((TextView)view.findViewById(R.id.tv_sequence)).setText(getItem(i));
            return view;
        }
    }

}
