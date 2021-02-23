//package gov.android.com.superior.tools;
//
//import android.text.TextUtils;
//
//import java.util.List;
//
///**
// * @author HarryJoker
// * @date :2020/9/15 23:18
// * @description:
// */
//public class PatternHelper {
//
//    private String message;
//    private String pwd;
//    private int times;
//    private boolean isFinish = false;
//    private boolean isOk = false;
//
//
//
//    private boolean validateForSetting(List<Integer> hitIndexList) {
//        isFinish = false;
//        isOk = false;
//        if (hitIndexList == null || hitIndexList.size() < MAX_SIZE) {
//            pwd = null;
//            message = sizeErrorMsg;
//            return false;
//        }
//        //1. draw first time
//        if (TextUtils.isEmpty(pwd)) {
//            pwd = convert2String(hitIndexList);
//            message = reDrawMsg;
//            isOk = true;
//            return true;
//        }
//        //2. draw second times
//        if (pwd == convert2String(hitIndexList)) {
//            message = settingSuccessMsg;
//            saveToStorage(pwd);
//            isOk = true;
//            isFinish = true;
//        } else {
//            pwd = null;
//            message = diffPreErrorMsg;
//        }
//    }
//
//    public boolean validateForChecking(List<Integer> hitIndexList) {
//        isOk = false;
//        if (hitIndexList == null || hitIndexList.size() < MAX_SIZE) {
//            times++;
//            isFinish = times >= MAX_SIZE;
//            message = pwdErrorMsg;
//            return false;
//        }
//        storagePwd = fromStorage
//        if (!TextUtils.isEmpty(storagePwd) && storagePwd == convert2String(hitIndexList)) {
//            message = checkingSuccessMsg
//            isOk = true
//            isFinish = true
//        } else {
//            times++
//            isFinish = times >= MAX_SIZE
//            message = pwdErrorMsg
//        }
//    }
//
//    private val remainTimes: Int
//    get() = if (times < 5) MAX_TIMES - times else 0
//    private val reDrawMsg: String = "请再次绘制解锁图案"
//    private val settingSuccessMsg: String = "手势解锁图案设置成功！"
//    private val checkingSuccessMsg: String = "解锁成功！"
//    private val sizeErrorMsg: String = String.format("至少连接个%d点，请重新绘制", MAX_SIZE)
//    private val diffPreErrorMsg: String = "与上次绘制不一致，请重新绘制"
//    private val pwdErrorMsg: String
//    get() = String.format("密码错误，还剩%d次机会", remainTimes)
//
//    private fun convert2String(hitIndexList: List<Int>): String {
//        return hitIndexList.toString()
//    }
//
//    private fun saveToStorage(gesturePwd: String?) {
//        val encryptPwd = encrypt(gesturePwd!!)
//        instance!!.saveString(GESTURE_PWD_KEY, encryptPwd)
//    }
//
//    private val fromStorage: String? = instance?.getString(GESTURE_PWD_KEY)?.let { SecurityUtil.decrypt(it) }
//
//
//    companion object {
//        const val MAX_SIZE = 4
//        const val MAX_TIMES = 3
//        private const val GESTURE_PWD_KEY = "gesture_pwd_key"
//    }
//}
