<?php
class ReportTask extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

   
    public function newReportTask() {
        $unitTaskId = $this->input->post('unitTaskId');
        $unitIds = $this->input->post('unitIds');
        if ($unitTaskId && $unitIds) {
            $unitIds = explode(",", $unitIds);
            $data = array();
            foreach ($unitIds as $value) {
                $data[] = array('unitTaskId' => $unitTaskId, 'unitId' => $value);
            }
            $rows = $this->reporttask_model->create_batch($data);
            $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '上报成功' : '上报失败', array('rows' => $rows));
        } else {
            $this->set_content(-1, '参数错误', '');
        }
    }


}
?>
