<?php
class Studentclass extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->json();
        $id = $this->studentclass_model->create_id($data);
        $result = array('id' => $id);

        $this->load->model('student_model');
        $rows = $this->student_model->update_where(array('teacherId' => $data['teacherId']), array('id' => $data['studentId']));

        $this->load->model('order_model');
        $rows = $this->order_model->update_where(array('student_id' => $data['studentId']), array('id' => $data['orderId']));

        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/
}
?>