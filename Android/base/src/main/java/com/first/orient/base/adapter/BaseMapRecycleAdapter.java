package com.first.orient.base.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseMapRecylceActivity;
import com.first.orient.base.utils.JokerLog;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Author: harryjoker
 * Created on: 2020-01-17 18:29
 * Description:
 */
public class BaseMapRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<MetaBean> mMetaBeans;

    private Map<Integer, HolderMeta> mViewHolderMetas;

    private JSONObject mDataSource = new JSONObject();

    public BaseMapRecycleAdapter(Context context, List<MetaBean> beans, Map<Integer, HolderMeta> holders) {
        this.mContext = context;
        this.mMetaBeans = beans;
        this.mViewHolderMetas = holders;
    }

    public BaseMapRecycleAdapter(Context context, List<MetaBean> beans, Map<Integer, HolderMeta> holders, JSONObject dataSource) {
        this(context, beans, holders);
        this.mDataSource.putAll(dataSource);
    }

    public void setDataSource(JSONObject dataSource) {
        this.mDataSource.clear();
        if (dataSource != null) {
            this.mDataSource.putAll(dataSource);
        }
        notifyDataSetChanged();
    }

    public void updateDataSource(String key, String value, int position) {
        if (key == null || value == null) return;
        mDataSource.put(key, value);
        if (position >= 0 && position < mDataSource.size()) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        this.mDataSource.clear();
        notifyDataSetChanged();
    }

    public String getValue(int position) {
        return mDataSource.getString(mMetaBeans.get(position).getKey());
    }

    public Object getObjectByKey(String key) {
        if (mDataSource == null || !mDataSource.containsKey(key)) return "";
        return mDataSource.get(key);
    }

    public JSONObject getMetaValue() {
        JSONObject data = new JSONObject();
        for (MetaBean meta : mMetaBeans) {
            data.put(meta.getKey(), mDataSource.containsKey(meta.getKey()) ? mDataSource.getString(meta.getKey()) : "");
        }
        return data;
    }


    public String getMetaFields() {
        StringBuilder builder = new StringBuilder();
        for (MetaBean meta : mMetaBeans) {
//            data.put(meta.getKey(), mDataSource.containsKey(meta.getKey()) ? mDataSource.getString(meta.getKey()) : "");
            builder.append(builder.length() > 0 ? "," : "");
            builder.append("'" + meta.getKey() + "'");
        }
        return builder.toString();
    }

    public String getMetaValues() {
        StringBuilder builder = new StringBuilder();
        for (MetaBean meta : mMetaBeans) {
            builder.append(builder.length() > 0 ? "," : "");
            builder.append("'" +  getMetaValue(meta.getKey()) + "'");
        }
        return builder.toString();
    }

    public String getMetaValue(String key) {
        if (TextUtils.isEmpty(key)) return "";
        String value = mDataSource.containsKey(key) ? mDataSource.getString(key) : "";
        if (onMetaValueChangeCallback != null) {
            value = onMetaValueChangeCallback.onMetaValue(key, value);
        }
        return value;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMetaBeans == null || mMetaBeans.size() <= 0) return  -1;
        return mMetaBeans.get(position).viewType;
    }

    @Override
    public int getItemCount() {
        return mMetaBeans == null ? 0 : mMetaBeans.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!mViewHolderMetas.containsKey(viewType)) {
            throw new NullPointerException("View type can not match Holder....");
        }
        RecyclerView.ViewHolder viewHolder = null;
        try {
            View view = LayoutInflater.from(mContext).inflate(mViewHolderMetas.get(viewType).getLayoutRes(), parent, false);
            Constructor<RecyclerView.ViewHolder> constructor = mViewHolderMetas.get(viewType).getClazz().getConstructor(View.class);
            viewHolder = constructor.newInstance(view);

            onInitViewHolder(viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (viewHolder == null) {
            throw new NullPointerException("Can not refect ViewHolder....");
        }
        initItemClickListener(viewHolder);
        return viewHolder;
    }

    protected void onInitViewHolder(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder != null && holder instanceof BaseViewHolder) {
            BaseViewHolder myHolder = ((BaseViewHolder)holder);
            MetaBean metaBean = mMetaBeans.get(position);
            myHolder.bindViewHolderForTitle(position, metaBean.key, metaBean);

           Object content = mDataSource.get(metaBean.getKey());
           myHolder.bindViewHolderForValue(position, metaBean.key, content == null ? "" : content);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        JokerLog.e("RecycleAdapter onViewDetachedFromWindow..............");
    }

    /**
     * @param baseViewHolder
     */
    private void initItemClickListener(final RecyclerView.ViewHolder baseViewHolder) {
        if (onRecyclerItemClickListener != null) {
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("MapBaseAdapter", "onItemClick:" + baseViewHolder.getAdapterPosition() + ", " + v + " ------------------------------>");
                    onRecyclerItemClickListener.onItemClick(v, baseViewHolder.getLayoutPosition());
                }
            });
        }
    }

    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    /**
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }


    private OnMetaValueChangeCallback onMetaValueChangeCallback;

    public void setOnMetaValueChangeCallback(OnMetaValueChangeCallback onMetaValueChangeCallback) {
        this.onMetaValueChangeCallback = onMetaValueChangeCallback;
    }

    public static class HolderMeta {
        private int viewType;
        private Class clazz;
        private int layoutRes;

        public HolderMeta(int viewType, Class clazz, int res) {
            this.viewType = viewType;
            this.clazz = clazz;
            this.layoutRes = res;
        }

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public int getLayoutRes() {
            return layoutRes;
        }

        public void setLayoutRes(int layoutRes) {
            this.layoutRes = layoutRes;
        }
    }


    public static class MetaBean {
        private String key;
        private String title;
        private int viewType;

        public MetaBean(String key, String title, int type) {
            this.key = key;
            this.title = title;
            this.viewType = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getViewType() {
            return viewType;
        }
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bindViewHolderForValue(int position, String key, Object content);

        public abstract void bindViewHolderForTitle(int position, String key, MetaBean metaBean);
    }

    public interface OnMetaValueChangeCallback {
        String onMetaValue(String key, String value);
    }

}
