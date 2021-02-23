package com.first.orient.base.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.first.orient.base.R;
import com.first.orient.base.view.ImageTextView;

/**
 * Author: harryjoker
 * Created on: 2020-01-02 19:48
 * Description:
 */
public abstract class BaseToolBarActivity extends BaseContentActivity {

    private ImageView mBarArrow;
    private TextView mBarTitle;
    private ImageTextView mBarFun;
    private LinearLayout layoutToolBar;

    @Override
    protected void findRegionView() {
        super.findRegionView();
        layoutToolBar = findViewById(R.id.layout_toolbar);

        initToolbar();
    }

    private void initToolbar() {
        if (layoutToolBar == null) return;
        mBarArrow = layoutToolBar.findViewById(R.id.iv_base_bar_arrow);
        mBarTitle = layoutToolBar.findViewById(R.id.tv_base_bar_title);
        mBarFun = layoutToolBar.findViewById(R.id.itv_base_bar_fun);

        if (mBarArrow != null) {
            onBarArrow(mBarArrow);
            mBarArrow.setOnClickListener(mBarArrowClickListener);
        }
        if (mBarTitle != null) {
            onBarTitle(mBarTitle);
        }
        if (mBarFun != null) {
            onBarFun(mBarFun);
            mBarFun.setOnClickListener(mBarFunClickListener);
        }
    }

    public ImageTextView getmBarFun() {
        return mBarFun;
    }

    @Override
    protected int getBaseLayoutRes() {
        return R.layout.layout_main;
    }

    public void setBarTitle(String title) {
        if (mBarTitle != null && title != null) {
            mBarTitle.setText(title);
        }
    }

    protected void onBarArrow(ImageView barArrow) {

    }

    protected void onBarTitle(TextView barTitle) {

    }

    protected void onBarFun(ImageTextView barFun) {

    }

    protected void onBarArrowClick(View v) {
        finish();
    }

    protected void onBarFunClick(View v) {

    }

    private View.OnClickListener mBarArrowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBarArrowClick(v);
        }
    };

    private View.OnClickListener mBarFunClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBarFunClick(v);
        }
    };
}
