package com.first.orient.base.callback;

import com.first.orient.base.utils.GsonUtil;
import com.first.orient.base.utils.JokerLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.StringConvert;

import java.util.Map;

/**
 * Created by wanghua on 17/8/4.
 */

public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {

        //注册自定义MapTypeAdapter,解析int默认转换double问题
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(), new GsonUtil.MapTypeAdapter()).create();
        //转换为Map泛型
        Map data = gson.fromJson(new StringConvert().convertResponse(response), new TypeToken<Map<String, Object>>(){}.getType());

        //关闭reponse
        response.close();

        //外层状态结构中errorCode！=0，交互失败
        JokerLog.d(data.get("code").getClass().getName());
        int err = Integer.parseInt(data.get("code").toString());
        if (err != 0) throw new IllegalStateException(data.get("msg").toString());

        //获取数据层
        return (T)data.get("data");
    }
}
