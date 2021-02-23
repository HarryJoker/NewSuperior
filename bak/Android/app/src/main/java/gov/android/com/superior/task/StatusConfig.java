package gov.android.com.superior.task;

import java.util.HashMap;
import java.util.Map;

public class StatusConfig {

    public static final String[] categroyNames = new String[] {"", "政府工作报告", "市委市政府重大决策部署", "建议提案", "会议议定事项","领导批示", "专项督查", "重点项目"};

    public static final Map<String, String> STATUS = new HashMap<String, String>(){
        {
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


    public static final Map<String, String> taskTitleLabels = new HashMap<String, String>(){
        {
                put("1", "重点任务");
                put("2", "具体项目");
                put("3", "建议名称");
                put("4", "议定事项");
                put("5", "交办事项");
                put("6", "重点工作");
                put("7", "重点工作");
        }
    };

    public static final Map<String, String> childTaskTitleLabels = new HashMap<String, String>() {
        {
             put("1", "主要任务");
             put("2", "办理细则");
             put("3", "办理要求");
             put("4", "工作要求");
             put("5", "办理要求");
             put("6", "具体项目");
             put("7", "具体项目");
        }
    };
    
    public static final Map<String, String[]> childTaskCategoryNames = new HashMap<String, String[]>() {
        {
                put("2", new String[] {"", "第一名", "第二名", "第三名", "第四名", "第五名", "第六名", "第七名"});
                put("3", new String[] {"", "人大", "政协"});
                put("4", new String[] {"", "全体会议", "常务会议", "县长办公室会议", "专题会议"});
        }
    };
}
