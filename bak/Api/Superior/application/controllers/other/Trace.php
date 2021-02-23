<?php
class Trace extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
        $attachments = array();
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            //$this->do_thumb($data);
            foreach ($data as $image)
            { 
                $attachments[] = $image['file_name'];
            } 
        }
        $params = array(
            'taskId' => $this->input->post('taskId'),
            'userId' => $this->input->post('userId'),
            'category' => $this->input->post('category'),
            'unitId' => $this->input->post('unitId'),
            'unitName' => $this->input->post('unitName'),
            'content' => $this->input->post('content'),
            'address' => $this->input->post('address'),
            'location' => $this->input->post('location'),
            'attachment' => implode(",", $attachments),
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
        $sql = 'select t.* , u.logo from tbl_trace t, tbl_unit u where t.unitId = u.id and status = 0 and type = 0 and category = '.$category.';';
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
        $sql = 'select a.id, a.name, a.plan from tbl_task a, tbl_trace b where a.id = b.taskId and b.id = '.$id;
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
        }
    }/*}}}*/

}
?>


