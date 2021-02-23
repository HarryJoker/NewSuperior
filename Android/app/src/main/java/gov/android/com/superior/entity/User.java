package gov.android.com.superior.entity;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.utils.DateUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.android.com.superior.SuperiorApplication;

/**
 * @name: User
 * 
 * @date 2013-9-19 下午12:04:21
 * 
 * @author Huare(王华)
 * 
 * @copyright Huare wanghua870626@gmail.com Copyright.
 * 
 * @desc: 全局的User实体,
 * 
 */
public class User {

	private static User user;

	private static final String LOCATE_USER_NAME = "User";

    public static final String KEY_GESTURE_PASSWORD = "gesturePassword";

    public static final String KEY_FINGER_PASSWORD = "fingerPassword";

	// user属性
	private final static JSONObject userEntity = new JSONObject();

	/**
	 * 实例化User信息，会默认使用Applicaiton加载本地用户
	 * Activity中不需要用此方法构造User,Application会构造全局的单实例User信息，Activity中只需要拿(newInstance())User实例就Ok
	 *
	 * @return
	 */
	private User() {
		String userStr = SuperiorApplication.getContext().getSharedPreferences(LOCATE_USER_NAME, Context.MODE_PRIVATE).getString("user", "");
		JSONObject user = JSONObject.parseObject(userStr);
		if (user != null && user.size() > 0) {
			userEntity.putAll(user);
		}
		Logger.d(userEntity);
	}
	
	public static User getInstance() {
		if (user == null) user = new User();
		return user;
	}

	public static JSONObject getUser() {
		return userEntity;
	}

	/**
	 * 如果是新user替换原来的user
	 * @param entity
	 */
	public void updateUser(JSONObject entity) {
		if (entity == null || entity.size() == 0 || !entity.containsKey("id")) return;
		if (getUserId() != Integer.parseInt(entity.get("id").toString())) {
			userEntity.clear();
			userEntity.putAll(entity);
		} else {
			userEntity.putAll(entity);
		}
		updateLocate();
		Logger.d(userEntity);
	}

	/**
	 * 只更新当前User的某些字段
	 * @param entity
	 */
	public void update(JSONObject entity) {
		if (entity == null || entity.size() == 0) return;
		userEntity.putAll(entity);
		updateLocate();
	}


	public void update(String key , Object value) {
        if (key != null && value != null) {
            userEntity.put(key, value);
            updateLocate();
        }
    }

	public int getUserId() {
		if (userEntity.containsKey("id")) {
			Object obj = userEntity.get("id");
			if (obj != null) {
				return Integer.parseInt(obj.toString());
			}
		}
		return 0;
	}

	public int getUserRule() {
		if (userEntity.containsKey("rule")) {
			return userEntity.getIntValue("rule");
		}
		return 0;
	}

	public String getUserPhone() {
		if (userEntity.containsKey("phone")) {
			Object obj = userEntity.get("phone");
			return obj == null ? "" : obj.toString();
		}
		return "";
	}

//	public boolean isLogined() {
//		return getUserId() > 0;
//	}

	public boolean isMyTask(long taskId) {

		Logger.d("current task :" + taskId);
		Object object = get("taskIds");
		Logger.d("myTask:" + object);
		if (object != null && object instanceof List) {
			List taskIds = (List)object;
			for (Object obj : taskIds) {
				return  Long.parseLong(obj.toString()) == taskId;
			}
		}
		return false;
	}

	//0：默认，1：县长，2：副县长，3：督查，4：单位
	public int getUserRole() {
		int userRole = Integer.parseInt(get("role").toString());
		if (userRole == 0) {
			return 0;
		}
		JSONObject unit = getUserUnit();
		if (unit == null) return 0;
		int role = unit.getIntValue("role");
		Logger.d("role: " + role);
		return role;
	}

	public JSONObject getUserUnit() {
		if (userEntity.containsKey("unit") && userEntity.getJSONObject("unit") != null) {
			return userEntity.getJSONObject("unit");
		}
		return null;
	}


	//0：默认，1：县长，2：副县长，3：督查，4：单位
	public int getUnitId() {
		JSONObject unit = getUserUnit();
		if (unit == null) return 0;
		return unit.getIntValue("id");
	}

	public int getSex() {
		Object object = get("sex");
		if (object == null || TextUtils.isEmpty(object.toString())) {
			return  0;
		}
		return Integer.parseInt(object.toString());
	}

	public int getUserAge() {
		Object object = get("birthday");
		if (object == null || TextUtils.isEmpty(object.toString())) {
			return  0;
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date bithday = format.parse("1994-08-23 17:20:20");
			int age = DateUtil.getAgeByBirth(bithday);
			return age;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getUnitLogo() {
		JSONObject unit = getUserUnit();
		if (unit == null) return "";
		return unit.getString("logo");
	}

	public String getUnitName() {
		JSONObject unit = getUserUnit();
		if (unit == null) return "";
		return unit.getString("name");
	}

	public boolean hasAlisAndTag() {
		if (userEntity.containsKey("hasAliaAndTag")) {
			Object object = userEntity.get("hasAliaAndTag");
			if (object != null && object instanceof Boolean) {
				return (Boolean)object;
			}
		}
		return false;
	}

	public void logoutUser() {
		userEntity.clear();
		SuperiorApplication.getContext().getSharedPreferences(LOCATE_USER_NAME, Context.MODE_PRIVATE).edit().clear().commit();
	}

	public int getVerify() {
        if (userEntity.containsKey("verify")) {
            Object object = userEntity.get("verify");
            if (object != null) {
                return Integer.parseInt(object.toString());
            }
        }
        return 0;
    }

	public boolean isVerify() {
		return getVerify() == 1;
	}

	public Object get(String key) {
		if (key == null || userEntity.size() == 0 || !userEntity.containsKey(key)) return "";
		return userEntity.get(key);
	}

	private List<String> json2List(String json) {
		if (json == null || json.trim().length() == 0 || json.equals("null")) return new ArrayList<String>();
		return new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
	}
	
	private void updateLocate() {
		Editor editor = SuperiorApplication.getContext().getSharedPreferences(LOCATE_USER_NAME, Context.MODE_PRIVATE).edit();
		editor.putString("user", new Gson().toJson(userEntity));
		editor.commit();
		Logger.d(userEntity);
	}

	@Override
	public String toString() {
		return "" + userEntity;
	}

}
