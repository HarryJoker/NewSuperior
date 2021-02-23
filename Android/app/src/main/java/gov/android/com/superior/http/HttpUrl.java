package gov.android.com.superior.http;


import android.text.TextUtils;

/**
 * Created by wanghua on 17/8/4.
 */

public final class HttpUrl {

//    private static final String URL = "http://192.168.0.102/Superior/app.php/";
//    private static final String URL = "http://api.admin.com/app.php/";
//    private static final String URL = "http://ducha.boxing.gov.cn/v2/Superior/app.php/";
    private static final String URL = "http://api.superior.yooar.com/app.php/";
//    private static final String URL = "http://ducha.boxing.gov.cn/Superior/app.php/";

    //图片地址
    public static final String ATTACHMENT = URL.replace("app.php/", "") + "uploads/";

    public static final String ATTACHMENT_DEFAULT = "http://api.superior.yooar.com/uploads/ic_default_attachment.png";

    public static String makeAttachmentUrl(String attchemntName) {
        if (TextUtils.isEmpty(attchemntName)) {
            return ATTACHMENT_DEFAULT;
        }
        return ATTACHMENT + attchemntName;
    }

    public static final String UPLOAD = "http://api.superior.yooar.com/app.php/banner/uploadSingle";
    //Banner
    public static final String BANNER = URL + "banner/getBanners";

    //注册用户
    public static final String USER_CREATE = URL + "user/create";

    //绩效得分
    public static final String UNIT_SCORE = URL + "unit/getUnitScore";

    //绩效得分
    public static final String UNIT_CATEGORY_SCORE = URL + "unit/getUnitScoreByCategory";

    //督查激励列表
    public static final String GET_UNITTASK_REWARDS = URL + "reward/getRewards";

    //某项责任事项的督查激励
    public static final String GET_UNITTASK_REWARD = URL + "reward/getUnitTaskReward";

    //更新信息
    public static final String USER_UPDATE = URL +  "user/update";

    public static final String UPDATE_UNIT = URL +  "unit/updateUnit";

    //获取用户
    public static final String USER_GET = URL +  "user/get";

    //登录
    public static final String USER_LOGIN = URL +  "user/login";

    //登录
    public static final String USER_LOGIN_BY_ACCOUNT = URL +  "user/userLogin";

    //根据id获取task
    public static final String TASK_GET = URL + "task/get";

    //根据category获取所有任务
    public static final String TASK_BY_CATEGORY = URL + "task/getAllTasksByCategory";

    //根据category获取已审核的任务
    public static final String TASK_VERIFY_BY_CATEGORY = URL + "task/getAllVerifyTasksByCategory";

    //根据部门id获取所有的任务（已领和未领）
    public static final String TASK_BY_UNIT_AND_CATEGORY = URL + "task/getAllTasksByUnitAndCategory";

    //根据部门id获取所有的任务用category分类（已领或未领）
    public static final String TASK_BY_UNIT_AND_CATEGORY_WITH_ACCEPT = URL + "task/getTaskByUnitAndCategoryWithAccept";

    //用户领取任务
    public static final String TASK_ACCEPT = URL + "task/accept";

    //获取Category下User的任务
    public static final String TASK_GET_BY_USER_AND_CATEGORY = URL + "task/getTaskByUserAndCategory";

    //获取Category下县长首页的数据统计(附带副县长列表)
    public static final String TASK_COUNT_WITH_LEADERS = URL + "task/getTaskCountWithLeadersByUnitId";

    public static final String LEADER_SUMMARY_INFO = URL + "task/getTaskSummaryAllLeaderUnit";

    public static final String USER_LIST_BY_UNIT = URL + "user/getUsersByUnit";

    public static final String DEPUTY_LEADER_SUMMARY_INFO = URL + "task/getTaskSummaryByLeaderUnit";

    public static final String LEADER_MONTH_SUMMARY_INFO = URL + "task/getLeaderMonthTaskSummary";

    //分管子任务的unitask列表
    public static final String UNITASK_LIST_BY_CHILDTASK = URL + "task/getUnitTaskListByChildTask";


    //获取Category下县长分管的任务
    public static final String TASK_GET_LEADER_TASK_BY_UNIT_AND_CATEGORY = URL + "task/getLeaderTaskByUnitIdAndCateory";

//    public static final String TASK_LIST_BY_LEADER_UNIT = URL + "task/getTaskListByLeaderUnit";

