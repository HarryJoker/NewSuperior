package gov.android.com.superior.home.unit;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseBannerFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.unit.task.TaskActivity;
import gov.android.com.superior.home.unit.task.TaskListActivity;
import gov.android.com.superior.home.unit.task.TaskTraceActivity;
import gov.android.com.superior.http.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseBannerFragment {

    private int category;

    private ListView unAcceptListView;

    private ListView acceptedListView;

    private TaskAdapter unAcceptAdapter = new TaskAdapter(2);

    private TaskAdapter acceptedAdapter = new TaskAdapter(5);

    private PullToRefreshScrollView refreshScrollView;

    public PageFragment() {

    }

    public static PageFragment newInstance(int category) {
        PageFragment pageFragment =  new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("category", 0);
    }

    private void asyncGetAllTask() {

        String url = Config.TASK_BY_UNIT_AND_CATEGORY + "/" + User.getInstance().get("unitId") + "/" + category;

        OkGo.<Map>get(url).tag(this).execute(jsonCallback);
    }

    private AdapterView.OnItemClickListener unAcceptItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (l > 0) {
                Intent intent = new Intent(getActivity(), TaskActivity.class);
                intent.putExtra("taskId", l);
                startActivityForResult(intent, 1);
            }
        }
    };

    private AdapterView.OnItemClickListener acceptedItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (l > 0) {
                Intent intent = new Intent(getActivity(), TaskTraceActivity.class);
//                intent.putExtra("taskId", l);
                List tasks = new ArrayList();
                tasks.add(acceptedAdapter.getItem(i));
                intent.putExtra("groupTasks", (Serializable) tasks);
                startActivity(intent);
            }
        }
    };

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<Map> response) {
            Map<String,Object> data = response.body();

            Object object = data.get("unAccept");
            if (object != null  && object instanceof List) {
                unAcceptAdapter.updateTasks((List<Map<String,Object>>) object);
            }

            object = data.get("accepted");
            if (object != null  && object instanceof List) {
                acceptedAdapter.updateTasks((List<Map<String,Object>>) object);
            }

            unAcceptAdapter.notifyDataSetChanged();
            acceptedAdapter.notifyDataSetChanged();

            refreshScrollView.onRefreshComplete();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logger.d(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_report, container, false);

        refreshScrollView = view.findViewById(R.id.pull_refresh_scrollview);

        unAcceptListView = view.findViewById(R.id.lv_unAccept);

        acceptedListView = view.findViewById(R.id.lv_accepted);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        refreshScrollView.setRefreshing();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unAcceptListView.setAdapter(unAcceptAdapter);
        acceptedListView.setAdapter(acceptedAdapter);
        unAcceptListView.setOnItemClickListener(unAcceptItemClickListener);
        acceptedListView.setOnItemClickListener(acceptedItemClickListener);

        getView().findViewById(R.id.layout_accept_section).setOnClickListener(sectionLayoutClick);
        getView().findViewById(R.id.layout_unAccept_section).setOnClickListener(sectionLayoutClick);

        startBanner((Banner) getView().findViewById(R.id.banner));

        refreshScrollView.setOnRefreshListener(refreshListener);

        asyncGetAllTask();

    }

    private View.OnClickListener sectionLayoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getActivity(), TaskListActivity.class);
            if (view.getId() == R.id.layout_accept_section) {
                intent.putExtra("model", TaskListActivity.MODEL_ACCEPTED);
            } else if (view.getId() == R.id.layout_unAccept_section) {
                intent.putExtra("model", TaskListActivity.MODEL_UNACCEPT);
            }
            startActivity(intent);

        }
    };

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetAllTask();
        }
    };

    class TaskAdapter extends BaseAdapter {

        private int displayCount = 2;

        public TaskAdapter(int count)
        {
            this.displayCount = count;
        }

        private List<Map<String,Object>> tasks = new ArrayList<>();

        public void updateTasks(List<Map<String, Object>> list) {
            if (list != null) tasks.clear();
            tasks.addAll(list);
        }

        @Override
        public int getCount() {
            return tasks.size() == 0 ? 1 : tasks.size() > displayCount ? displayCount : tasks.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return tasks.size() == 0 ? null : tasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return tasks.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (tasks.size() == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText("暂无任务");
                return view;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_task, null);
                ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());

                if (category == 4) {
                    Object object = getItem(i).get("taskLabel");
                    String taskLabel = object == null ? "" : "【" + object.toString() + "】";
                    ((TextView) view.findViewById(R.id.tv_title)).setText(taskLabel + getItem(i).get("name").toString());
                }

                if (category > 2) {
                    view.findViewById(R.id.iv_logo).setVisibility(View.GONE);
                } else {
                    if (category == 1) {
                        int progress = Integer.parseInt(getItem(i).get("progress").toString());
                        ((ImageView) view.findViewById(R.id.iv_logo)).setImageResource(SuperiorApplicaiton.taskStates[progress]);
                    } else if (category == 2) {
                        view.findViewById(R.id.iv_logo).setVisibility(View.GONE);
                        AvatarImageView avatarImageView = ((AvatarImageView) view.findViewById(R.id.iv_logo2));
                        avatarImageView.setVisibility(View.VISIBLE);
                        avatarImageView.setTextAndColor(getItem(i).get("sequence").toString(), Color.parseColor("#f8f8ff"));

                    }
                }
                return view;
            }
        }
    }
}
