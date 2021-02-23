package com.first.orient.base.picker;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.first.orient.base.R;
import com.first.orient.base.activity.BaseActivity;

import java.io.Serializable;

public class PickSelectorActivity extends BaseActivity {
    public static final String KEY_BUILDER = "builder";

    private static PickSelector.OnPickerResult mOnPickerResult;

    public static void setmOnPickerResult(PickSelector.OnPickerResult pickerResult) {
        mOnPickerResult = pickerResult;
    }

    private RecyclerView mRecyclerView;

    private LinearLayout mLayoutContent;

    private EntryAdatper mEntryAdatper;

    private PickSelector.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Serializable serializable = getIntent().getSerializableExtra(KEY_BUILDER);
        if (serializable == null || !(serializable instanceof PickSelector.Builder)){
            throw new NullPointerException("Builder can not null.......");
        }

        mBuilder = (PickSelector.Builder)serializable;

        setContentView(R.layout.activity_pick_selector);

        Transition transitionFadeFast = TransitionInflater.from(this).inflateTransition(R.transition.fade_fast);
        getWindow().setEnterTransition(transitionFadeFast);


        mRecyclerView = findViewById(R.id.recyclerview);

        mLayoutContent = findViewById(R.id.laycontent);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        buildWindowForConfig();
    }


    private void buildWindowForConfig() {

        if (mBuilder == null) return;
        if (mBuilder.margin > 0) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLayoutContent.getLayoutParams();
            layoutParams.leftMargin = mBuilder.margin;
            layoutParams.rightMargin = mBuilder.margin;
        }

        ((TextView)findViewById(R.id.tv_title)).setText(mBuilder.getTitle());

        mRecyclerView.setAdapter(new EntryAdatper());
    }


    public void screenClick(View v) {
        closePicker();
    }

    private void closePicker() {
        finishAfterTransition();
    }

    public void cancelClick(View v) {
        closePicker();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closePicker();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class EntryAdatper extends RecyclerView.Adapter<EntryAdatper.EntryViewHolder> {

        private String[] entrys;

        public EntryAdatper() {
            entrys = mBuilder.getEntrys();
        }

        @NonNull
        @Override
        public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EntryViewHolder(getLayoutInflater().inflate(R.layout.item_pick_selector, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.bindEntryViewHolder(position);
        }

        @Override
        public int getItemCount() {
            return entrys == null ? 0 : entrys.length;
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {

            TextView entry;

            public EntryViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(entryClick);
                entry = itemView.findViewById(R.id.tv_picker);

                buildLayoutForConfig();
            }

            private void buildLayoutForConfig() {
                if (mBuilder == null) return;
                if (mBuilder.optionTextColor != 0) {
                    entry.setTextColor(mBuilder.optionTextColor);
                }

                if (mBuilder.optionTextSize != 0) {
                    entry.setTextSize(mBuilder.optionTextSize);
                }
            }

            public void bindEntryViewHolder(int position) {
                entry.setText(position < entrys.length ? entrys[position] : "");
            }
        }
    }

    private View.OnClickListener entryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int index = (Integer)v.getTag();
                if (mOnPickerResult != null) {
                    closePicker();
                    mOnPickerResult.onPickerResult(index, mBuilder.getEntrys()[index]);
                }
            }
        }
    };

}
