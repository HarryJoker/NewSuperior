<?php
class Trace extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
        $this->load->model('unittask_model');
    }/*}}}*/


    private function makeDoUpload() {
        $attachments = $this->input->post('attachments');
        $attachments = isset($attachments) && strlen($attachments) ? $attachments : '';
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            //$this->do_thumb($data);
            foreach ($data as $image) { 
                $attachments .= strlen($attachments) > 0 ? "," : "";
                $attachments .= $image['file_name'];
            } 
        }
        return $attachments;
    }


    public function create()
    {/*{{{*/
        $attachments = $this->makeDoUpload();
        $params = array(
            'taskId' => $this->input->post('taskId'),
            'userId' => $this->input->post('userId'),
            'category' => $this->input->post('category'),
            'unitId' => $this->input->post('unitId'),
            'unitName' => $this->input->post('unitName'),
            'content' => str_replace("/a25a/", "%", $this->input->post('content')),
            'address' => $this->input->post('address'),
            'location' => $this->input->post('location'),
            'attachment' => $attachments,
            'type' => $this->input->post('type'),
        );
        $id = $this->trace_model->create_id($params);
        $result = array('id' => $id);
        if ($id > 0)
        {
            $this->set_content(0, '提报成功', $result);
        }
        else
        {
            $this->set_content(-1, '提报失败', $result);
        }
    }/*}}}*/


    //部门内部报送（新创建或修改完善 --> 待审核内容）
    public function newUnitStaySignReportContentTrace($traceId = 0) {
        $attachments = $this->makeDoUpload();
        $data = $this->input->post();
        $data['attachments'] = $attachments;
        $data['status'] = -1;
        if ($traceId) {
            //完善修改继续报送
            $rows = $this->trace_model->update_where($data, array('id' => $traceId));
            if ($rows >= 0)
            {
                $this->set_content(0, '提报成功', array("id" => $rows));
            }
            else
            {
                $this->set_content(-1, '提报失败', array("id" => 0));
            }
        } else {
            //新增报送内容
            $id = $this->trace_model->create_id($data);
            if ($id > 0)
            {
                $this->set_content(0, '提报成功', array("id" => $id));
            }
            else
            {
                $this->set_content(-1, '提报失败', array("id" => 0));
            }
        }
        
    }

    //部门内部审核退回（待完善内容）
    public function newUnitStayCompleteContentTrace($traceId = 0) {
        $data = $this->input->post();
        $data['status'] = -2;
        $rows = $this->trace_model->update_where($data, array('id' => $traceId));
        if ($rows >= 0)
        {
            $this->set_content(0, '提报成功', array("rows" => $rows));
        }
        else
        {
            $this->set_content(-1, '提报失败', array("id" => 0));
        }
    }

    //部门报送内容
    public function newUnitContentTrace($staySignTraceId = 0) {
        $attachments = $this->makeDoUpload();
        $data = $this->input->post();
        if (empty($data['taskDate'])) {
            $this->set_content(-1, '报送月份不能为空', array("id" => 0));
            return;
        }
        $data['attachments'] = $attachments;
        $data['status'] = 31;
        //创建平台部门报送工作
        $id = $this->trace_model->create_id($data);
        if ($id > 0){
            $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 31, 0, $data['progress'], 0);
            $this->unittask_model->udpateUnitTaskMonthById($data['unitTaskId'], $data['taskDate']);
            //删除部门内部审核文稿
            $this->trace_model->delete(array('id' => $staySignTraceId));
            $this->set_content(0, '提报成功', array("id" => $id));
        }
        else
        {
            $this->set_content(-1, '提报失败', array("id" => 0));
        }

    }


    //领取任务
    public function newAcceptTaskTrace() {/*{{{*/
        $data = $this->input->post();
        $unitTaskId = $data['unitTaskId'];

        $responsibiltyData = array(
            'responsibilityUserId' => $data['responsibilityUserId'], 
            'responsibilityUserName' => $data['responsibilityUserName'],
            'partReponsibilityUserId' => $data['partReponsibilityUserId'], 
            'partReponsibilityUserName' => $data['partReponsibilityUserName'],
            'handleUserId' => $data['handleUserId'],
            'handleUserName' => $data['handleUserName'],
            'status' => 1);

        unset($data['responsibilityUserId']);
        unset($data['partReponsibilityUserId']);
        unset($data['handleUserId']);
        unset($data['handleUserName']);
        unset($data['responsibilityUserName']);
        unset($data['partReponsibilityUserName']);

        $this->load->model('unittask_model');
        $this->unittask_model->update_where($responsibiltyData, array('id' => $unitTaskId));

        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->set_content(0, '领取成功', $trace);
            } else {
                $this->set_content(-1, '领取失败', '');
            }
        } else {
            $this->set_content(-1, '请检查领取数据', '');
        }
    }/*}}}*/


    //上报领导
    public function newReportTrace() {/*{{{*/
        $data = $this->input->post();
        $unitTaskId = $data['unitTaskId'];

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];

        $leaderUnitIds = explode(',', $data['leaderUnitIds']);
        unset($data['leaderUnitIds']);
        if (!$leaderUnitIds) {
            $this->set_content(-1, '请上传上报领导的id', array('id' => 0));
            return;
        }


        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->load->model('reporttask_model');
                $this->reporttask_model->reportUnitTask2Leaders($leaderUnitIds,$unitTaskId);
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 20, 0, 0, 0);
                $this->set_content(0, '领取成功', $trace);
            } else {
                $this->set_content(-1, '领取失败', '');
            }
        } else {
            $this->set_content(-1, '请检查领取数据', '');
        }
    }/*}}}*/


    //任务审核Trace
    public function newVerifyTrace() {/*{{{*/
        $data = $this->input->post();

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];

        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], $data['status'], $data['status'], 0, $data['progress']);
                $this->unittask_model->udpateMonthUnitTaskStatusById($data['unitTaskId'], $data['status']);
                $this->set_content(0, '报送成功', array('id' => $trace));
            } else {
                $this->set_content(-1, '报送失败', '');
            }
        } else {
            $this->set_content(-1, '报送失败,请检查数据', '');
        }
    }/*}}}*/


    //领导批示
    public function newLeaderTrace() {/*{{{*/
        $data = $this->input->post();

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];

        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 21, 0, 0, 0);
                $this->set_content(0, '报送成功', array('id' => $trace));
            } else {
                $this->set_content(-1, '报送失败', '');
            }
        } else {
            $this->set_content(-1, '报送失败,请检查数据', '');
        }
    }/*}}}*/



    //任务催报Trace
    public function newRushTrace() {/*{{{*/
        $data = $this->input->post();

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];


        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 52, 0, 0, 0);
                $this->set_content(0, '报送成功', array('id' => $trace));
            } else {
                $this->set_content(-1, '报送失败', '');
            }
        } else {
            $this->set_content(-1, '报送失败,请检查数据', '');
        }
    }/*}}}*/

    //任务完成Trace
    public function newDoneTrace() {/*{{{*/
        $data = $this->input->post();

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];

        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 91, 91, 0, 100);
                $this->set_content(0, '报送成功', array('id' => $trace));
            } else {
                $this->set_content(-1, '报送失败', '');
            }
        } else {
            $this->set_content(-1, '报送失败,请检查数据', '');
        }
    }/*}}}*/

    //任务完成Trace
    public function newBackTrace() {/*{{{*/
        $data = $this->input->post();

        $unitTask = $this->unittask_model->get($data['unitTaskId']);
        if (empty($unitTask) ||empty($unitTask['taskReportTime'])) {
            $this->set_content(-1, '未查询到该任务调度月份', array("id" => 0));
            return;
        }
        $data['taskDate'] = $unitTask['taskReportTime'];

        if ($data && count($data) >= 5) {
            $id = $this->trace_model->create_id($data);
            $trace = $this->trace_model->get($id);
            if ($id) {
                $this->unittask_model->udpateUnitTaskById($data['unitTaskId'], 74, 0, 0, 0);
                $this->set_content(0, '报送成功', array('id' => $trace));
            } else {
                $this->set_content(-1, '报送失败', '');
            }
        } else {
            $this->set_content(-1, '报送失败,请检查数据', '');
        }
    }/*}}}*/



    public function newTrace() {/*{{{*/
        $attachments = array();
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            foreach ($data as $image)
            { 
                $attachments[] = $image['file_name'];
            } 
        }
       
       $data = $this->input->post();


       // $oldAttachments = isset($data['attachments']) && strlen($data['attachments']) ? explode(",", $data['attachments']) : array();

       //  //新老合并
       //  $newAttachments = array_merge($oldAttachments, $attachments);
       //  //序列化
       //  $data['attachments'] = implode(",", $newAttachments);

       if ($data && count($data) >= 5) {
        $id = $this->trace_model->create_id($data);
        $trace = $this->trace_model->get($id);
        if ($id) {
            if ($data['progress'] && $data['progress'] > 0) {
                $key = $data['status'] == 31 ? 'reportProgress' : 'verifyProgress';
                $this->load->model('unittask_model');
                $rows = $this->unittask_model->update_where(array($key => $data['progress']), array('id' => $data['unitTaskId']));
                $this->set_content(0, '报送成功', $trace);
            } else {
                $this->set_content(0, '报送成功', $trace);
            }
            // $this->set_content(0, '报送成功', array('id' => $id));
        } else {
            $this->set_content(-1, '报送失败', '');
        }
       } else {
        $this->set_content(-1, '报送失败,请检查数据', '');
       }

        // var_dump($this->input->post());
    }/*}}}*/


    //部门内部待完善或待手签的Trace
    public function getUnitStayTrace($unitTaskId = 0) {
        $traces = $this->trace_model->get_where(array('unitTaskId' => $unitTaskId,  "status < " => 0));
        if (count($traces) != 1) {
            $this->set_content(0, '获取成功', array('id' => 0));
        } else {
            $this->set_content(0, '获取成功', $traces[0]);
        }
    }

    //部门内部待完善Trace
    public function getUnitStayCompleteTrace($unitTaskId = 0) {
        $traces = $this->trace_model->get_where(array('unitTaskId' => $unitTaskId, "status" => -2));
        if (count($traces) != 1) {
            $this->set_content(0, '获取成功', array('id' => 0));
        } else {
            $this->set_content(0, '获取成功', $traces[0]);
        }
    }

    //部门内部待手签Trace
    public function getUnitStaySignReportTrace($unitTaskId = 0) {
        $traces = $this->trace_model->get_where(array('unitTaskId' => $unitTaskId, "status" => -1));
        if (count($traces) != 1) {
            $this->set_content(-1, '获取待审核报送内容错误', array('id' => 0));
        } else {
            $this->set_content(0, '获取成功', $traces[0]);
        }
    }


    public function getMonthUnitTaskTrace($category = 0) {
        $sql = "select r.id, r.unitTaskId, group_concat(r.content order by r.id separator '。') as content from tbl_trace r left join tbl_unittask ut on  r.unittaskId = ut.id left join tbl_task t on ut.taskId = t.id where t.category = ".$category." and r.status = 31 and DATE_FORMAT(r.taskDate, '%Y%m') = DATE_FORMAT(now(), '%Y%m')  group by unitTaskId";
        $traces = $this->trace_model->query($sql);
        $newTraces = array();
        foreach ($traces as $value) {
            $newTraces[$value['unitTaskId']] = $value;
        }
        $this->set_content(0, '获取成功', $newTraces);
    }

    //获取trace列表
    public function getTraceListByUnitTask($unitTaskId = 0, $hasUnitSaty = 0) {
        $traces = $this->trace_model->getTracesByUnitTask($unitTaskId, $hasUnitSaty);
        $this->set_content(0, '获取成功', $traces);
    }

    //获取trace列表
    public function getTraceListByTaskAndUnit($taskId = 0, $unitId = 0) {
        $this->load->model('unittask_model');
        $unitTasks = $this->unittask_model->get_where(array('taskId' => $taskId, 'unitId' => $unitId));
        if (count($unitTasks) == 1) {
            $traces = $this->trace_model->get_where_with_order(array('unitTaskId' => $unitTasks[0]['id'], 'status >=' => 0), 'id');
            for ($i=0; $i < count($traces); $i++) {
                $images = array();
                $imageUrls = array(); 
                if(strlen($traces[$i]['attachments'])) {
                    $imageNames = explode(',', $traces[$i]['attachments']);
                    foreach ($imageNames as $value) {
//                        $images[] = array('name' => $value, 'url' => 'http://ducha.boxing.gov.cn/uploads/'.$value);
//                        $imageUrls[] = 'http://ducha.boxing.gov.cn/uploads/'.$value;
                        $images[] = array('name' => $value, 'url' => $this ->config->item('attachment_url').''.$value);
                        $imageUrls[] = $this ->config->item('attachment_url').''.$value;
                    }
                }
                $traces[$i]['images'] = $images;
                $traces[$i]['imageUrls'] = $imageUrls;
            }
            $this->set_content(0, '获取成功', $traces);
        } else {
            $this->set_content(-1, '获取失败', array());
        }
        
    }

    public function approve()
    {/*{{{*/
        $params = array(
            'taskId' => $this->input->post('taskId'),
            'userId' => $this->input->post('userId'),
            'content' => $this->input->post('content'),
//            'attachment' => $this->input->post('attachment'),
//            'type' => $this->input->post('type'),
//            'score' => $this->input->post('score'),
        );
        $id = $this->trace_model->create_id($params);
        $result = array('id' => $id);
        if ($id > 0)
        {
            $this->set_content(0, '审核成功', $result);
        }
        else
        {
            $this->set_content(-1, '审核失败', $result);
        }

    }/*}}}*/

    //获取所有的trace by type 
    public function getAllUnVirifyTrace($category)
    {/*{{{*/
        //$sql = 'select t.* , u.logo from tbl_trace t, tbl_unit u where t.unitId = u.id and status = 0 and type = 0 and category = '.$category.' order by t.createtime ;';
		$sql = 'select t.* ,k.taskLabel, u.logo from tbl_trace t, tbl_task k, tbl_unit u where k.id = t.taskId and t.unitId = u.id and status = 0 and type = 0 and t.category = '.$category.' order by t.createtime;';
        //$result = $this->trace_model->get_where_with_order(array('status' => '0', 'type' => '0', 'category' => $category));
        $result = $this->trace_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //通过id获取用户
    public function get($id = 0)
    {/*{{{*/
        $result = $this->trace_model->get($id);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getWithTask($id = 0)
    {/*{{{*/
        $sql = 'select a.id, a.name, a.plan, a.category,a.planDetail from tbl_task a, tbl_trace b where a.id = b.taskId and b.id = '.$id;
        $task = $this->trace_model->query($sql);
        $task = count($task) == 1 ? $task[0] : array('id' => 0);
        $trace = $this->trace_model->get($id);
        $this->load->model('grade_model');
        $grade = $this->grade_model->select_by_category();
        $data = array('task' => $task, 'trace' => $trace, 'grade' => $grade);
        $this->set_content(0, '获取成功', $data);
    }/*}}}*/

    public function getWithTaskAndGrade($taskId, $traceId)
    {/*{{{*/
        $this->load->model('task_model');
        $task = $this->task_model->get($taskId);
        $trace = $this->trace_model->get($traceId);
        $trace->grades = array();
        if ($trace->type == 0)
        {
            $this->load->model('grade_model');
            $gradeIds = explode(',', $trace->cutScoreDetail);
            $grades = $this->grade_model->get_in('id', $gradeIds);
            $trace->grades = $grades;
        }

        $result = array('task' => $task, 'trace' => $trace);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getWithTaskAndLeader($id = 0)
    {/*{{{*/
        $sql = 'select a.id, a.name, a.plan from tbl_task a, tbl_trace b where a.id = b.taskId and b.id = '.$id;
        $task = $this->trace_model->query($sql);
        $task = count($task) == 1 ? $task[0] : array('id' => 0);
        $trace = $this->trace_model->get($id);
        $this->load->model('grade_model');
        $grade = $this->grade_model->select_by_category();
        $data = array('task' => $task, 'trace' => $trace, 'grade' => $grade);
        $this->set_content(0, '获取成功', $data);
    }/*}}}*/

    //审核提报
    public function verify($traceId, $taskId)
    {/*{{{*/
        $content = $this->input->post('content');
        $progress = $this->input->post('progress');
        $status = $this->input->post('status');
        $cutScore = $this->input->post('cutScore');
        $cutScoreDetail = $this->input->post('cutScoreDetail');
        $data = array('verifyContent' => $content,'cutScore' => $cutScore, 'cutScoreDetail' => $cutScoreDetail, 'status' => $status, 'progress' => $progress);
        //开启事务
        $this->db->trans_start();
        $rows = $this->trace_model->update_where($data, array('id' => $traceId));
        $this->load->model('task_model', '', true);
        $rows = $this->task_model->update_where(array('progress' => $progress), array('id' => $taskId));
        $this->db->trans_complete();
        if ($this->db->trans_status() === FALSE)
        {
            $this->set_content(-1, '审核失败', array('id' => 0));
        }
        else
        {
            $this->set_content(0, '审核成功', array('tracdeId' => $traceId));
			if ($status == 2) //退回推送通知
			{
				$trace = $this->trace_model->get($traceId);
				require_once 'Jpush.php';
				$jpush = Jpush::getInstance();
				$jpush->pushTaskBack($content, array('id' => $taskId, 'type' => 3), ''.$trace->unitId);
			}
        }
    }/*}}}*/

    //审核提报
    public function test($traceId, $taskId)
    {/*{{{*/
				$trace = $this->trace_model->get($traceId);
				var_dump($trace);
		require_once 'Jpush.php';
		$jpush = Jpush::getInstance();
		$jpush->pushTaskBack('测试。。爱上了都快放假爱上', array('id' => $taskId, 'type' => 3), '102');
    }/*}}}*/

}
?>


