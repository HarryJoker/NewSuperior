package gov.android.com.superior.gather;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.view.NestExpandableListView;

public class GatherDetailActivity extends BaseLoadActivity {

    private int unitId;

    private int category;

    private ExpandableListView expandableListView;

    private GradeParentExpandAdapter gradeParentExpandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_detail);

        setTitle("扣分详情");

        unitId = getIntent().getIntExtra("unitId", 0);
        category = getIntent().getIntExtra("category", 0);

        expandableListView = (ExpandableListView) findViewById(R.id.elv_grade);

        expandableListView.setOnGroupExpandListener(onGroupExpandListener);

        asyncTasks();

    }

    private void asyncTasks() {
        showProgress("加载中");
        OkGo.<List>get(Config.GATHER_DETAIL + "/" + unitId + "/" + category).tag(this).execute(jsonCallback);

    }

    private JsonCallback<List> jsonCallback = new JsonCallback<List>() {
        @Override
        public void onError(Response<List> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<List> response) {
            gradeParentExpandAdapter = new GradeParentExpandAdapter(response.body());
            expandableListView.setAdapter(gradeParentExpandAdapter);
            removeProgress();
        }
    };

    private ExpandableListView.OnGroupExpandListener onGroupExpandListener = new ExpandableListView.OnGroupExpandListener() {
        @Override
        public void onGroupExpand(int groupPosition) {
            if (gradeParentExpandAdapter != null) {
                for (int i = 0; i < gradeParentExpandAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        }
    };


    private class GradeParentExpandAdapter extends BaseExpandableListAdapter {

        private List tasks = new ArrayList();

        public GradeParentExpandAdapter(List list) {
            if (list != null && list.size() > 0) tasks.clear();
            tasks.addAll(list);
        }

        @Override
        public int getGroupCount() {
            return tasks.size() == 0 ? 1 : tasks.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return tasks.size() == 0 ? 0 : 1;
        }

        @Override
        public Object getGroup(int i) {
            return tasks.size() == 0 ? null : tasks.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return ((List)((Map)tasks.get(i)).get("traces")).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_expand, viewGroup, false);

            TextView textView = layout.findViewById(R.id.text);

            textView.setPadding(40, 20, 0, 20);
            textView.setTextSize(16);

            textView.setTextColor(Color.parseColor("#222222"));

            String text = tasks.size() == 0 ? "未承接【" + SuperiorApplicaiton.titles[category - 1] + "】工作" : ((Map)getGroup(i)).get("name").toString();

            textView.setText(text);
            return layout;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            List traces = (List) ((Map)getGroup(i)).get("traces");
            if (traces.size() == 0) {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_expand, viewGroup, false);
                TextView textView = layout.findViewById(R.id.text);

                textView.setPadding(40, 20, 0, 20);
                textView.setTextSize(14);

                textView.setTextColor(Color.parseColor("#333333"));

                textView.setText("还未报送工作");
                return layout;
            } else {

                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                NestExpandableListView expandView = new NestExpandableListView(GatherDetailActivity.this);

                expandView.setLayoutParams(layoutParams);

                // 加载班级的适配器
                GradeChildExpandAdapter adapter = new GradeChildExpandAdapter(traces);

                expandView.setAdapter(adapter);

                expandView.setPadding(20, 0, 0, 0);

                return expandView;
            }
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    private class GradeChildExpandAdapter extends BaseExpandableListAdapter {

        private List traces = new ArrayList();

        public GradeChildExpandAdapter(List list) {
            if (list != null && list.size() > 0) traces.clear();
            this.traces.addAll(list);
        }

        @Override
        public int getGroupCount() {
            return traces.size();
        }

        @Override
        public int getChildrenCount(int i) {
            int size = ((List)((Map)traces.get(i)).get("grades")).size();
            return size == 0 ? 1 : size;
        }

        @Override
        public Object getGroup(int i) {
            return traces.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            int size = ((List)((Map)traces.get(i)).get("grades")).size();
            return size == 0 ? null : ((List)((Map)traces.get(i)).get("grades")).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_second, viewGroup, false);
            layout.setPadding(40, 20, 0, 20);

            TextView textView = layout.findViewById(R.id.text);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(Color.parseColor("#333333"));
            textView.setTextSize(14);
            textView.setText(((Map)getGroup(i)).get("content").toString());

            TextView tv_date = layout.findViewById(R.id.date);
            tv_date.setText("报送日期：" + ((Map)getGroup(i)).get("createtime").toString());
            return layout;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_expand, viewGroup, false);
            TextView textView = layout.findViewById(R.id.text);

            textView.setPadding(60, 20, 0, 20);
            textView.setTextSize(13);

            Map grade = ((Map)getChild(i, i1));
            String text = "";
            if (grade == null) {
                text = "此报送未扣分";
            } else {
                text = grade.get("name").toString();
                int category = Integer.parseInt(grade.get("category").toString());
                if (category == 6) {
                    text += "（加" + grade.get("grade").toString().replace("-", "") + "分）";
                } else {
                    text += "（扣" + grade.get("grade") + "分）";
                }
            }
            textView.setTextColor(Color.parseColor("#444444"));
            textView.setText(text);

            return layout;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

}
