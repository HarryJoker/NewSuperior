package gov.android.com.superior.task;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.http.Config;

public class UnitManager {

    private static UnitManager instace;

    private Map<String, JSONObject> units = new HashMap<String, JSONObject>() {
        {
            JSONObject unit = new JSONObject();
            unit.put("name", "系统");
            unit.put("logo", "robot.png");
            put("0", unit);
        }
    };

    private UnitManager() {

    }

    public static UnitManager getInstace() {
        if (instace == null) {
            synchronized (UnitManager.class) {
                if (instace == null) {
                    instace = new UnitManager();
                }
            }
        }
        return instace;
    }

    public JSONObject getUnit(String unitId) {
        if (!TextUtils.isEmpty(unitId)) {
            return units.get(unitId);
        }
        return null;
    }

    public void aysncCacheUnits() {
        OkGo.<JSONArray>get(Config.UNIT_ALL).execute(unitsCallback);
    }

    private JsonObjectCallBack<JSONArray> unitsCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            if (response.body() != null && response.body().size() > 0) {
                makeArr2Map(response.body());
            }
        }
    };

    private void makeArr2Map(JSONArray array) {
        if (array == null) return;
        for (int n = 0; n < array.size(); n++) {
            units.put(array.getJSONObject(n).getString("id"), array.getJSONObject(n));
        }
    }
}