    public static final String TASK_LIST_BY_REPORT_UNIT = URL + "task/getTaskListByReportUnit";


//    public static final String TASK_LIST_BY_CATEGORY = URL + "task/getTaskListByCategory";

    public static final String TASK_LIST_BY_LEADER_UNIT = URL + "task/getLeaderUnitTaskList";

    public static final String REPORT_LIST_BY_LEADER_UNIT = URL + "task/getReportTaskList";

    public static final String TASK_LIST_BY_STATUS_AND_MONTH = URL + "task/getLeaderUnitMonthTaskList";

    public static final String TASK_LIST_BY_LEADER_AND_STATUS_MONTH = URL + "task/getTaskListByLeaderUnitWithStatusAndMonth";


    public static final String LEADER_TASK_ALL_INFO_BY_LEADER = URL + "task/getTaskAllInfoByLeaderUnit";

    //获取Category下县长查看所有的任务
    public static final String TASK_GET_OFFICER_TASK_BY_UNIT_AND_CATEGORY = URL + "task/getOfficerTaskByUnitIdAndCateory";

    //获取Category下副县长分管的任务
    public static final String TASK_GET_LEADER_TASK_BY_UNIT_AND_CATEGORY_SCOPE_PROGRESS = URL + "task/getLeaderTaskByUnitIdAndCateoryScopeProgress";

    //政府工作报告月报
    public static final String TASK_GET_WORK_REPORT = URL + "task/getMonthWorkReport";

    //重大决策部署报告月报
    public static final String TASK_GET_Deployer_REPORT = URL + "task/getMonthDeployerReport";

    //提案建议报告月报
    public static final String TASK_GET_Proposal_REPORT = URL + "task/getMonthProposalReport";

    //其它类型完成未完成报告月报
    public static final String TASK_GET_FINISH_REPORT = URL + "task/getMonthFinishReport";

    //获取任务进展
    public static final String TASK_GET_TASKTRACE = URL + "task/getTaskTraceByTaskId";
    public static final String TRACE_LIST_BY_UNITTASK = URL + "trace/getTraceListByUnitTask";

    public static final String UNITTASK_BY_ID = URL + "unitTask/getUnitTask";

    public static final String CONTENT_UNITTASK_LIST_BY_STATUS = URL + "task/getContentUnitTaskListByStatus";

    public static String GET_NEW_VERSION = URL + "version/getNewVersion";

    public static final String NEW_CLUE = URL + "clue/newClue";

    public static final String PEOPLE_OPINION_LIST = URL + "opinion/getPeopleOpinions";

    public static final String COMMENT_TASK_LIST = URL + "comment/getCommentTasks";

        public static final String COMMENT_LIST_BY_TYPE = URL + "comment/getCommentsByType";

    public static final String NEW_COMMENT = URL + "comment/newComment";

    public static final String NEW_OPINIION = URL + "opinion/newOpinion";

    public static final String VOTE_OPINION_LIST = URL + "vote/getVoteOpinions";

    public static final String VOTE_OPINION= URL + "vote/getVoteOpinion";

    public static final String OPINION_VOTE = URL + "vote/newVote";

    public static final String SPACE_LIST_BY_TYPE = URL + "review/getReviews";

    public static final String DUTY_UNIT_SCORE_LIST = URL + "unit/getDutyUnitScores";

    public static final String TASK_LIST_BY_UNIT = URL + "task/getTaskListByUnit";

    public static final String TASK_LIST_BY_CATEGORY = URL + "task/getTaskListByCategory";

    public static final String TASK_LIST_FOR_PEOPLE = URL + "task/getTaskListForPeople";

    public static final String TASK_LIST_FOR_GOVERMENT_PUBLIC = URL + "task/getGovermentPublicTaskList";

    public static final String UNIT_TASK_LIST_FOR_STAY_SIGN     = URL + "task/getUnitStaySignReportTaskList";

    public static final String UNIT_TASK_LIST_FOR_STAY_COMPLETE = URL + "task/getUnitStayCompleteTaskList";

    public static final String UNIT_UN_ACCEPT_TASK_LIST = URL + "task/getUnAcceptTaskListByUnit";

    public static final String TASK_SUMMARY_BY_UNIT = URL + "task/getUnitTaskSummaryByUnit";

