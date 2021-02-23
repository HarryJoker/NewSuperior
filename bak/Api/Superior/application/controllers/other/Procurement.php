<?php
class Procurement extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->post();
        $rows = $this->procurement_model->create_batch_ignore($data);
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '提交成功' : '提交失败', $rows);
    }/*}}}*/

    public function getProcurements()
    {/*{{{*/
        $data = $this->input->json();
        $items = $this->procurement_model->result();
        $count = $this->procurement_model->result_count();
        $result = array("list" => $items, "pagination" => array("total" => $count));
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/
}
?>