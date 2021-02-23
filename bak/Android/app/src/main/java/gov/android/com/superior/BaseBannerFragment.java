package gov.android.com.superior;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.banner.BannerActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.GlideImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseBannerFragment extends Fragment {

    private static final String CANCEL_TAG = "BANNER";

    private List<String> titles = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private List<String> attachments = new ArrayList<>();

    private Banner banner;

    protected void startBanner(Banner banner) {

        this.banner = banner;

        initBanner();

        asyncBanner();
    }


    private void invalidateBanner(List<String> titles, List<String> images) {

        //设置图片集合
        banner.setImages(images);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    public void asyncBanner() {
        OkGo.<List<Map<String, String>>>get(Config.BANNER).tag(CANCEL_TAG).cacheTime(3 * 60 *60 * 1000).cacheKey("banner").cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST).execute(bannerCallback);
    }

    private JsonCallback<List<Map<String, String>>> bannerCallback = new JsonCallback<List<Map<String, String>>>() {
        @Override
        public void onSuccess(Response<List<Map<String, String>>> response) {

            for (Map<String, String> banner : response.body()) {
                titles.add(banner.get("title"));
                urls.add(banner.get("link"));
                attachments.add(Config.ATTACHMENT + banner.get("image"));
            }
            invalidateBanner(titles, attachments);
        }
    };

    private void initBanner() {
        try {
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);

            //设置图片加载器
            banner.setImageLoader(new GlideImageLoader());

            banner.setDelayTime(3 * 1000);

            banner.setOnBannerListener(bannerListener);

            banner.setIndicatorGravity(BannerConfig.RIGHT);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private OnBannerListener bannerListener = new OnBannerListener() {
        @Override
        public void OnBannerClick(int position) {
            Intent intent = new Intent(getActivity(), BannerActivity.class);
            intent.putExtra("url", urls.get(position));
            intent.putExtra("title", titles.get(position));
            startActivity(intent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(CANCEL_TAG);
    }
}
