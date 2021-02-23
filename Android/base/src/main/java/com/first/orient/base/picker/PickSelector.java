package com.first.orient.base.picker;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import java.io.Serializable;
import static com.first.orient.base.picker.PickSelectorActivity.KEY_BUILDER;

/**
 * Author: harryjoker
 * Created on: 2020-01-17 20:36
 * Description:
 */
public class PickSelector {

    private Builder mBuilder;

    private Activity mContext;

    private PickSelector(Activity context) {
        mBuilder = new Builder();
        this.mContext = context;
    }

    public static PickSelector create(Activity activity) {
         return new PickSelector(activity);
    }

    public interface OnPickerResult extends Serializable {
        void onPickerResult(int index, String picker);
    }


    public PickSelector entrys(String[] entrys) {
        this.mBuilder.entrys = entrys;
        return this;
    }

    public PickSelector pickerTitle(String title) {
        this.mBuilder.title = title;
        return this;
    }

    public PickSelector displayWidth(int width) {
        this.mBuilder.width = width;
        return this;
    }

    public PickSelector displayHeight(int height) {
        this.mBuilder.height = height;
        return this;
    }

    public PickSelector optionTextSize(float dp) {
        this.mBuilder.optionTextSize = dp;
        return this;
    }

    public PickSelector optionTextColor(int color) {
        this.mBuilder.optionTextColor = color;
        return this;
    }

    public PickSelector windowMargin(int margin) {
        this.mBuilder.margin = margin;
        return this;
    }

    public PickSelector result(PickSelector.OnPickerResult pickerResult) {
        PickSelectorActivity.setmOnPickerResult(pickerResult);
        return this;
    }

    public void pick() {
        ActivityOptions transitionActivity = ActivityOptions.makeSceneTransitionAnimation(mContext);
        Intent intent = new Intent(mContext, PickSelectorActivity.class);
        intent.putExtra(KEY_BUILDER, mBuilder);
        mContext.startActivity(intent, transitionActivity.toBundle());
    }



    static class Builder implements Serializable {

        public String[] entrys;
        public String title;
        public int width;
        public int height;
        public int margin;
        public float optionTextSize;
        public int optionTextColor;

        public Builder() {
        }



        protected String[] getEntrys() {
            return entrys == null ? new String[]{} : entrys;
        }

        protected String getTitle() {
            return title == null ? "选择" : title;
        }

        protected int getWidth() {
            return width;
        }

        protected int getHeight() {
            return height;
        }
    }
}
