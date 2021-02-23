<?php
class Task extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    // -2: 内部退回
    // -1: 内部审核

    // 0：未领取，
    // 1：已领取， 
    // 11: 未响应
    

    // 20：上报领导  
    // 21：领导批示，   
    
    // 31：已报送，          
    
    // 50：已逾期，
    // 51：系统催报，
    // 52：督查催报                 
    
    // 71：已审核(正常)，
    // 72：进度缓慢，
    // 73：进度较快， 
    // 74：已退回，       
    
    // 91：完成

    // 101: 加分

    //创建主任务
    public function newTask() {
        $data = $this->input->post();
        $unitIds = $this->input->post('unitIds');
        $status = $this->input->post('status');
        unset($data['unitIds']);
        unset($data['status']);
        $id = $this->task_model->create_id($data);
        $this->load->model('unittask_model');
        if ($data['category'] == 11 && $status >= 72) {
            //创建政务公开
            $this->unittask_model->newUnitTasksWithStatus($id, $unitIds, $status);
        } else {
            //创建普通任务
            $this->unittask_model->newUnitTasks($id, $unitIds);
        }
        $task = $this->task_model->getTask($id);
        $this->set_content(0, '创建成功', $task);
    }

    //更新主任务
    public function update($taskId = 0) {
        if ($taskId) {
            $data = $this->input->post();
            $unitIds = $data['unitIds'];
            unset($data['unitIds']);
            unset($data['unitTasks']);
            $id = $this->task_model->update_where($data, array('id' => $taskId));

            $this->load->model('unittask_model');
            $rows = $this->unittask_model->udpateTaskUnits($taskId, $unitIds);

            $task = $this->task_model->getTask($taskId);
            $this->set_content(0, '更新成功', $task);
        } else {
            $this->set_content(-1, '更新失败', array('id' => $taskId));
        }
    }


    //删除
    public function delete($taskId = 0) {
        $rows = $this->task_model->delete(array('id' => $taskId));
        if ($rows) {
             $this->set_content(0, '删除成功', array('rows' => $rows));
        } else {
            $this->set_content(-1, '删除失败', array('rows' => $rows));
        }
    }

    //完成调度
    public function done($taskId = 0) {
        $rows = $this->task_model->doneTask($taskId);
        if ($rows >= 0) {
            $this->set_content(0, '调度完成', array('rows' => $rows));
        } else {
            $this->set_content(-1, '调度失败', array('rows' => $rows));
        }
    }


    //根据任务类型获取所有任务列表
    public function getTaskListByCategory($category = 0) {
        $tasks = $this->task_model->getTaskList($category);
        $this->set_content(0, '获取成功', $tasks);
    }


    //根据群众端民生在线事项
    public function getTaskListForPeople() {
        $tasks = $this->task_model->getTaskListForPeople();
        $this->set_content(0, '获取成功', $tasks);
    }

    //获取民生在线（意见建议投票项的完成情况）
    public function getPeopleProgressTaskList() {
        $tasks = $this->task_model->getPeopleProgressTaskList();
        $this->set_content(0, '获取成功', $tasks);
    }

    //获取政务督查公开事项：category=1 || 11类别的（11手动发布的公开事项）
    public function getGovermentPublicTaskList($progressStatus = 0) {
        $tasks = $this->task_model->getGovermentPublicTaskList($progressStatus);
        $this->set_content(0, '获取成功', $tasks);
    }

    //根据任务类型获取所有任务列表
    public function getTaskListByFilter() {
        $status = $this->input->post('status');
        $category = $this->input->post('category');
        $leaderUnitId = $this->input->post('leaderUnitId');
        $tasks = $this->task_model->getTaskListByFilter($category, $leaderUnitId, $status);
        $this->set_content(0, '获取成功', $tasks);
    }


    //获取任务信息
    public function getTask($taskId)
    {/*{{{*/
        $result = $this->task_model->getTask($taskId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //获取部门责任事项
    public function getUnitTask($unitTaskId = 0)
    {/*{{{*/
        $result = $this->task_model->getUnitTask($unitTaskId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //获取主体任务基本信息
    public function getTaskIntro($taskId)
    {/*{{{*/
        $result = $this->task_model->get_where(array('id' => $taskId));
        $result = count($result) == 1 ? $result[0] : array();
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //我评价的任务
    public function getMyCommentTaskList($userId = 0) {
        $result = $this->task_model->getMyCommentTaskList($userId);
        $this->set_content(0, '获取成功', $result);
    }

    //我的线索任务
    public function getMyClueTaskList($userId = 0) {
        $result = $this->task_model->getMyClueTaskList($userId);
        $this->set_content(0, '获取成功', $result);
    }


    //我的意见任务
    public function getMyOpinionTaskList($userId = 0) {
        $result = $this->task_model->getMyOpinionTaskList($userId);
        $this->set_content(0, '获取成功', $result);
    }


    //我的关注的事项
    function getMyFavoriteTaskList($userId = 0) {
        $result = $this->task_model->getMyFavoriteTaskList($userId);
        $this->set_content(0, '获取成功', $result);
     }

    //领导批注的事项
    public function getMyNotationTaskList($unitId = 0) {
        $result = $this->task_model->getMyNotationTaskList($unitId);
        $this->set_content(0, '获取成功', $result);
    }


    //获取单位的所有任务
    public function getTaskListByUnit($category = 0, $unitId = 0) {
        $result = $this->task_model->getTaskList($category, $unitId);
        $this->set_content(0, '获取成功', $result);
    }


    //获取单位未领取任务列表
    public function getUnAcceptTaskListByUnit($category = 0, $unitId = 0) {
        $result = $this->task_model->getUnAcceptTaskListByUnit($category, $unitId);
        $this->set_content(0, '获取成功', $result);
    }


    //获取单位的待修改完善的任务
    public function getUnitStayCompleteTaskList($category = 0, $unitId = 0) {
         $tasks = $this->task_model->getStayDealTaskList($category, $unitId, -2);
        $this->set_content(0, '获取成功', $tasks);
    }

    //获取单位的待手签报送的任务
    public function getUnitStaySignReportTaskList($category = 0, $unitId = 0) {
         $tasks = $this->task_model->getStayDealTaskList($category, $unitId, -1);
        $this->set_content(0, '获取成功', $tasks);
    } 


    //获取上报给领导的任务(领导部门unitId)
    public function getReportTaskList($category = 0, $leaderUnitId = 0)
    {/*{{{*/
        $result = $this->task_model->getReportTaskListByLeaderUnit($category, $leaderUnitId);
        $this->set_content(0, '获取成功', $result);

    }/*}}}*/


    public function deleteTasks() {
        $ids = $this->input->post('ids');
        if ($ids && count($ids) > 0) {
            $this->task_model->deleteTasks(implode(',', $ids));
            $this->set_content(0, '删除成功', array('id' => 0));
        } else {
            $this->set_content(-1, '删除失败', array());
        }
    }


    // //获取分管的的任务的UniTasks
    // public function getLeaderUnitTaskListByChildTask($childTaskId = 0)
    // {/*{{{*/
    //     $result = $this->task_model->getUnitTaskListByChildTask($childTaskId);
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/


    //分管的工作
    public function getLeaderUnitTaskList($category = 0, $leaderUnitId = 0)
    {/*{{{*/
        $result = $this->task_model->getTaskListByLeaderUnit($category, $leaderUnitId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


     //单位任务的汇总信息
    public function getUnitTaskSummaryByUnit($category = 0, $unitId) {
        $taskSummary = $this->task_model->getUnitTaskSummary($category, $unitId);
        $traceSummary = $this->task_model->getUnitTraceSummary($category, $unitId);
        $staySummary = $this->task_model->getUnitStayTaskSummary($category, $unitId);
        $unAcceptSummary = $this->task_model->getUnAcceptTaskSummary($category, $unitId);
        $this->set_content(0, '获取成功', array(
            'taskSummary' => $taskSummary, 
            'traceSummary' => $traceSummary, 
            'staySummary' => $staySummary,
            'unAcceptSummary' => $unAcceptSummary)
            );
    }

 


    //领导分管任务的汇总信息(任务汇总及月汇总)
    public function getLeaderTaskSummary($category = 0, $unitId, $month) {
        $result = $this->task_model->getLeaderSummary($category, $unitId, $month);
        $this->set_content(0, '获取成功', $result);
    }

    //领导分管任务的月汇总信息
    public function getLeaderMonthTaskSummary($category = 0, $unitId, $month) {
        $result = $this->task_model->getLeaderMonthSummary($category, $unitId, $month);
        $this->set_content(0, '获取成功', $result);
    }

    //所有领导的分管信息汇总
    public function getAllLeaderTaskSummary($category = 0, $month) {
        $result = $this->task_model->getAllLeaderSummary($category, $month);
        $this->set_content(0, '获取成功', $result);
    }


    //获取X月X状态任务列表
    public function getLeaderUnitMonthTaskList($category = 0, $leaderUnitId = 0, $status = 0, $month = 0) {

        $result = $this->task_model->getLeaderUnitMonthTaskList($category, $leaderUnitId, $status, $month);
        $this->set_content(0, '获取成功', $result);
    }








    //县长分管的工作(根据月份和状态)
    // public function getTaskListByLeaderUnitWithStatusAndMonth($category = 0, $leaderUnitId = 0, $status = 0, $month)
    // {/*{{{*/
    //     $result = $this->task_model->getTaskListByLeaderUnitWithStatusAndMonth($category, $leaderUnitId, $status, $month);
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    //月报
    // public function getTaskMonthSummaryByMonth($category = 0, $leaderUnitId = 0, $month) {
    //     $result = $this->task_model->getMonthSummaryByLeaderUnit($category, $leaderUnitId, $month);
    //     $this->set_content(0, '获取成功', $result);
    // }


    //单位任务的汇总信息
    // public function getUnitTaskSummaryByUnit($category = 0, $unitId) {
    //     $taskCount = $this->task_model->getUnitTaskCountInfo($category, $unitId);
    //     $traceCount = $this->task_model->getUnitTraceCountInfo($category, $unitId);
    //     $unitContentCount = $this->task_model->getUnitContentCountInfo($category, $unitId);
    //     $this->set_content(0, '获取成功', array('taskCount' => $taskCount, 
    //         'traceCount' => $traceCount, 
    //         'unitContentCount' => $unitContentCount));
    // }


 //获取部门任务分类任务( $accept是否已申领)
    // public function getTaskByUnitAndCategoryWithAccept($unitId, $category = 0, $accept = 0)
    // {/*{{{*/
    //     if ($accept == 0 || $accept == 1)
    //     {
    //         if ($category == 0)
    //         {
    //             //全部
    //             $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'accept' => $accept, 'valid' => 1, 'YEAR(createtime)' => '2018'));
    //         }
    //         else
    //         {
    //             $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $category, 'accept' => $accept, 'valid' => 1, 'YEAR(createtime)' => '2018'));
    //         }
    //         $this->set_content(0, '获取成功', $result);
    //     }
    //     else
    //     {
    //         $this->set_content(-1, '参数错误', '');
    //     }
    // }/*}}}*/


    // public function getAcceptedTaskByUnitAndCategory($unitId, $categoryId)
    // {/*{{{*/
    //     //未领取
    //     $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $categoryId, 'accept' => '1', 'valid' => '1'));
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    // public function getWithLastTraceAndLeaders($taskId)
    // {/*{{{*/
    //     $task = $this->task_model->get($taskId);

    //     //trace
    //     $sql = 'select b.* from tbl_task a, tbl_trace b where b.valid = 1 and a.id = b.taskId and b.type = 0 and a.id = '.$taskId.' order by id desc limit 1;';
    //     $this->load->model('trace_model');
    //     $trace = $this->trace_model->query($sql);
    //     $trace = count($trace) == 1 ? $trace[0] : array('id' => 0, 'content' => '', 'attachment' => '');

    //     //user
    //     $sql = 'select a.* , b.logo from tbl_user a, tbl_unit b where a.unitId = b.id and b.role < 3 order by b.id;';
    //     $this->load->model('user_model');
    //     $users = $this->user_model->query($sql);

    //     $data = array('task' => $task, 'trace' => $trace, 'users' => $users);
    //     $this->set_content(0, '获取成功', $data);
    // }/*}}}*/

    //转移到unitTask里（创建一条trace）
    // public function accept($taskId, $userId)
    // {/*{{{*/
    //     $params = array(
    //         'taskId' => $taskId,
    //         'userId' => $userId,
    //         'category' => $this->input->post('category'),
    //         'unitId' => $this->input->post('unitId'),
    //         'unitName' => $this->input->post('unitName'),
    //         'content' => $this->input->post('content'),
    //         'type' => $this->input->post('type'),
    //     );
    //     $this->load->model('taskuserrelation_model', 'userTask', true);
    //     //开启事务
    //     $this->db->trans_start();
    //     //user与task关联
    //     $this->userTask->create_id(array('taskId' => $taskId, 'userId' => $userId));
    //     //task状态更新
    //     $this->task_model->update_where(array('accept' => 1), array('id' => $taskId));
    //     //创建trace
    //     $this->load->model('trace_model');
    //     $id = $this->trace_model->create_id($params);
    //     $this->db->trans_complete();
    //     if ($id > 0)
    //     {
    //         $this->set_content(0, '申领成功', array('taskId' => $taskId));
    //     }
    //     else
    //     {
    //         $this->set_content(-1, '申领失败', array('id' => 0));
    //     }
    // }/*}}}*/

    //trace列表，（转移到trace里）
    // public function getTaskTraceByTaskId($taskId)
    // {/*{{{*/
    //     $task = $this->task_model->get($taskId);
    //     $this->load->model('trace_model', '', true);
    //     $traces = $this->trace_model->get_where_with_order(array('taskId' => $taskId));
    //     $sql = 'select a.*, (select logo from tbl_unit where id = a.unitId) as logo from tbl_trace a where a.valid = 1 and a.taskId ='.$taskId.' order by id desc';
    //     $traces = $this->trace_model->query($sql);
    //     if (count($task) > 0)
    //     {
    //         array_unshift($traces, $task);
    //         $this->set_content(0, '获取成功', $traces);
    //     }
    //     else
    //     {
    //         $this->set_content(-1, '没有该任务', array('id' => 0));
    //     }
    // }/*}}}*/

    //上报领导（转移到unitask）
    // public function upgrade()
    // {/*{{{*/
    //     $taskId = $this->input->post('taskId');
    //     $userIds = $this->input->post('userIds');
    //     $userIds = explode(',',$userIds); 
    //     $this->load->model('taskuserrelation_model', 'userTask', true);
        
    //     $data = array();
    //     foreach($userIds as $userId)
    //     {
    //         $taskUser = array('taskId' => $taskId, 'userId' => $userId);
    //         $data[] = $taskUser;
    //     }
    //     $rows = $this->userTask->create_batch($data);
    //     if ($rows > 0)
    //     {
    //         $this->set_content(0, '上报成功', array('taskId' => $taskId));
    //     }
    //     else
    //     {
    //         $this->set_content(-1, '上报失败', array('id' => 0));
    //     }
    // }/*}}}*/

   // //获取单位的待手签或待修改的任务
    // public function getUnitTaskListByStatus($category = 0, $unitId = 0, $status = -1) {
    //      $unitTasks = $this->task_model->getContentUnitTaskListByStatus($category, $unitId, $status);

    //      //根据状态获取所有的unitcontent
    //     $this->load->model('unitcontent_model');
    //     $unitContents = $this->unitcontent_model->get_where(array('status' => $status));


    //     for($n = 0; $n < count($unitTasks); $n++) {
    //         $unitContent = $this->findUnitContent($unitTasks[$n]['unitTask']['unitTaskId'], $unitContents);
    //         $unitTasks[$n]['unitContent'] = $unitContent;
    //     }

    //     $this->set_content(0, '获取成功', $unitTasks);
    // }

    // private function findUnitContent($unitTaskId = 0, $unitContents = array()) {
    //     foreach ($unitContents as $value) {
    //         if ($unitTaskId == $value['unitTaskId']) return $value;
    //     }
    //     return array();
    // }


    // //根据category获取所有的task
    // public function getTaskListByCategory($category = 0)
    // {/*{{{*/
    //     $result = $this->task_model->getTaskListByCategory($category);
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/


    //根据category获取已审核的task
    public function getTaskListByVerify($category = 0, $verify)
    {/*{{{*/
        //$sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where u.id = t.unitId and t.category = '.$category.' order by t.id;';
        //$sql = 'select t.*, u.name as unitName, (select createtime from tbl_trace where taskId=t.id order by createtime desc limit 1) as lastTime from tbl_task t, tbl_unit u where u.id = t.unitId and t.category = '.$category.' and t.accept = 1 order by lastTime desc;';
        // $sql = 'select tt.*,uu.name as unitName from tbl_task tt, tbl_unit uu where tt.id in (select taskId from (select * from (select * from tbl_trace order by createtime desc) as t group by t.taskId) as a where a.status > 0 and a.status < 4) and tt.groupTask = "" and tt.valid = 1 and uu.id = tt.unitId and tt.category = '.$category.' and YEAR(tt.createtime) > 2017;';
        // $unGroupResult = $this->task_model->query($sql);
        // $sql = 'select tt.*,uu.name as unitName from tbl_task tt, tbl_unit uu where tt.id in (select taskId from (select * from (select * from tbl_trace order by createtime desc) as t group by t.taskId) as a where a.status > 0 and a.status < 4) and tt.groupTask != "" and tt.valid = 1 and uu.id = tt.unitId and tt.category = '.$category.' and YEAR(tt.createtime) > 2017 order by groupTask;';
        // $groupResult = $this->task_model->query($sql);
        // foreach($groupResult as $task)
        // {
        //     $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and t.groupTask = '".$task->groupTask."' and YEAR(t.createtime) > 2017 order by t.id desc;";
        //     $groupTasks = $this->task_model->query($sql);
        //     $task->groupTasks = $groupTasks;
        // }
        // $result = array_merge($groupResult, $unGroupResult);
        // $this->set_content(0, '获取成功', $result);
        $result = $this->task_model->getTaskListByVerify($category, $verify);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    protected $categoryNames = array('', '政府工作报告', '市委市政府重大决策部署', '建议提案', '会议议定事项', '领导批示', '专项督查', '重点项目');

    public function getTaskAllSummary() {
        $summarys = array();
        for($category = 1; $category < 8; $category++) {
            $summary = array();
            $summary['category'] = $category;
            $summary['categoryName'] = $this->categoryNames[$category];
            $summary['infos'] = $this->task_model->getTraceStatusCountInfo($category);
            $summarys[] = $summary;
        }
        $this->set_content(0, '获取成功', $summarys);
    }

    //县长首页，获取统计数据和副县长
    public function getTaskCountWithLeadersByUnitId($unitId, $category = 0)
    {/*{{{*/
		$in = $unitId == 2 ? '' : ' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.')';
        //1：完成，2：快速，3：正常，4：缓慢
        $sql = 'select progress, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY progress;';
        //progress对应得任务数
        $stateCount = $this->task_model->query($sql);

        $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.valid = 1 and a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.' limit 1) and YEAR(a.createtime) = 2018;';
        $reportCount = $this->task_model->query($sql);
        //上报任务数量
        $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

        $sql = 'select b.* from tbl_unit b where b.role <= 2 order by b.id;';
        //副县长
        $leaders = $this->task_model->query($sql);

        $sql = 'select sequence, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY sequence;';
        //决策部署排名
        $sequence= $this->task_model->query($sql);
        
        $sql = 'select childType, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY childType;';
        //建议提案分类
        $type = $this->task_model->query($sql);
        $sql = 'select count(*) as count from tbl_task where valid = 1 '.$in.' and YEAR(createtime) = 2018 and category = '.$category;  
        $sumCount = $this->task_model->query($sql);
        //总任务数
        $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

		//分管任务数
        $sql = 'select count(*) as count from tbl_task where valid = 1 and unitId in (select unitId from tbl_unitrelation where parentId= '.$unitId.') and YEAR(createtime) = 2018 and category = '.$category;  
        $divisionCount = $this->task_model->query($sql);
        $divisionCount = count($divisionCount) > 0 ? $divisionCount[0] : array('count' => 0);

        $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'leaders' => $leaders, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type'=>$type, 'divisionCount' => $divisionCount);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getTaskCountByUnitId($unitId, $category)
    {/*{{{*/
        //1：完成，2：快速，3：正常，4：缓慢
        $sql = 'select progress, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY progress;';
        $stateCount = $this->task_model->query($sql);
        $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.valid = 1 and a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.');';
        $reportCount = $this->task_model->query($sql);
        $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

        $sql = 'select sequence, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY sequence;';
        $sequence= $this->task_model->query($sql);

        $sql = 'select count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.');';
        $sumCount = $this->task_model->query($sql);
        $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

        $sql = 'select childType, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY childType;';
        $type = $this->task_model->query($sql);

        $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type' => $type);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

	//副县长分管工作
    public function getLeaderTaskByUnitIdAndCateory($unitId, $category, $progress = 0)
    {/*{{{*/
		$psql = $progress > 0 ? ' and progress = '.$progress : '';
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask = "" and t.category = '.$category.' and t.unitId = u.id '.$psql.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(t.createtime) = 2018 order by id desc;';
        $unGroupResult = $this->task_model->query($sql);
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask != "" and t.category = '.$category.' and t.unitId = u.id '.$psql.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(t.createtime) = 2018 group by groupTask order by id desc;';
        $groupResult = $this->task_model->query($sql);
        foreach($groupResult as $task)
        {
            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and YEAR(t.createtime) = 2018 and t.groupTask = '".$task->groupTask."' order by t.id desc;";
            $groupTasks = $this->task_model->query($sql);
            $task->groupTasks = $groupTasks;
        }
        $result = array_merge($groupResult, $unGroupResult);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/



    //副县长首页的信息：分管工作summary，月报summary， 风管任务tasks
    public function getTaskAllInfoByLeaderUnit($category = 0, $leaderUnitId = 0) {
        $taskSummary = $this->task_model->getTaskSummaryByLeaderUnit($category, $leaderUnitId);
        $monthSummary = $this->task_model->getMonthSummaryByLeaderUnit($category, $leaderUnitId, date('n'));
        // $tasks = $this->task_model->getTaskListByLeaderUnit($category, $leaderUnitId);
        $this->set_content(0, '获取成功', array('taskSummary' => $taskSummary, 'monthSummary' => $monthSummary));
    }



    //汇总统计
    public function getPlatformSummary() {
        $this->load->model('user_model');
        $userCount = $this->user_model->getPlatformUserCount();
        $this->load->model('unittask_model');
        $unitTaskCount = $this->unittask_model->getPlatformUnitTaskCount();
        $this->load->model('unit_model');
        $unitCount = $this->unit_model->getPlatformUnitCount();
        $massUserCount = $this->user_model->getPlatformMassUserCount();
        $result = array_merge($userCount, $unitCount, $unitTaskCount, $massUserCount);
        $this->set_content(0, '获取成功', $result);
    }


    public function getTasksMonthStatusSummary() {
        $this->load->model('monthunittask_model');
        $months = $this->monthunittask_model->getTasksMonthStatusSumarry(1);
        $this->set_content(0, '获取成功', $months);
    }


    public function getTasksMonthCountSummary() {
        $this->load->model('monthunittask_model');
        $monthCounts = $this->monthunittask_model->getTasksMonthCountSummary();
        $this->set_content(0, '获取成功', $monthCounts);
    }


    // public function getLeaderTaskByUnitIdAndCateoryScopeProgress($unitId, $category)
    // {/*{{{*/
    //     $sql = 'select progress, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation parentId = '.$unitId.') and YEAR(createtime) = 2018 GROUP BY progress;';
    //     $stateCount = $this->task_model->query($sql);
    //     $result = array();
    //     for ($progress = 1; $progress < 5; $progress++)
    //     {
    //         //$sql = 'select * from tbl_task where category = '.$category.' and unitId in (select id from tbl_unit where progress = '.$progress.' and parentId = '.$unitId.') order by id desc;';
    //         $sql = 'select * from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where  parentId = '.$unitId.') and progress = '.$progress.' and YEAR(createtime) = 2018 order by id desc;';
    //         $tasks = $this->task_model->query($sql);
    //         $result[$progress] = $tasks;
    //     }
    //     $result['stateCount'] = $stateCount;

    //     $sql = 'select count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(createtime) = 2018;';
    //     $sumCount = $this->task_model->query($sql);
    //     $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);
    //     $result['sumCount'] = $sumCount;

    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    // //政府工作报告月报
    // public function getMonthWorkReport($unitId, $category, $date)
    // {/*{{{*/
    //     $result = array();
    //     for ($progress = 1; $progress < 5; $progress++)
    //     {
    //         $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = ".$progress." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId)";
    //         $tasks = $this->task_model->query($sql);
    //         $result[$progress] = $tasks;
    //     }
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    // //市委市政府重大部署月报
    // public function getMonthDeployerReport($unitId, $category, $date)
    // {/*{{{*/
    //     $result = array();
    //     for ($sequence = 1; $sequence < 8; $sequence++)
    //     {
    //         //$sql = "select * from tbl_task where unitId in (select id from tbl_unit where parentId = ".$unitId.")  and sequence = ".$sequence." and category = ".$category." and date_format(createtime,'%Y-%m') <= date_format(".$date.",'%Y-%m');";

    //         $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and sequence = ".$sequence." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";

    //         $tasks = $this->task_model->query($sql);
    //         $result[$sequence] = $tasks;
    //     }
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    // //建议提案月报
    // public function getMonthProposalReport($unitId, $category, $date)
    // {/*{{{*/
    //     $result = array();
    //     for ($childType = 1; $childType < 3; $childType++)
    //     {
    //         //$sql = "select * from tbl_task where unitId in (select id from tbl_unit where parentId = ".$unitId.")  and childType = ".$childType." and category = ".$category." and date_format(createtime,'%Y-%m') <= date_format(".$date.",'%Y-%m');";

    //         $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and childType = ".$childType." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";
    //         $tasks = $this->task_model->query($sql);
    //         $result[$childType] = $tasks;
    //     }
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    // //其它类型完成未完成月报
    // public function getMonthFinishReport($unitId, $category, $date)
    // {/*{{{*/
    //     $result = array();
    //     $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
    //     $tasks = $this->task_model->query($sql);
    //     $result['finish'] = $tasks;

    //     $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress > 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
    //     $tasks = $this->task_model->query($sql);
    //     $result['unFinish'] = $tasks;
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    public function search()
    {/*{{{*/
        $keyword = $this->input->post('keyword');
        $unitId = $this->input->post('unitId');
        //0：默认，1：县长，2：副县长，3：督查，4：单位
        $role = $this->input->post('role');
        if (isset($keyword) && strlen($keyword) && isset($unitId) && isset($role))
        {
//            SELECT t.id, t.name, t.plan, t.category, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and t.unitId = u.id and (t.name LIKE '%asdf%' or t.plan like '%asdf%') order by t.id desc;
            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u ";
            $sql .= "WHERE t.valid = 1 and t.unitId = u.id and "; 
            $sql .= "(t.name LIKE '%".$keyword."%' or t.plan like '%".$keyword."%') ";
            //部门只查看自己下得任务，副县长下得，督查和县长看所有
            if ($role == 4)
            {
                $sql .= " and t.unitId = ".$unitId." ";
            }
            else if ($role == 2)
            {
                $sql .= " and t.unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.") ";
            }
            $exSql = " and t.groupTask = '' order by t.id desc;";
            $unGroupResult = $this->task_model->query($sql.$exSql);

            $exSql = " and t.groupTask != '' group by t.groupTask order by t.id desc;";
            $groupResult = $this->task_model->query($sql.$exSql);
            foreach($groupResult as $task)
            {
                $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.groupTask = '".$task->groupTask."'";
                if ($role == 2)
                {
                    $sql .= " and t.unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.") ";
                }
                $sql .= " order by t.id desc";
                $groupTasks = $this->task_model->query($sql);
                $task->groupTasks = $groupTasks;
            }
            $result = array_merge($groupResult, $unGroupResult);
            $this->set_content(0, '获取成功', $result);
        }
        else
        {
            $this->set_content(-1, '请输入查询关键字', array());
        }

//        $sql = "select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and u.id = t.unitId and t.groupTask = '' and t.category = ".$category." order by t.id desc;";
//        $unGroupResult = $this->task_model->query($sql);
//        $sql = "select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and u.id = t.unitId and t.groupTask != '' and t.category = ".$category." group by groupTask order by t.id desc;";
//        $groupResult = $this->task_model->query($sql);
//        foreach($groupResult as $task)
//        {
//            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and t.groupTask = '".$task->groupTask."' order by t.id desc;";
//            $groupTasks = $this->task_model->query($sql);
//            $task->groupTasks = $groupTasks;
//        }
//        $result = array_merge($groupResult, $unGroupResult);
    }/*}}}*/

}
?>
