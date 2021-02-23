package com.first.orient.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: harryjoker
 * Created on: 2019-10-28 12:38
 * Description:
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final String TAG = getClass().getSimpleName();

    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private OnRecyclerItemLongClickListener onRecyclerItemLongClickListener;
    private OnRecyclerItemCheckedChangeListener mOnRecyclerItemCheckedChangeListener;
    private OnRecyclerItemChildClikListener onRecyclerItemChildClikListener;

    public BaseRecyclerAdapter(Context context) {
        mContext = context;
    }

    public BaseRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        if (list != null && list.size() > 0) {
            mDataList.addAll(list);
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public T getItemData(int position) {
        return position < mDataList.size() ? mDataList.get(position) : null;
    }

    public JSONObject getJsonObject(int position) {
        Object object = getItemData(position);
        if (object == null) object = new Object();
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    public int getDataCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    /**
     * 移除某一条记录
     *
     * @param position 移除数据的position
     */
    public void removeItem(int position) {
        if (position < mDataList.size()) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setDataList(List<T> list) {
        this.mDataList.clear();
        if (list != null && list.size() > 0) {
            this.mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 添加一条记录
     *
     * @param data     需要加入的数据结构
     * @param position 插入位置
     */
    public void append(T data, int position) {
        if (position <= mDataList.size()) {
            mDataList.add(position, data);
            notifyItemInserted(position);
        }
    }

    /**
     * 添加一条记录
     *
     * @param data 需要加入的数据结构
     */
    public void append(T data) {
        append(data, mDataList.size());
    }

    /**
     * 移除所有记录
     */
    public void clearAllItem() {
        int size = mDataList.size();
        if (size > 0) {
            mDataList.clear();
            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        mDataList.clear();
    }

    /**
     * 批量添加记录
     *
     * @param data     需要加入的数据结构
     * @param position 插入位置
     */
    public void append(List<T> data, int position) {
        if (position <= mDataList.size() && data != null && data.size() > 0) {
            mDataList.addAll(position, data);
            notifyItemRangeChanged(position, data.size());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder holder = onCreateBaseViewHolder(parent, viewType);
        initItemClickListener(holder);
        return holder;
    }

    public abstract BaseViewHolder onCreateBaseViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * @param baseViewHolder
     */
    private void initItemClickListener(final BaseViewHolder baseViewHolder) {
        if (onRecyclerItemClickListener != null) {
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("BaseAdapter", "onItemClick:" + baseViewHolder.getAdapterPosition() + ", " + v + " ------------------------------>");
                    int position = baseViewHolder.getLayoutPosition();
                    onRecyclerItemClickListener.onItemClick(v, position, getJsonObject(position));
                }
            });
        }
        if (onRecyclerItemLongClickListener != null) {
            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i("BaseAdapter", "onItemLongClick:" + baseViewHolder.getAdapterPosition() + ", " + v + " ------------------------------>");
                    int position = baseViewHolder.getLayoutPosition();
                    return onRecyclerItemLongClickListener.onItemLongClick(v, position, getJsonObject(position));
                }
            });
        }
    }

    protected void initCheckedChangeListener (final BaseViewHolder baseViewHolder, CheckBox checkBox) {
        if (mOnRecyclerItemCheckedChangeListener != null) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOnRecyclerItemCheckedChangeListener.onItemCheckedChange( buttonView, isChecked, baseViewHolder.itemView, baseViewHolder.getAdapterPosition());
                }
            });
        }
    }

    protected void initItemChildClickListener (final BaseViewHolder baseViewHolder, View view) {
        if (onRecyclerItemChildClikListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.d("RecyclerAdapter OnItemClick:  ------------------------------>" + "\n" +
                            "position: " + baseViewHolder.getAdapterPosition() + "\n" +
                            "ItemView:" + v + "\n" +
                            "data: " + getItemData(baseViewHolder.getAdapterPosition()));
                    onRecyclerItemChildClikListener.onItemChildViewClick(v, baseViewHolder.itemView, baseViewHolder.getAdapterPosition());
                }
            });
        }
    }

    /**
     * 单选模式(只支持JSONObject数据源）
     * 设置选中的position和数据源中控制选择的key字段
     * @param position
     * @param key
     */
    public void setSingleSelect(int position, String key) {
        for (int index = 0; index < mDataList.size(); index++) {
            T t = mDataList.get(index);
            if (t instanceof JSONObject) {
                ((JSONObject)t).put(key, position == index ? true : false);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 批量添加记录
     *
     * @param data 需要加入的数据结构
     */
    public void append(List<T> data) {
        append(data, mDataList.size());
    }

    /**
     * 生成item的view
     *
     * @param res
     * @param viewGroup
     * @return
     */
    protected View inflalte(int res, ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(res, viewGroup, false);
    }

    /**
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerViewItemClickListener;
    }

    public OnRecyclerItemCheckedChangeListener getOnRecyclerItemCheckedChangeListener() {
        return mOnRecyclerItemCheckedChangeListener;
    }

    public void setOnRecyclerItemCheckedChangeListener(OnRecyclerItemCheckedChangeListener onRecyclerItemCheckedChangeListener) {
        mOnRecyclerItemCheckedChangeListener = onRecyclerItemCheckedChangeListener;
    }


    public void destroy() {
        this.mDataList.clear();
        this.mContext = null;
        this.mOnRecyclerItemCheckedChangeListener = null;
        this.onRecyclerItemChildClikListener = null;
        this.onRecyclerItemLongClickListener = null;
    }


    /**
     * @param onRecyclerViewItemLongClickListener
     */
    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener onRecyclerViewItemLongClickListener) {
        this.onRecyclerItemLongClickListener = onRecyclerViewItemLongClickListener;
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public OnRecyclerItemLongClickListener getOnRecyclerItemLongClickListener() {
        return onRecyclerItemLongClickListener;
    }

    public interface OnRecyclerItemChildClikListener {
        boolean onItemChildViewClick(View view, View convertView, int position);
    }

    public OnRecyclerItemChildClikListener getOnRecyclerItemChildClikListener() {
        return onRecyclerItemChildClikListener;
    }

    public void setOnRecyclerItemChildClikListener(OnRecyclerItemChildClikListener onRecyclerItemChildClikListener) {
        this.onRecyclerItemChildClikListener = onRecyclerItemChildClikListener;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position, JSONObject data);
    }

    public interface OnRecyclerItemLongClickListener {
        boolean onItemLongClick(View view, int position, JSONObject data);
    }

    public interface OnRecyclerItemCheckedChangeListener {
        boolean onItemCheckedChange(CompoundButton view, boolean checked, View convertView, int position);
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

//        public BaseViewHolder(@NonNull Activity context, @NonNull ViewGroup parent) {
//            super(context.getLayoutInflater().inflate());
//        }

        public abstract void bindViewHolder(int position, JSONObject data);
    }
}
