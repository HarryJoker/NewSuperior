package gov.android.com.superior.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class Messageragment extends BaseFragment {

    private PullToRefreshListView refreshListView;

    private MessageAdapter messageAdapter = new MessageAdapter();

    public Messageragment() {
    }

    public static Messageragment newInstance() {
        return new Messageragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messageragment, container, false);

        refreshListView = (PullToRefreshListView) view.findViewById(R.id.refreshListView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshListView.setOnRefreshListener(refreshListener);
        refreshListView.setAdapter(messageAdapter);
        refreshListView.setOnItemClickListener(itemClickListener);

        refreshListView.setRefreshing();
    }

    private void asyncGetAllTask() {
        OkGo.<List>get(Config.MESSAGE_GET_ALL + "/" + User.getInstance().get("unitId")).tag(this).execute(jsonCallback);
    }

    private JsonCallback<List> jsonCallback = new JsonCallback<List>() {

        @Override
        public void onError(Response<List> response) {
            super.onError(response);
            refreshListView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<List> response) {
            List data = response.body();

            Logger.d(data);

            messageAdapter.updateMessages(data);

            messageAdapter.notifyDataSetChanged();

            refreshListView.onRefreshComplete();

        }
    };

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetAllTask();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.d("MessageFragment onActivityResult --------------------> ");

        refreshListView.setRefreshing();

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            intent.putExtra("messageId", messageAdapter.getItemId(i-1));
            startActivityForResult(intent, 0x00F1);
        }
    };

    class MessageAdapter extends BaseAdapter {

        private List<Map<String,Object>> messages = new ArrayList<>();

        public void updateMessages(List<Map<String, Object>> list) {
            if (list != null) messages.clear();
            messages.addAll(list);
        }

        @Override
        public int getCount() {
            return messages.size() == 0 ? 1 : messages.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return messages.size() == 0 ? null : messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return messages.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (messages.size() == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText("暂无通知消息");
                return view;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_message, null);
                Map<String, Object> message = getItem(i);
                ((TextView) view.findViewById(R.id.tv_title)).setText(message.get("title").toString());
                ((TextView) view.findViewById(R.id.tv_content)).setText(message.get("content").toString());
                int read = Integer.parseInt(message.get("read").toString());
                ((ImageView) view.findViewById(R.id.iv_state)).setBackgroundResource(read == 0 ? R.mipmap.mail_unread : R.mipmap.mail_read);
                return view;
            }
        }
    }
}
