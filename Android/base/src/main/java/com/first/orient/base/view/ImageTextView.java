package com.first.orient.base.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: harryjoker
 * Created on: 2019-10-29 13:46
 * Description:
 */
public class ImageTextView extends FrameLayout {
    ImageView imageView;
    TextView mTextView;

    private void initView() {
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        addView(imageView);

        mTextView = new TextView(getContext());
        mTextView.setLayoutParams(params);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setGravity(Gravity.CENTER);
        addView(mTextView);
    }

    public ImageTextView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }


    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return mTextView;
    }
}
