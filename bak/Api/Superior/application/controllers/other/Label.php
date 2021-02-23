<?php
class Label extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = array(
            'name' => $this->input->post('name', true),
            'description' => $this->input->post('description', true) 
        );
        $id = $this->label_model->create_id($data);
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function getLabels()
    {/*{{{*/
        $banners = $this->label_model->result();
        $this->set_content(0, '获取成功', $banners);
    }/*}}}*/
}
?>