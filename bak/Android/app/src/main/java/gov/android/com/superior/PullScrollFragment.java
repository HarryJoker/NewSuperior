package gov.android.com.superior;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harry.pulltorefresh.library.PullToRefreshScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class PullScrollFragment extends BaseFragment {

    private PullToRefreshScrollView pullToRefreshScrollView;

    public PullScrollFragment() {

    }


    public abstract void onInitPullRefreshScroll();

    public abstract void attachToRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pull_scroll, container, false);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        attachToRootView(inflater,pullToRefreshScrollView, savedInstanceState);
        return view;
    }

}
