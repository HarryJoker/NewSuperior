<?php
class UnitTask extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function newUnitTask()
    {/*{{{*/
        //post 获取参数
        $data = $this->input->post();
 
        $unitIds = array_keys($data['like']);
        $childTaskId = $data['childTaskId'];
        $this->load->model('unittask_model');
        $rows = $this->unittask_model->newUnitTasks($childTaskId, $unitIds);

        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '创建成功' : '创建失败', array('rows' => $rows));
    }/*}}}*/


    public function delete($taskId = 0, $unitId = 0)
    {
        $rows = $this->unittask_model->delete(array('taskId'=>$taskId, 'unitId' => $unitId));
        $this->set_content($rows >= 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }


    public function getUnitTask($unitTaskId = 0) {
        $unitTask = $this->unittask_model->getUnitTask($unitTaskId);
        $this->set_content($unitTask ? 0 : -1, $unitTask ? '获取成功' : '获取失败', $unitTask);
    }

    //上报领导部门
    public function reportLeaderUnit()
    {/*{{{*/
        $unitTaskId = $this->input->post('unitTaskId');
        $leaderUnitIds = $this->input->post('leaderUnitIds');
        $leaderUnitIds = explode(',',$leaderUnitIds); 
        $this->load->model('reporttask_model', 'reporttask', true);
        
        $data = array();
        foreach($leaderUnitIds as $unitId) {
            $data[] = array('unitTaskId' => $unitTaskId, 'unitId' => $unitId);
        }

        $rows = $this->reporttask->create_batch($data);
        if ($rows > 0)
        {
            $this->set_content(0, '上报成功', array('rows' => $rows));
        }
        else
        {
            $this->set_content(-1, '上报失败', array('id' => 0));
        }
    }/*}}}*/

}
?>
