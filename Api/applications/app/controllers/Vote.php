<?php
class Vote extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getVoteOpinions()
    {/*{{{*/
        $cules = $this->vote_model->getVoteOpinions();
        $this->set_content(0, '获取成功', $cules);
    }/*}}}*/

    // public function updateOpinion($opinionId = 0) {
    //     $id = $this->opinion_model->update_where($this->input->post(), array('id' => $opinionId));
    //     $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '更新成功' : '更新失败', array('rows' => $id));
    // }

    public function newVote() {
        $data = $this->input->post();
        $id = $this->vote_model->create_id($data);
        if ($id) {
            $this->set_content(0, '创建成功', array('id' => $id));
        } else {
            $this->set_content(-1, '提交失败', array('id' => 0));
        }
    }

    public function getVoteOpinion($opinionId = 0) {
        if ($opinionId) {
            $opinion = $this->vote_model->getVoteOpinion($opinionId);
            if (count($opinion)) {
                $this->set_content(0, '创建成功', $opinion);
            } else {
                $this->set_content(-1, '获取失败', array('id' => 0));
            }
        } else {
            $this->set_content(-1, '获取失败', array('id' => 0));
        }
    }

}
?>
