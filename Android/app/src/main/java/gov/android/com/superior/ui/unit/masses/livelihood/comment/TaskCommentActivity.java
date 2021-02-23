package gov.android.com.superior.ui.unit.masses.livelihood.comment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.view.ImageTextView;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;

/**
 * 任务Inform + 评论列表
 */
public class TaskCommentActivity extends BaseToolBarActivity {

    private int mUnitTaskId;

    private JSONObject mTask;

    private TextView tvTitle;
    private TextView tvContent;
    private SlidingTabLayout slidingTablayout;
    private ViewPager viewPage;

    @Override
    public void onInitParams() {
        mUnitTaskId = getIntent().getIntExtra("unitTaskId", 0);
        String task = getIntent().getStringExtra("task");
        if (TextUtils.isEmpty(task) || mUnitTaskId == 0) {
            showToast("数据错误");
            finish();
        }
        mTask = JSONObject.parseObject(task);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_task_comment;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("公众评价");
    }

    @Override
    protected void onBarFunClick(View v) {
        super.onBarFunClick(v);
        Intent intent = new Intent(this, CreateCommentActivity.class);
        intent.putExtra("unitTaskId", mUnitTaskId);
        startActivity(intent);
    }

    @Override
    protected void onBarFun(ImageTextView barFun) {
        barFun.getTextView().setText("我要评价");
    }

    @Override
    protected void onFindViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        slidingTablayout = (SlidingTabLayout) findViewById(R.id.sliding_tablayout);
        viewPage = (ViewPager) findViewById(R.id.viewPage);
    }

    @Override
    public void onInitView() {
        tvTitle.setText(mTask.getString("title"));
        tvContent.setText(mTask.getString("content"));

        viewPage.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slidingTablayout.setViewPager(viewPage);
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private Map<Integer, Fragment> mTypeFragments = new HashMap<>();

        private String[] titles = new String[] {"全部", "满意", "基本满意", "不满意"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if (!mTypeFragments.containsKey(position)) {
                mTypeFragments.put(position, makeNewInstance(position));
            }
            return mTypeFragments.get(position);
        }

        private Fragment makeNewInstance(int type) {
            Fragment fragment = null;
            try {
                fragment = new CommentsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putInt("unitTaskId", mUnitTaskId);
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
