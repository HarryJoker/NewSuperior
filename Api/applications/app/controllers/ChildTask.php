<?php
class ChildTask extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/



    //发布子任务
    public function newChildTask() {
        $data = $this->input->post();
        $unitIds = array_keys($data['like']);

        unset($data['like']);
                
        if (count($unitIds)) {
            $id = $this->childtask_model->create_id($data);

            if ($id > 0) {
                $this->load->model('unittask_model');
                $rows = $this->unittask_model->newUnitTasks($id, $unitIds);
                if ($rows > 0) {
                    $this->set_content(0, '发布成功', array('rows' => $rows));
                } else {
                    $this->set_content(-1, '未能发送给部门', array('id' => 0));
                }
            } else {
                $this->set_content(-1, '发布失败', array('id' => 0));
            }
        } else {
            $this->set_content(-1, '未选择部门', array('id' => 0));
        }
    }


    //更新子任务
    public function update() {
        $childTaskId = $this->input->post('childTaskId');
        $data = array('content' => $this->input->post('content'), 
                        'leaderUnitId' => $this->input->post('leaderUnitId'),
                        'taskIntro' => $this->input->post('taskIntro'));
        $rows = $this->childtask_model->update_where($data, array('id' => $childTaskId));
        $this->set_content(0, '更新成功', array('rows' => $rows));
    }

    //删除子任务
    public function delete($childTaskId = 0) {
        $rows = $this->childtask_model->delete(array('id' => $childTaskId));
        if ($rows) {
             $this->set_content(0, '删除成功', array('rows' => $rows));
        } else {
            $this->set_content(-1, '删除失败', array('rows' => $rows));
        }
    }


    //部门获取自己的任务
    public function getChildTasks($unitId) {

    }


    public function getChildTaskListByUnit($unitId = 0) {

    }

    public function getChildTaskListByTask($taskId = 0) {
        $childTasks = $this->childtask_model->getChildTaskListByTask($taskId);
        $this->set_content(0, '获取成功', $childTasks);
    }

 


	public function updateCreateTime()
	{/*{{{*/
		$result = $this->task_model->get_where(array('createtime' => ''));
		foreach($result as $row)
		{
			$data = array('createtime' => $row->updatetime);
			$row->update = $this->task_model->update_where($data, array('id'=> $row->id));
		}
        $this->set_content(0, '获取成功', $result);
	}/*}}}*/

    public function get($taskId)
    {/*{{{*/
        $result = $this->task_model->get($taskId);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //获取某单位某category的未申领和已申领的任务
    public function getAllTasksByUnitAndCategory($unitId, $category)
    {/*{{{*/
        //未领取
        $unAcceptResult = $this->task_model->get_where(array('unitId' => $unitId, 'category' => $category, 'accept' => '0', 'valid' => 1, 'YEAR(createtime)' => '2018'));
        $acceptedResult = $this->task_model->get_where(array('unitId' => $unitId, 'category' => $category, 'accept' => '1', 'valid' => 1, 'YEAR(createtime)' => '2018'));
        $data = array('unAccept' => $unAcceptResult, 'accepted' => $acceptedResult);

        $this->set_content(0, '获取成功', $data);
    }/*}}}*/
    
    //根据category获取所有的task
    public function getAllTasksByCategory($category)
    {/*{{{*/
        $sql = "select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and u.id = t.unitId and t.groupTask = '' and t.category = ".$category." and YEAR(t.createtime) = 2018 order by t.id desc;";
        $unGroupResult = $this->task_model->query($sql);
        $sql = "select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.valid = 1 and u.id = t.unitId and t.groupTask != '' and t.category = ".$category." and YEAR(t.createtime) = 2018 group by groupTask order by t.id desc;";
        $groupResult = $this->task_model->query($sql);
        foreach($groupResult as $task)
        {
            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and t.groupTask = '".$task->groupTask."' and YEAR(t.createtime) = 2018 order by t.id desc;";
            $groupTasks = $this->task_model->query($sql);
            $task->groupTasks = $groupTasks;
        }
        $result = array_merge($groupResult, $unGroupResult);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //根据category获取已审核的task
    public function getAllVerifyTasksByCategory($category)
    {/*{{{*/
        //$sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where u.id = t.unitId and t.category = '.$category.' order by t.id;';
        //$sql = 'select t.*, u.name as unitName, (select createtime from tbl_trace where taskId=t.id order by createtime desc limit 1) as lastTime from tbl_task t, tbl_unit u where u.id = t.unitId and t.category = '.$category.' and t.accept = 1 order by lastTime desc;';
        $sql = 'select tt.*,uu.name as unitName from tbl_task tt, tbl_unit uu where tt.id in (select taskId from (select * from (select * from tbl_trace order by createtime desc) as t group by t.taskId) as a where a.status > 0 and a.status < 4) and tt.groupTask = "" and tt.valid = 1 and uu.id = tt.unitId and tt.category = '.$category.' and YEAR(tt.createtime) > 2017;';
        $unGroupResult = $this->task_model->query($sql);
        $sql = 'select tt.*,uu.name as unitName from tbl_task tt, tbl_unit uu where tt.id in (select taskId from (select * from (select * from tbl_trace order by createtime desc) as t group by t.taskId) as a where a.status > 0 and a.status < 4) and tt.groupTask != "" and tt.valid = 1 and uu.id = tt.unitId and tt.category = '.$category.' and YEAR(tt.createtime) > 2017 order by groupTask;';
        $groupResult = $this->task_model->query($sql);
        foreach($groupResult as $task)
        {
            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and t.groupTask = '".$task->groupTask."' and YEAR(t.createtime) > 2017 order by t.id desc;";
            $groupTasks = $this->task_model->query($sql);
            $task->groupTasks = $groupTasks;
        }
        $result = array_merge($groupResult, $unGroupResult);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/
    
    //获取部门任务分类任务( $accept是否已申领)
    public function getTaskByUnitAndCategoryWithAccept($unitId, $category = 0, $accept = 0)
    {/*{{{*/
        if ($accept == 0 || $accept == 1)
        {
            if ($category == 0)
            {
                //全部
                $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'accept' => $accept, 'valid' => 1, 'YEAR(createtime)' => '2018'));
            }
            else
            {
                $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $category, 'accept' => $accept, 'valid' => 1, 'YEAR(createtime)' => '2018'));
            }
            $this->set_content(0, '获取成功', $result);
        }
        else
        {
            $this->set_content(-1, '参数错误', '');
        }
    }/*}}}*/

    public function getAcceptedTaskByUnitAndCategory($unitId, $categoryId)
    {/*{{{*/
        //未领取
        $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $categoryId, 'accept' => '1', 'valid' => '1'));
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getWithLastTraceAndLeaders($taskId)
    {/*{{{*/
        $task = $this->task_model->get($taskId);

        //trace
        $sql = 'select b.* from tbl_task a, tbl_trace b where b.valid = 1 and a.id = b.taskId and b.type = 0 and a.id = '.$taskId.' order by id desc limit 1;';
        $this->load->model('trace_model');
        $trace = $this->trace_model->query($sql);
        $trace = count($trace) == 1 ? $trace[0] : array('id' => 0, 'content' => '', 'attachment' => '');

        //user
        $sql = 'select a.* , b.logo from tbl_user a, tbl_unit b where a.unitId = b.id and b.role < 3 order by b.id;';
        $this->load->model('user_model');
        $users = $this->user_model->query($sql);

        $data = array('task' => $task, 'trace' => $trace, 'users' => $users);
        $this->set_content(0, '获取成功', $data);
    }/*}}}*/

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

    public function getTaskTraceByTaskId($taskId)
    {/*{{{*/
        $task = $this->task_model->get($taskId);
        $this->load->model('trace_model', '', true);
        $traces = $this->trace_model->get_where_with_order(array('taskId' => $taskId));
        $sql = 'select a.*, (select logo from tbl_unit where id = a.unitId) as logo from tbl_trace a where a.valid = 1 and a.taskId ='.$taskId.' order by id desc';
        $traces = $this->trace_model->query($sql);
        if (count($task) > 0)
        {
            array_unshift($traces, $task);
            $this->set_content(0, '获取成功', $traces);
        }
        else
        {
            $this->set_content(-1, '没有该任务', array('id' => 0));
        }
    }/*}}}*/

    //上报领导
    public function upgrade()
    {/*{{{*/
        $taskId = $this->input->post('taskId');
        $userIds = $this->input->post('userIds');
        $userIds = explode(',',$userIds); 
        $this->load->model('taskuserrelation_model', 'userTask', true);
        
        $data = array();
        foreach($userIds as $userId)
        {
            $taskUser = array('taskId' => $taskId, 'userId' => $userId);
            $data[] = $taskUser;
        }
        $rows = $this->userTask->create_batch($data);
        if ($rows > 0)
        {
            $this->set_content(0, '上报成功', array('taskId' => $taskId));
        }
        else
        {
            $this->set_content(-1, '上报失败', array('id' => 0));
        }
    }/*}}}*/

    //获取上报给领导的任务
    public function getTaskByUserAndCategory($userId = 0, $category = 0)
    {/*{{{*/
        //$sql = 'select a.* from tbl_task a, tbl_taskuserrelation b where a.id = b.taskId and a.category = '.$category.' and b.userId = '.$userId.' order by a.id desc;';
        $sql = 'select a.*, u.name as unitName from tbl_task a, tbl_unit u, tbl_taskuserrelation b where a.groupTask = "" and a.valid = 1 and a.id = b.taskId and u.id = a.unitId and a.category = '.$category.' and b.userId = '.$userId.' and YEAR(a.createtime) = 2018 order by a.id desc;';
        $unGroupResult = $this->task_model->query($sql);
        $sql = 'select a.*, u.name as unitName from tbl_task a, tbl_unit u, tbl_taskuserrelation b where a.groupTask != "" and a.valid = 1 and a.id = b.taskId and u.id = a.unitId and a.category = '.$category.' and b.userId = '.$userId.' and YEAR(a.createtime) = 2018 group by a.groupTask order by a.id desc;';
        $groupResult = $this->task_model->query($sql);
        foreach($groupResult as $task)
        {
            $sql = "SELECT t.*, u.name as unitName FROM tbl_task t, tbl_unit u WHERE t.valid = 1 and u.id = t.unitId and t.category = ".$category." and t.groupTask = '".$task->groupTask."' and YEAR(t.createtime) = 2018 order by t.id desc;";
            $groupTasks = $this->task_model->query($sql);
            $task->groupTasks = $groupTasks;
        }
        $result = array_merge($groupResult, $unGroupResult);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

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

    public function getLeaderTaskByUnitIdAndCateoryScopeProgress($unitId, $category)
    {/*{{{*/
        $sql = 'select progress, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation parentId = '.$unitId.') and YEAR(createtime) = 2018 GROUP BY progress;';
        $stateCount = $this->task_model->query($sql);
        $result = array();
        for ($progress = 1; $progress < 5; $progress++)
        {
            //$sql = 'select * from tbl_task where category = '.$category.' and unitId in (select id from tbl_unit where progress = '.$progress.' and parentId = '.$unitId.') order by id desc;';
            $sql = 'select * from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where  parentId = '.$unitId.') and progress = '.$progress.' and YEAR(createtime) = 2018 order by id desc;';
            $tasks = $this->task_model->query($sql);
            $result[$progress] = $tasks;
        }
        $result['stateCount'] = $stateCount;

        $sql = 'select count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') and YEAR(createtime) = 2018;';
        $sumCount = $this->task_model->query($sql);
        $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);
        $result['sumCount'] = $sumCount;

        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

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
