<?php
class Task extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
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
        $unAcceptResult = $this->task_model->get_where(array('unitId' => $unitId, 'category' => $category, 'accept' => '0'));
        $acceptedResult = $this->task_model->get_where(array('unitId' => $unitId, 'category' => $category, 'accept' => '1'));
        $data = array('unAccept' => $unAcceptResult, 'accepted' => $acceptedResult);

        $this->set_content(0, '获取成功', $data);
    }/*}}}*/
    
    //根据category获取所有的task
    public function getAllTasksByCategory($category)
    {/*{{{*/
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where u.id = t.unitId and t.category = '.$category.' order by t.id;';

//        $result = $this->task_model->get_where_with_order(array('category' => $category));
        $result = $this->task_model->query($sql);
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
                $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'accept' => $accept));
            }
            else
            {
                $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $category, 'accept' => $accept));
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
        $result = $this->task_model->get_where_with_order(array('unitId' => $unitId, 'category' => $categoryId, 'accept' => '1'));
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getWithLastTraceAndLeaders($taskId)
    {/*{{{*/
        $task = $this->task_model->get($taskId);

        //trace
        $sql = 'select b.* from tbl_task a, tbl_trace b where a.id = b.taskId and b.type = 0 and a.id = '.$taskId.' order by id desc limit 1;';
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
        $sql = 'select a.*, (select logo from tbl_unit where id = a.unitId) as logo from tbl_trace a where a.taskId ='.$taskId.' order by id desc';
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
        $sql = 'select a.*, u.name as unitName from tbl_task a, tbl_unit u, tbl_taskuserrelation b where a.id = b.taskId and u.id = a.unitId and a.category = '.$category.' and b.userId = '.$userId.' order by a.id desc;';
        $result = $this->task_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //县长首页，获取统计数据和副县长
    public function getTaskCountWithLeadersByUnitId($unitId, $category = 0)
    {/*{{{*/
        //1：完成，2：快速，3：正常，4：缓慢
        $sql = 'select progress, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY progress;';
        $stateCount = $this->task_model->query($sql);
        $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.');';
        $reportCount = $this->task_model->query($sql);
        $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

        $sql = 'select b.* from tbl_unit b where b.parentId= '.$unitId.' and b.role = 2;';
        $leaders = $this->task_model->query($sql);

        $sql = 'select sequence, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY sequence;';
        $sequence= $this->task_model->query($sql);
        
        $sql = 'select childType, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY childType;';
        $type = $this->task_model->query($sql);

        $sql = 'select count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.');';
        $sumCount = $this->task_model->query($sql);
        $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

        $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'leaders' => $leaders, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type'=>$type);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getTaskCountByUnitId($unitId, $category)
    {/*{{{*/
        //1：完成，2：快速，3：正常，4：缓慢
        $sql = 'select progress, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY progress;';
        $stateCount = $this->task_model->query($sql);
        $sql = 'select count(a.id) as reportCount from tbl_task a, tbl_taskuserrelation b where a.id = b.taskId and a.category = '.$category.' and b.userId = (select id from tbl_user where unitId = '.$unitId.');';
        $reportCount = $this->task_model->query($sql);
        $reportCount = count($reportCount) == 1 ? $reportCount[0] : array('count' => 0);

        $sql = 'select sequence, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY sequence;';
        $sequence= $this->task_model->query($sql);

        $sql = 'select count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.');';
        $sumCount = $this->task_model->query($sql);
        $sumCount = count($sumCount) > 0 ? $sumCount[0] : array('count' => 0);

        $sql = 'select childType, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') GROUP BY childType;';
        $type = $this->task_model->query($sql);

        $result = array('stateCount' => $stateCount, 'reportCount' => $reportCount, 'sequence' => $sequence, 'sumCount' => $sumCount, 'type' => $type);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getLeaderTaskByUnitIdAndCateory($unitId, $category)
    {/*{{{*/
        //$sql = 'select * from tbl_task where category = '.$category.' and unitId in (select id from tbl_unit where parentId = '.$unitId.') order by id desc;';
        $sql = 'select t.*, u.name as unitName from tbl_task t, tbl_unit u where t.category = '.$category.' and t.unitId = u.id and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.') order by id desc;';
        $result = $this->task_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getLeaderTaskByUnitIdAndCateoryScopeProgress($unitId, $category)
    {/*{{{*/
        $sql = 'select progress, count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation parentId = '.$unitId.') GROUP BY progress;';
        $stateCount = $this->task_model->query($sql);
        $result = array();
        for ($progress = 1; $progress < 5; $progress++)
        {
            //$sql = 'select * from tbl_task where category = '.$category.' and unitId in (select id from tbl_unit where progress = '.$progress.' and parentId = '.$unitId.') order by id desc;';
            $sql = 'select * from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where  parentId = '.$unitId.') and progress = '.$progress.' order by id desc;';
            $tasks = $this->task_model->query($sql);
            $result[$progress] = $tasks;
        }
        $result['stateCount'] = $stateCount;

        $sql = 'select count(*) as count from tbl_task where category = '.$category.' and unitId in (select unitId from tbl_unitrelation where parentId = '.$unitId.');';
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
            $sql = "select * from tbl_task where id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = ".$progress." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId)";
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

            $sql = "select * from tbl_task where id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and sequence = ".$sequence." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";

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

            $sql = "select * from tbl_task where id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 or type = 3 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and childType = ".$childType." and category = ".$category." and date_format(createtime,'%Y-%m')= date_format(".$date.",'%Y-%m') group by taskId);";
            $tasks = $this->task_model->query($sql);
            $result[$childType] = $tasks;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //其它类型完成未完成月报
    public function getMonthFinishReport($unitId, $category, $date)
    {/*{{{*/
        $result = array();
        $sql = "select * from tbl_task where id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress = 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
        $tasks = $this->task_model->query($sql);
        $result['finish'] = $tasks;

        $sql = "select * from tbl_task where id in (select taskId from (select * from tbl_trace order by id desc) t where type = 0 and unitId in (select unitId from tbl_unitrelation where parentId = ".$unitId.")  and progress > 1 and category = ".$category." and date_format(createtime,'%Y-%m') = date_format(".$date.",'%Y-%m') group by taskId);";
        $tasks = $this->task_model->query($sql);
        $result['unFinish'] = $tasks;
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

}
?>
