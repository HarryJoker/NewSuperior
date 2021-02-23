package gov.android.com.superior.callback;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.StringConvert;

//public abstract class JsonObjectCallBack<T> extends AbsCallback<T> {
//
//    @Override
//    public T convertResponse(okhttp3.Response response) throws Throwable {
//
//        JSONObject jsonObject = JSONObject.parseObject(new StringConvert().convertResponse(response));
//
//        //关闭reponse
//        response.close();
//
//        //外层状态结构中errorCode！=0，交互失败
//        if (jsonObject.getIntValue("code") != 0) throw new IllegalStateException(jsonObject.getString("msg"));
//
//        //获取数据层
//        return (T) jsonObject.get("data");
//    }
//}