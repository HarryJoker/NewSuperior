<?php
class Advice extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
        $data = array(
            'content' => $this->input->post('content', true),
            'userId' => $this->input->post('userId', true) 
        );
        $id = $this->advice_model->create_id($data);
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

}
?>
