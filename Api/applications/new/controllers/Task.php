<?php
class Task extends CA_Controller {

// status状态：
// 0:未领取（发布任务后默认生成一条占位trace的状态）
// 1:已领取 （领取任务）
// 2:已逾期
// 3:系统催报
// 4:督查催报
// 5:领导批示
// 6:已报送（报送完成/未审核）
// 7:退回重报
// 8:审核正常
// 9:审核缓慢
// 10:审核较快
// 11:调度完成
// 12:上报领导


    function __construct()
    {/*{{{*/
        parent::__construct();

    }/*}}}*/


    //创建住任务
    public function create() {
        $data = $this->input->post();
        $id = $this->task_model->create_id($data);
        $this->set_content(0, '创建任务成功', array('id' => $id));
    }

    //创建子任务
    public function createChildUnitsTask() {
        $data = $this->input->post();
        $unitIds = $data['unitIds'];
        array_unshift($data, 'unitIds');
        $this->load->model('childtask_model');
        $this->load->model('unittask_model');

        //开启事务
        $this->db->trans_start();
        $cTaskId = $this->childtask_model->create_id($data);

        $unitTasks = array();
        foreach ($unitIds as $unitId) {
            $unitTasks[] = array('unitId' => $unitId, 'ctaskId' => $cTaskId, 'unitName' => $data['unitName']);
        }

        $unitTaskIds = $this->task_model->create_batch($unitTasks);
        //提交事务
        $complete = $this->db->trans_complete();

        $this->set_content(0, '创建任务成功', array('id' => $cTaskId, 'unitTaskIds' => $unitTaskIds));
    }

    //更新主任务
    public function updateTask($taskId = 0) {
        $data = $this->input->post();
        $state = $this->task_model->update_where($data, array('id' => $taskId));
        if ($state >= 0) {
            $this->set_content(0, '创建任务成功', array('id' => 0));
        } else {
            $this->set_content(-1, '创建任务成功', array('id' => -1));
        }
    }

    //删除主任务
    public function deleteTaskById($taskId = 0) {
        // $this->load->model('childtask_model');
        // $this->load->model('unittask_model');
        // $this->load->model('trace_model');
        // //开启事务
        // $this->db->trans_start();
        // $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
        // $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
        // //.....
        // // unittak , trace
        // //提交事务
        // $complete = $this->db->trans_complete();
        $result = $this->task_model->deleteTaskById($taskId);
        $this->set_content(0, '删除成功', $result);
    }

    //删除子任务
    public function deleteChildTaskById($childTaskId = 0) {
        // $this->load->model('childtask_model');
        // $this->load->model('unittask_model');
        // $this->load->model('trace_model');
        // //开启事务
        // $this->db->trans_start();
        // $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
        // $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
        // //.....
        // // unittak , trace
        // //提交事务
        // $complete = $this->db->trans_complete();
        // $this->deleteTask($)
        $result = $this->task_model->deleteChildTasksByIds(array($childTaskId));
        $this->set_content(0, '删除成功', $result);
    }

    //删除部门任务
    public function deleteUnitTaskById($unitTaskId = 0) {
        // $this->load->model('childtask_model');
        // $this->load->model('unittask_model');
        // $this->load->model('trace_model');
        // //开启事务
        // $this->db->trans_start();
        // $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
        // $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
        // //.....
        // // unittak , trace
        // //提交事务
        // $complete = $this->db->trans_complete();
        $result = $this->task_model->deleteUnitTasksByIds(array($unitTaskId));
        $this->set_content(0, '删除成功', $result);
    }

    //删除trace
    public function deleteTraceById($traceId = 0) {
        // $this->load->model('childtask_model');
        // $this->load->model('unittask_model');
        // $this->load->model('trace_model');
        // //开启事务
        // $this->db->trans_start();
        // $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
        // $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
        // //.....
        // // unittak , trace
        // //提交事务
        // $complete = $this->db->trans_complete();
        $result = $this->task_model->deleteTracesByIds(array($traceId));
        $this->set_content(0, '删除成功', $result);
    }


