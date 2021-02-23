package gov.android.com.superior.ui.unit.person.message;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class Messageragment extends BaseRecylceFragment {

    public Messageragment() {
    }

    public static Messageragment newInstance() {
        return new Messageragment();
    }

    @Override
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_1), Color.GRAY));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        asyncGetAllTask();
    }

    private void asyncGetAllTask() {
        OkGo.<List<JSONObject>>get(HttpUrl.MESSAGE_GET_ALL + "/" + User.getInstance().get("unitId")).tag(this).execute(getJsonArrayCallback(HttpUrl.MESSAGE_GET_ALL));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        mSmartRefreshLayout.finishRefresh();
        setDataSource(data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.d("MessageFragment onActivityResult --------------------> ");

        mSmartRefreshLayout.autoRefresh();

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(getLayoutInflater().inflate(R.layout.rc_item_message, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("messageId", getRecyclerAdapter().getJsonObject(position).getLongValue("id"));
        startActivityForResult(intent, 0x00F1);
    }

    class MessageViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        TextView title;
        TextView content;
        ImageView icon;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            icon = itemView.findViewById(R.id.iv_state);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
           title.setText(data.getString("title"));
            content.setText(data.getString("content"));
            int read = data.getIntValue("read");
            icon.setImageResource(read == 0 ? R.mipmap.mail_unread : R.mipmap.mail_read);
        }
    }

//    class MessageAdapter extends BaseAdapter {
//
//        private List<Map<String, Object>> messages = new ArrayList<>();
//
//        public void updateMessages(List<Map<String, Object>> list) {
//            if (list != null) messages.clear();
//            messages.addAll(list);
//        }
//
//        @Override
//        public int getCount() {
//            return messages.size() == 0 ? 1 : messages.size();
//        }
//
//        @Override
//        public Map<String, Object> getItem(int i) {
//            return messages.size() == 0 ? null : messages.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return messages.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            if (messages.size() == 0) {
//                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
//                ((TextView)view).setText("暂无通知消息");
//                return view;
//            } else {
//                view = getActivity().getLayoutInflater().inflate(R.layout.item_message, null);
//                Map<String, Object> message = getItem(i);
//                ((TextView) view.findViewById(R.id.tv_title)).setText(message.get("title").toString());
//                ((TextView) view.findViewById(R.id.tv_content)).setText(message.get("content").toString());
//                int read = Integer.parseInt(message.get("read").toString());
//                ((ImageView) view.findViewById(R.id.iv_state)).setBackgroundResource(read == 0 ? R.mipmap.mail_unread : R.mipmap.mail_read);
//                return view;
//            }
//        }
//    }
}
