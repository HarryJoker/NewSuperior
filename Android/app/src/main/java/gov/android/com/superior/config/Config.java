package gov.android.com.superior.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.android.com.superior.R;

public class Config {

//    public static final String[] categroyNames = new String[]{"", "政府工作报告", "市委市政府重大决策部署", "建议提案", "会议议定事项", "领导批示", "专项督查", "重点项目"};
    public static final LinkedHashMap<Integer, String> categoryTitles = new LinkedHashMap<Integer, String>() {
        {
            put(1, "政府工作报告");
            put(2, "7+3重点改革任务");
            put(3, "建议提案");
            put(4, "会议议定事项");
            put(5, "领导批示");
            put(6, "专项督查");
            put(7, "重点项目");
            put(8, "民生实事");
            put(9, "群众线索");
        }
    };

    public static final Map<Integer, String[]> labelMetas = new HashMap<Integer, String[]>() {
        {
            put(1, new String[]{"任务类别：", "重点工作：", "推进计划："});
            put(2, new String[]{"任务排名：", "具体项目：", "办理细则："});
            put(3, new String[]{"任务类别：", "建议提案：", "办理要求："});
            put(4, new String[]{"任务类别：", "议定事项：", "工作要求："});
            put(5, new String[]{"", "交办事项：", "办理要求："});
            put(6, new String[]{"", "督查事项：", "具体要求："});
            put(7, new String[]{"", "重点工作：", "推进计划："});
        }
    };

    public static final Map<String, String> STATUS = new HashMap<String, String>() {
        {
            put("-2", "待完善");
            put("-1", "待手签");
            put("0", "未领取");
            put("1", "已领取");
            put("20", "上报领导");
            put("21", "领导批示");
            put("31", "已报送");
            put("50", "已逾期");
            put("51", "系统催报");
            put("52", "督查催报");
            put("71", "已审核");
            put("72", "进度缓慢");
            put("73", "进度较快");
            put("74", "退回重报");
            put("91", "已完成");
        }
    };


    public static final Map<String, String> scoreActivitys = new HashMap<String, String>() {
        {
            put("11", "响应");
            put("50", "逾期");
            put("52", "催报");
            put("72", "缓慢");
            put("74", "退回");
        }
    };

    public static final Map<String, Integer> lightStatus = new HashMap<String, Integer>() {
        {
            put("71",  R.mipmap.icon_light_yellow);
            put("72", R.mipmap.icon_light_red);
            put("73",R.mipmap.icon_light_green);
            put("91", R.mipmap.icon_light_star);
        }
    };

    public static final Map<String, String> informstatus = new HashMap<String, String>() {
        {
            put("0", "当前工作未开展");
            put("71", "当前工作序时推进");
            put("72", "当前工作进展缓慢");
            put("73", "当前工作进展较快");
            put("91", "当前工作已完成");
        }
    };

    public static final Map<String, String[]> categoryTaskTypeLabels = new HashMap<String, String[]>() {
        {
            put("1", new String[]{"预期目标类", "试点验收类", "定量类", "定性类"});
            put("2", new String[]{"第一名", "第二名", "第三名", "第四名", "第五名", "第六名", "第七名", "第八名", "第九名", "第十名", "未排名"});
            put("3", new String[]{"人大", "政协"});
            put("4", new String[]{"全体会议", "常务会议", "县长办公室会议", "专题会议"});
        }
    };

    public static final Map<Integer, String> opinionTypes = new HashMap<Integer, String>() {
        {
            put(0, "城市基础设施建设问题");
            put(1, "教育卫生问题");
            put(2, "经济发展问题");
            put(3, "道路提升问题");
            put(4, "文化旅游问题");
        }
    };
}
