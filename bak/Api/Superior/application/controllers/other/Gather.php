<?php
class Gather extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getUnitGatherById($unitId = 0)
    {/*{{{*/
        $sql = 'select a.category, ifnull(ROUND(avg((select Sum(cutScore) from tbl_trace where taskId = a.id)), 1), 0) as cutScore from tbl_task a where a.unitId = '.$unitId.' group by category;';
        $result = $this->gather_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getDetailByUnitIdAndCateogry($unitId = 0, $category)
    {/*{{{*/
        $this->load->model('task_model');
        $this->load->model('trace_model');
        $this->load->model('grade_model');
        $tasks = $this->task_model->get_where(array('unitId' => $unitId, 'accept' => 1, 'category' => $category));
        foreach ($tasks as $task)
        {
            $traces = $this->trace_model->get_where(array('taskId' => $task->id, 'type' => 0));
            foreach ($traces as $trace)
            {
                $gradeIds = explode(',', $trace->cutScoreDetail);
                $grades = $this->grade_model->get_in('id', $gradeIds);
                $trace->grades = $grades;
            }

            $task->traces = $traces;
        }
        $this->set_content(0, '获取成功', $tasks);
    }/*}}}*/

}
?>
