package gov.android.com.superior.home.supervise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harry.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;

public class TraceListFragment extends BaseFragment {


    public TraceListFragment() {

    }

    public static TraceListFragment newInstance(int category) {
        TraceListFragment fragment = new TraceListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    private int category = 0;

    private PullToRefreshListView taskListView;

    private TraceListAdapter traceListAdapter = new TraceListAdapter();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("category", 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trace_list, container, false);
        taskListView = view.findViewById(R.id.pull_refresh_listview);
        return view;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int position = i - 1;
            if (position < 0) return;
            Map<String, Object> trace = traceListAdapter.getItem(position);

            if (trace.get("id") == null || trace.get("taskId") == null) return;
            int traceId =  Integer.parseInt(trace.get("id").toString());
            int taskId = Integer.parseInt(trace.get("taskId").toString());

            Intent intent = new Intent(getActivity(), VerifyActivity.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("traceId", traceId);
            startActivityForResult(intent, 111);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            taskListView.setRefreshing(false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        taskListView.setOnItemClickListener(itemClickListener);
        taskListView.setAdapter(traceListAdapter);

        asyncGetAllTask();
    }

    private void asyncGetAllTask() {
        OkGo.<List>get(Config.TRACE_UNVIRIFY + "/" + category).tag(this).execute(jsonCallback);
    }

    private JsonCallback<List> jsonCallback = new JsonCallback<List>() {

        @Override
        public void onError(Response<List> response) {
            super.onError(response);
            taskListView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<List> response) {
            List data = response.body();

            Logger.d(data);

            Logger.d("category: " + category);

            traceListAdapter.updateTraces(data);

            traceListAdapter.notifyDataSetChanged();

        }
    };


    class TraceListAdapter extends BaseAdapter {

        private List<Map<String,Object>> traces = new ArrayList<>();

        public void updateTraces(List<Map<String, Object>> list) {
            if (list != null) traces.clear();
            traces.addAll(list);

            Logger.d(traces);
        }

        @Override
        public int getCount() {
            return traces.size() == 0 ? 1 : traces.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return traces.size() == 0 ? null : traces.get(i);
        }

        @Override
        public long getItemId(int i) {
            return traces.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (traces.size() == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText("暂无可审核的任务");
                return view;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_trace2, null);
                Map<String, Object> trace = getItem(i);

                ((TextView) view.findViewById(R.id.tv_time)).setText(trace.get("createtime").toString());
                ((TextView) view.findViewById(R.id.tv_trace)).setText(trace.get("content").toString());
                ((TextView) view.findViewById(R.id.tv_address)).setText(trace.get("address").toString());
                view.findViewById(R.id.layout_adrress).setVisibility(trace.get("address").toString().isEmpty() ? View.GONE : View.VISIBLE);

                if (category == 4) {
                    Object object = getItem(i).get("taskLabel");
                    String taskLabel = object == null ? "" : "【" + object.toString() + "】";
                    if (taskLabel.length() > 0) {
                        SpannableString spannableString = new SpannableString(taskLabel + trace.get("content").toString());
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
                        spannableString.setSpan(colorSpan, 0, taskLabel.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new RelativeSizeSpan(1f), 0, taskLabel.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ((TextView) view.findViewById(R.id.tv_trace)).setText(spannableString);
                    }
                }


                AvatarImageView imageView = view.findViewById(R.id.lv_logo);
                String logo = trace.get("logo").toString();

                if (TextUtils.isEmpty(logo)) {
                    String unitName = trace.get("unitName").toString();
                    imageView.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.parseColor("#f8f8ff"));
                } else {
                    Glide.with(getActivity())
                            .load(Config.ATTACHMENT + logo)
                            .centerCrop()
                            .crossFade()
                            .into(imageView);
                }

                bindAttchementView(trace, view);

            }
            return view;
        }

        private void bindAttchementView(Map<String, Object> trace, View view) {
            GridView gridView = ((GridView) view.findViewById(R.id.gv_attachment));
            if (!trace.get("attachment").toString().trim().isEmpty()) {
                List<String> attachments = Arrays.asList(trace.get("attachment").toString().split(","));
                gridView.setAdapter(new AttachmentAdapter(getActivity(), attachments, 70));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), TransitionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "transition");
                        intent.putExtra("url", Config.ATTACHMENT + "/" + adapterView.getAdapter().getItem(i));
                        startActivity(intent, options.toBundle());
                    }
                });
            } else {
                gridView.setVisibility(View.GONE);
            }
        }
    }
}
