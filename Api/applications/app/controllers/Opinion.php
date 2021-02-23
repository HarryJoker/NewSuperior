<?php
class Opinion extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function newOpinion() {
        $data = $this->input->post();
        $id = $this->opinion_model->create_id($data);
        if ($id) {
            $opinion = $this->opinion_model->get($id);
            $this->set_content(0, '创建成功', $opinion);
        } else {
            $this->set_content(-1, '提交失败', array());
        }
        
    }

    public function getOpinion($id = 0) {/*{{{*/
        if ($id) {
            $opinions = $this->opinion_model->get($id);
            $this->set_content(0, '获取成功', $opinions);
        } else {
            $this->set_content(-1, '获取失败', array('id' => 0));
        }
        
    }/*}}}*/

    public function getPeopleOpinions() {/*{{{*/
        $opinions = $this->opinion_model->get_where(array('status >' => 1));
        $this->set_content(0, '获取成功', $opinions);
    }/*}}}*/

    public function getOpinions($status = -1) {/*{{{*/
        $opinions = array();
        if ($status == -1) {
            $opinions = $this->opinion_model->result();
        } else {
            $opinions = $this->opinion_model->get_where(array('status' => $status));
        }
        $this->set_content(0, '获取成功', $opinions);
    }/*}}}*/

    public function updateOpinion($opinionId = 0) {
        $data = $this->input->post();
        unset($data['voteCount']);
        $id = $this->opinion_model->update_where($data, array('id' => $opinionId));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '更新成功' : '更新失败', array('rows' => $id));
    }

    public function deletOpinion($id = 0) {
        $rows = $this->opinion_model->delete(array('id'=>$id));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }
}
?>