    //获取单个主任详情
    public function get($taskId)
    {/*{{{*/
        $result = $this->task_model->getTask($taskId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //获取单个子任详情
    public function getChildTask($cTaskId)
    {/*{{{*/
        $result = $this->task_model->getChildTask($cTaskId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //获取单位的所有任务
    public function getUnitAllTasks($unitId = 0, $category = 0)
    {/*{{{*/
        $result = $this->task_model->getUnitAllTasks($unitId, $category);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //获取单位内部审核的所有任务
    // public function getUnitReviewTasks($unitId = 0, $category = 0) {
    //     $result = $this->task_model->getUnitReviewTasks($unitId, $category);
    //     $this->set_content(0, '获取成功', $result);
    // }


    //根据category获取所有的task
    public function getAllTasksByCategory($category)
    {/*{{{*/
        $result = $this->task_model->getAllTask($category);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //根据category获取已审核的task
    public function getTasksForVerify($verify = 0, $category = 0)
    {/*{{{*/
        $result = $this->task_model->getTasksForVerify($verify, $category);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //获取部门任务（接收和未接收）
    public function getUnitTasksForAccept($unitId = 0, $category = 0) {
        $unAcceptTasks = $this->task_model->getTasksByUnitWithAccept($unitId, $category, 0);
        $acceptedTasks = $this->task_model->getTasksByUnitWithAccept($unitId, $category, 0);
        $this->set_content(0, '获取成功', array('unAcceptTasks' => $unAcceptTasks, 'acceptedTasks' => $acceptedTasks));
    }


    //获取部门任务（接收或未接收）
    public function getUnitTasksByAccept($unitId = 0, $category = 0, $accept = 0) {
        $result = $this->task_model->getTasksByUnitWithAccept($unitId, $category, $accept);
        $this->set_content(0, '获取成功', $result);
    }

    public function accept($taskId, $userId)
    {/*{{{*/
        $params = array(
            'taskId' => $taskId,
            'userId' => $userId,
            'category' => $this->input->post('category'),
            'unitId' => $this->input->post('unitId'),
            'unitName' => $this->input->post('unitName'),
            'content' => $this->input->post('content'),
            'type' => $this->input->post('type'),
        );
        $this->load->model('taskuserrelation_model', 'userTask', true);
        //开启事务
        $this->db->trans_start();
        //user与task关联
        $this->userTask->create_id(array('taskId' => $taskId, 'userId' => $userId));
        //task状态更新
        $this->task_model->update_where(array('accept' => 1), array('id' => $taskId));
        //创建trace
        $this->load->model('trace_model');
        $id = $this->trace_model->create_id($params);
        $this->db->trans_complete();
        if ($id > 0)
        {
            $this->set_content(0, '申领成功', array('taskId' => $taskId));
        }
        else
        {
            $this->set_content(-1, '申领失败', array('id' => 0));
        }
    }/*}}}*/


    //上报领导部门
    public function reportTaskToLeaderUnit()
    {/*{{{*/
        $unitTaskId = $this->input->post('unitTaskId');
        $leaderUnitIds = $this->input->post('leaderUnitIds');
        $leaderUnitIds = explode(',',$leaderUnitIds); 
        $category = $this->input->post('category');

        $this->load->model('reporttask_model', 'report', true);
        $this->load->model('trace_model', 'trace', true);
        
        $traces = array();
        $reportTasks = array();
        foreach($leaderUnitIds as $leaderUnitId) {
            $traces[] = array('unitTaskId' => $unitTaskId, 'category' => $category, 'content' => '已上报领导', 'status' => 12);
            $reportTasks[] = array('unitTaskId' => $unitTaskId, 'category' => $category, 'leaderUnitId' => $leaderUnitId);
        }

        $this->db->trans_start();
        $this->report->create_batch($reportTasks);
        $this->trace->create_batch($traces);
        $complete = $this->db->trans_complete();
        
        if ($complete) {
            $this->set_content(0, '上报成功', array('id' => 0));
        } else {
            $this->set_content(-1, '上报失败', array('id' => 0));
        }
    }/*}}}*/


    //获取上报给领导的任务
    public function getReportLeaderUnitTasks($leaderUnitId = 0, $category = 0)
    {/*{{{*/
        $result = $this->task_model->getReportLeaderUnitTasks($leaderUnitId, $category);
        $this->set_content(0, '获取成功', $result);

    }/*}}}*/

    //更新项目调度状态（同时更新每个字任务的状态：trace增加一条完成状态）
    public function updateTaskStatus($taskId = 0, $isFinish = 0) {
        $this->load->model('trace_model');
        $taskStatus = $this->task_model->update_where(array('taskStatus' => ($isFinish ? 1 : 0)), array('id' => $taskId));
        $traceStatus = $this->trace_model->createForTaskFinished($taskId);
        $this->set_content(0, '获取成功', array('taskStatus' => $taskStatus, 'traceStatus' => $traceStatus));
    }


    //获取领导部门的汇总信息(任务数量，完成任务数量 ｜ 退回，逾期，缓慢次数 ｜ 上报工作数量)
    public function getLeadUnitSummaryInfo($unitId = 0, $category) {
        $result = $this->task_model->getLeadUnitSummaryInfo($unitId, $category);
        $this->set_content(0, '获取成功', $result);
    }

    //获取领导部门的汇总信息(任务数量，完成任务数量 ｜ 退回，逾期，缓慢次数 ｜ 上报工作数量)
    public function getAllLeadUnitSummaryInfos($category) {
        $this->load->model('unit_model');
        $result = $this->unit_model->select_where(array('role <' => 3));
        $units = array();
        foreach ($result as $unit) {
            $unit['summaryInfo'] = $this->task_model->getLeadUnitSummaryInfo($unit['id'], $category);
            $units[] = $unit;
        }
        $this->set_content(0, '获取成功', $units);
    }

  //   //县长首页，获取统计数据和副县长
  //   public function getTaskCountWithLeadersByUnitId($unitId, $category = 0)
  //   {/*{{{*/
		// $in = $unitId == 2 ? '' : ' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.')';
  //       //1：完成，2：快速，3：正常，4：缓慢
  //       $sql = 'select progress, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY progress;';
  //       //progress对应得任务数
  //       $stateCount = $this->task_model->query($sql);

  //       $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.valid = 1 and a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.' limit 1) and YEAR(a.createtime) = 2018;';
  //       $reportCount = $this->task_model->query($sql);
  //       //上报任务数量
  //       $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

  //       $sql = 'select b.* from tbl_unit b where b.role <= 2 order by b.id;';
  //       //副县长
  //       $leaders = $this->task_model->query($sql);

  //       $sql = 'select sequence, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY sequence;';
  //       //决策部署排名
  //       $sequence= $this->task_model->query($sql);
        
  //       $sql = 'select childType, count(*) as count from tbl_task where valid = 1 and category = '.$category.' '.$in.' and YEAR(createtime) = 2018 GROUP BY childType;';
  //       //建议提案分类
  //       $type = $this->task_model->query($sql);
  //       $sql = 'select count(*) as count from tbl_task where valid = 1 '.$in.' and YEAR(createtime) = 2018 and category = '.$category;  
  //       $sumCount = $this->task_model->query($sql);
  //       //总任务数
  //       $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

		// //分管任务数
  //       $sql = 'select count(*) as count from tbl_task where valid = 1 and unitId in (select unitId from tbl_unitrelation where parentId= '.$unitId.') and YEAR(createtime) = 2018 and category = '.$category;  
  //       $divisionCount = $this->task_model->query($sql);
  //       $divisionCount = count($divisionCount) > 0 ? $divisionCount[0] : array('count' => 0);

  //       $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'leaders' => $leaders, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type'=>$type, 'divisionCount' => $divisionCount);
  //       $this->set_content(0, '获取成功', $result);
  //   }/*}}}*/

  //   public function getTaskCountByUnitId($unitId, $category)
  //   {/*{{{*/
  //       //1：完成，2：快速，3：正常，4：缓慢
  //       $sql = 'select progress, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY progress;';
  //       $stateCount = $this->task_model->query($sql);
  //       $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.valid = 1 and a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.');';
  //       $reportCount = $this->task_model->query($sql);
  //       $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

  //       $sql = 'select sequence, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY sequence;';
  //       $sequence= $this->task_model->query($sql);

  //       $sql = 'select count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.');';
  //       $sumCount = $this->task_model->query($sql);
  //       $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

  //       $sql = 'select childType, count(*) as count from tbl_task where valid = 1 and category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY childType;';
  //       $type = $this->task_model->query($sql);

  //       $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type' => $type);
  //       $this->set_content(0, '获取成功', $result);
  //   }/*}}}*/

	// //副县长分管工作
 //    public function getLeaderTaskByUnitIdAndCateory($unitId, $category, $progress = 0)
 //    {/*{{{*/
	// 	$psql = $progress > 0 ? ' and progress = '.$progress : '';
 //        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask = "" and t.category = '.$category.' and t.unitId = u.id '.$psql.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(t.createtime) = 2018 order by id desc;';
 //        $unGroupResult = $this->task_model->query($sql);
 //        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask != "" and t.category = '.$category.' and t.unitId = u.id '.$psql.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(t.createtime) = 2018 group by groupTask order by id desc;';
 //        $groupResult = $this->task_model->query($sql);
 //        foreach($groupResult as $task)
 //        {
 //            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and YEAR(t.createtime) = 2018 and t.groupTask = '".$task->groupTask."' order by t.id desc;";
 //            $groupTasks = $this->task_model->query($sql);
 //            $task->groupTasks = $groupTasks;
 //        }
 //        $result = array_merge($groupResult, $unGroupResult);
 //        $this->set_content(0, '获取成功', $result);
 //    }/*}}}*/

    //获取领导部门分管工作
    public function getLeaderUnitTasks($leaderUnitId = 0, $category = 0) {
        $result = $this->task_model->getLeaderUnitTasks($leaderUnitId, $category);
        $this->set_content(0, '获取成功', $result);
    }

	//县长看所有工作
    public function getOfficerTaskByUnitIdAndCateory($unitId, $category, $progress = 0)
    {/*{{{*/
		$psql = $progress > 0 ? ' and progress = '.$progress : '';
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask = "" and t.category = '.$category.' '.$psql.' and YEAR(t.createtime) = 2018 order by id desc;';
        $unGroupResult = $this->task_model->query($sql);
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and t.groupTask != "" and t.category = '.$category.' '.$psql.' and YEAR(t.createtime) = 2018 group by groupTask order by id desc;';
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

    //政府工作报告月报
    public function getMonthWorkReport($unitId, $category, $date)
    {/*{{{*/
        $result = array();
        for ($progress = 1; $progress < 5; $progress++)
        {
            $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = ".$progress." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId)";
            $tasks = $this->task_model->query($sql);
            $result[$progress] = $tasks;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //市委市政府重大部署月报
    public function getMonthDeployerReport($unitId, $category, $date)
    {/*{{{*/
        $result = array();
        for ($sequence = 1; $sequence < 8; $sequence++)
        {
            //$sql = "select * from tbl_task where unitId in (select id from tbl_unit where parentId = ".$unitId.")  and sequence = ".$sequence." and category = ".$category." and date_format(createtime,'%Y-%m') <= date_format(".$date.",'%Y-%m');";

            $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and sequence = ".$sequence." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";

            $tasks = $this->task_model->query($sql);
            $result[$sequence] = $tasks;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //建议提案月报
    public function getMonthProposalReport($unitId, $category, $date)
    {/*{{{*/
        $result = array();
        for ($childType = 1; $childType < 3; $childType++)
        {
            //$sql = "select * from tbl_task where unitId in (select id from tbl_unit where parentId = ".$unitId.")  and childType = ".$childType." and category = ".$category." and date_format(createtime,'%Y-%m') <= date_format(".$date.",'%Y-%m');";

            $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and childType = ".$childType." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";
            $tasks = $this->task_model->query($sql);
            $result[$childType] = $tasks;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //其它类型完成未完成月报
    public function getMonthFinishReport($unitId, $category, $date)
    {/*{{{*/
        $result = array();
        $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
        $tasks = $this->task_model->query($sql);
        $result['finish'] = $tasks;

        $sql = "select * from tbl_task where valid = 1 and id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress > 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
        $tasks = $this->task_model->query($sql);
        $result['unFinish'] = $tasks;
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    //搜索
    public function getTasksByKeyWord($keyWord, $unitId = 0, $role = 0) {
        $keyWord = $this->input->post('keyWord');
        $result = $this->task_model->getTasksByKeyWord($keyWord, $unitId, $role);
        $this->set_content(0, '获取成功', $result);
    }

//     public function search()
//     {/*{{{*/
//         $keyword = $this->input->post('keyword');
//         $unitId = $this->input->post('unitId');
//         //0：默认，1：县长，2：副县长，3：督查，4：单位
//         $role = $this->input->post('role');
//         if (isset($keyword) && strlen($keyword) && isset($unitId) && isset($role))
//         {
// //            SELECT t.id, t.name, t.plan, t.category, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and t.unitId = u.id and (t.name LIKE '%asdf%' or t.plan like '%asdf%') order by t.id desc;
//             $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u ";
//             $sql .= "WHERE t.valid = 1 and t.unitId = u.id and "; 
//             $sql .= "(t.name LIKE '%".$keyword."%' or t.plan like '%".$keyword."%') ";
//             //部门只查看自己下得任务，副县长下得，督查和县长看所有
//             if ($role == 4)
//             {
//                 $sql .= " and t.unitId = ".$unitId." ";
//             }
//             else if ($role == 2)
//             {
//                 $sql .= " and t.unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.") ";
//             }
//             $exSql = " and t.groupTask = '' order by t.id desc;";
//             $unGroupResult = $this->task_model->query($sql.$exSql);

//             $exSql = " and t.groupTask != '' group by t.groupTask order by t.id desc;";
//             $groupResult = $this->task_model->query($sql.$exSql);
//             foreach($groupResult as $task)
//             {
//                 $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.groupTask = '".$task->groupTask."'";
//                 if ($role == 2)
//                 {
//                     $sql .= " and t.unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.") ";
//                 }
//                 $sql .= " order by t.id desc";
//                 $groupTasks = $this->task_model->query($sql);
//                 $task->groupTasks = $groupTasks;
//             }
//             $result = array_merge($groupResult, $unGroupResult);
//             $this->set_content(0, '获取成功', $result);
//         }
//         else
//         {
//             $this->set_content(-1, '请输入查询关键字', array());
//         }

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
//      }/*}}}*/

}
?>
