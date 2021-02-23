package gov.android.com.superior.tools;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;


import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

//public class UpdateAppHttpUtil implements HttpManager {
//    /**
//     * 异步get
//     *
//     * @param url      get请求地址
//     * @param params   get参数
//     * @param callBack 回调
//     */
//    @Override
//    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
//        OkGo.<String>get(url)
//                .params(params)
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
//                        callBack.onResponse(response.body());
//                    }
//
//                    @Override
//                    public void onError(com.lzy.okgo.model.Response<String> response) {
//                        super.onError(response);
//                        callBack.onError("获取更新版本信息失败");
//                    }
//                });
//    }
//
//    /**
//     * 异步post
//     *
//     * @param url      post请求地址
//     * @param params   post请求参数
//     * @param callBack 回调
//     */
//    @Override
//    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
//        OkGo.<String>post(url)
//                .params(params)
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
//                        callBack.onResponse(response.body());
//                    }
//
//                    @Override
//                    public void onError(com.lzy.okgo.model.Response<String> response) {
//                        super.onError(response);
//                        callBack.onError("获取更新版本信息失败");
//                    }
//                });
//
//    }
//
//    /**
//     * 下载
//     *
//     * @param url      下载地址
//     * @param path     文件保存路径
//     * @param fileName 文件名称
//     * @param callback 回调
//     */
//    @Override
//    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
//        OkHttpUtils.get()
//                .url(url)
//                .build()
//                .execute(new FileCallBack(path, fileName) {
//                    @Override
//                    public void inProgress(float progress, long total, int id) {
//                        callback.onProgress(progress, total);
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e, int id) {
//                        callback.onError(validateError(e, response));
//                    }
//
//                    @Override
//                    public void onResponse(File response, int id) {
//                        callback.onResponse(response);
//
//                    }
//
//                    @Override
//                    public void onBefore(Request request, int id) {
//                        super.onBefore(request, id);
//                        callback.onBefore();
//                    }
//                });
//
//    }
//}
