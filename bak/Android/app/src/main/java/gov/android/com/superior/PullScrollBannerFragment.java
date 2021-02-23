package gov.android.com.superior;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.harry.pulltorefresh.library.PullToRefreshScrollView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.Arrays;

import gov.android.com.superior.tools.GlideImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class PullScrollBannerFragment extends Fragment {

    private Banner banner;

    private PullToRefreshScrollView pullToRefreshScrollView;

    public PullScrollBannerFragment() {

    }

    private void initPullToRefreshScrollView() {

    }

    public abstract void onInitPullToRefreshScrollView(PullToRefreshScrollView pullToRefreshScrollView);

    //banner默认设置
    private void initBanner() {
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);


        String[] imageUrls = {"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
                "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
                "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
                "http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
                "http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg"};

        String[] titles = {"SimpleDraweeView最基本的使用 ", "SimpleDraweeView的圆形图", "SimpleDraweeView的圆角图 ", "SimpleDraweeView的缩放类型", "响应习近平主席的留痕督查指导"};

        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(Arrays.asList(imageUrls));

        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(Arrays.asList(titles));

        banner.setDelayTime(2500);

        banner.setIndicatorGravity(BannerConfig.RIGHT);

        onInitBanner(banner);

        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    public Banner getBanner() {
        if (banner == null) {
            banner =  (Banner) getView().findViewById(R.id.banner);
        }
        return banner;
    }

    public abstract void onInitBanner(Banner banner);


    public abstract void attachToRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pull_scroll_banner, container, false);
        banner = (Banner) view.findViewById(R.id.banner);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        attachToRootView(inflater, (LinearLayout)view.findViewById(R.id.container), savedInstanceState);
        initPullToRefreshScrollView();
        initBanner();
        return view;
    }

}