    public static final String ALL_LEADERS_SUMMARY = URL + "task/getAllLeaderTaskSummary";

    public static final String EADERS_SUMMARY = URL + "task/getLeaderTaskSummary";

    public static final String TASK_DETAIL = URL + "task/getTask";

    public static final String TASK_INTRO = URL + "task/getTaskIntro";


    public static final String UNIT_TASK_TRACES = URL + "trace/getTraceListByUnitTask";


    //上报任务
    public static final String TASK_UPGRADE = URL + "task/upgrade";

    public static final String UNITTASK_REPORT = URL + "reportTask/newReportTask";


    //获取任务和最后一次trace和leaders
    public static final String TASK_GET_WITH_LAST_TRACE_AND_LEADERS = URL + "task/getWithLastTraceAndLeaders";

    //获取任务数量
    public static final String TASK_COUNT = URL + "task/getTaskCountByUnitId";

    //提报任务
    public static final String TRACE_CREATE = URL + "trace/create";

    //提报任务
    public static final String TRACE_NEW = URL + "trace/newTrace";

    public static final String NEW_DONE_TRACE = URL + "trace/newDoneTrace";

    public static final String NEW_RUSH_TRACE = URL + "trace/newRushTrace";

    public static final String NEW_VERIFY_TRACE = URL + "trace/newVerifyTrace";

    public static final String NEW_LEADER_TRACE = URL + "trace/newLeaderTrace";

    public static final String NEW_REPORT_TRACE = URL + "trace/newReportTrace";

    public static final String NEW_ACCEPT_TASK_TRACE = URL + "trace/newAcceptTaskTrace";

    //部门报送至平台
    public static final String NEW_UNIT_CONTENT_TRACE = URL + "trace/newUnitContentTrace";

    //提报内部任务内容
    public static final String UNIT_STAY_SIGNREPORT_TRACE = URL + "trace/newUnitStaySignReportContentTrace";
    //内部任务退回
    public static final String UNIT_STAY_COMPLETE_TRACE = URL + "trace/newUnitStayCompleteContentTrace";


    public static final String UNITCONTENT_COMMIT_BACK = URL + "unitContent/commitBack";

    public static final String UNITCONTENT_RE_COMMIT = URL + "unitContent/recommit";

    //获取部门内部待完善或待手签的trace
    public static final String GET_UNIT_STAY_TRACE = URL + "trace/getUnitStayTrace";

    //获取待完善内容的trace
    public static final String GET_UNIT_STAY_COMPLETE_TRACE = URL + "trace/getUnitStayCompleteTrace";

    //获取待审核报送的trace
    public static final String GET_UNIT_STAY_SIGNREPORT_TRACE = URL + "trace/getUnitStaySignReportTrace";


    //获取所有未审核的Trace
    public static final String TRACE_UNVIRIFY = URL + "trace/getAllUnVirifyTrace";

    //获取trace，且带task
    public static final String TRACE_GET_WITH_TASK = URL + "trace/getWithTask";

    //审核任务
    public static final String TRACE_VERIFY = URL + "trace/verify";

    //Trace详情
    public static final String TRACE_DETAIL = URL + "trace/getWithTaskAndGrade";

    //获取消息
    public static final String MESSAGE_GET = URL + "message/get";

    //获取消息列表
    public static final String MESSAGE_GET_ALL = URL + "message/getAllMessage";

    //提建议
    public static final String ADVICE_CREATE = URL + "advice/create";

    //部门统计
    public static final String GATHER_UNIT = URL + "gather/getUnitGatherById";

    //部门任务详细扣分
    public static final String GATHER_DETAIL = URL + "gather/getDetailByUnitIdAndCateogry";

    //部门任务详细扣分
    public static final String UNIT_SELECT = URL + "unit/getUnitsByParentId";

    //所有部门
    public static final String UNIT_ALL = URL + "unit/getAll";

    //获取部门信息
    public static final String GET_UNIT = URL + "unit/getUnit";

    //领导部门
    public static final String UNIT_LEADER_LIST = URL + "unit/getLeaderUnits";
    //查看版本
    public static final String VERSION = URL + "version/getVersion";

    //查看版本
    public static final String UPDATE_VERSION = URL + "version/getNewVersion";

    //搜索
    public static final String SEARCH = URL + "task/search";


}
