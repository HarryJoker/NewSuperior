//package com.first.orient.base.utils;
//
//import android.app.Activity;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import com.alibaba.sdk.android.oss.ClientConfiguration;
//import com.alibaba.sdk.android.oss.ClientException;
//import com.alibaba.sdk.android.oss.OSS;
//import com.alibaba.sdk.android.oss.OSSClient;
//import com.alibaba.sdk.android.oss.ServiceException;
//import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
//import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
//import com.alibaba.sdk.android.oss.common.OSSLog;
//import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
//import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
//import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
//import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
//import com.orhanobut.logger.Logger;
//
///**
// * Author: harryjoker
// * Created on: 2019-11-05 18:24
// * Description:
// */
//public class AliOssClient {
//
////    private static final String endpoint        = "http://oss-ap-southeast-3.aliyuncs.com";
//    private static final String endpoint        = "http://oss-accelerate.aliyuncs.com";
//    private static final String accessKeyId     = "LTAI4Fj37GeaturTT8VCbWZA";
//    private static final String accessKeySecret = "dK62eVR4MwuG07SnsE3FCgHd2lwRzn";
//    private static final String bucketName      = "moyinshijie";
//    private static final String objectName      = "imgs";
//
//    private OSSCredentialProvider ossProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, "");
//
//    private ClientConfiguration conf = new ClientConfiguration();
//    {
//        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
//        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
//        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
//        conf.setMaxErrorRetry(2); // retry，default 2
////        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1 && msg.obj != null) {
//                if (mOnOSSUploadCallback != null) {
//                    mOnOSSUploadCallback.onSuccess(msg.obj.toString());
//                }
//            }
//            if (msg.what != 1) {
//                if (mOnOSSUploadCallback != null) {
//                    mOnOSSUploadCallback.onFalie();
//                }
//            }
//        }
//    };
//
//    private OnOSSUploadCallback mOnOSSUploadCallback;
//
//    public void setOnOSSUploadCallback(OnOSSUploadCallback onOSSUploadCallback) {
//        mOnOSSUploadCallback = onOSSUploadCallback;
//    }
//
//    public void asyncUpload(Activity activity, String filePath) {
//        asyncUpload(activity, filePath, null);
//    }
//
//    public void asyncUpload(final Activity activity, final String filePath, final OnOSSUploadCallback onCallback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                upload(activity, filePath, onCallback);
//            }
//        }).start();
//    }
//
//    private void upload(Activity activity, String filePath,OnOSSUploadCallback onCallback) {
//        try {
//            if (onCallback != null) {
//                this.mOnOSSUploadCallback = onCallback;
//            }
//            OSS oss = new OSSClient(activity, endpoint, ossProvider, conf);
//            String fileName = MD5Utils.getMD5String(System.currentTimeMillis() + "shortVideo") + filePath.substring(filePath.lastIndexOf("."));
//            // 创建断点上传请求
//            ResumableUploadRequest request = new ResumableUploadRequest(bucketName, objectName + "/" + fileName, filePath);
//            // 设置上传过程回调
////            request.setProgressCallback(mResumableUploadRequestOSSProgressCallback);
//            oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
//                @Override
//                public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
//                    Log.d("AliOssClient", "upload success: " + result.getBucketName() + ", " + result.getObjectKey() );
//
//                    handler.sendMessage(handler.obtainMessage(1,  endpoint.replace("//", "//" + bucketName + ".") + "/" + result.getObjectKey()));
//                }
//
//                @Override
//                public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                    Log.d("AliOssClient", Thread.currentThread().getName() + "， MainThread：" + Looper.getMainLooper().getThread().getName());
//                    if (clientExcepion != null) {
//                        clientExcepion.printStackTrace();
//                        Log.e("AliOssClient", "异常信息: " + clientExcepion.toString());
//                    }
//                    if (serviceException != null) {
//                        Log.e("AliOssClient", "ServiceException: + \n ErrorCode:" + serviceException.getErrorCode() + "，HostId:" + serviceException.getHostId() + "，RawMessage:" + serviceException.getRawMessage() + ", " + serviceException.toString());
//                    }
//                    handler.sendEmptyMessage(-1);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private OSSProgressCallback<ResumableUploadRequest> mResumableUploadRequestOSSProgressCallback = new OSSProgressCallback<ResumableUploadRequest>() {
//        @Override
//        public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
//
//        }
//    };
//    public interface OnOSSUploadCallback {
//        void onSuccess(String url);
//        void onFalie();
//    }
//}