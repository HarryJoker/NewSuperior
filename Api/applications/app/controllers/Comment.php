<?php
class Comment extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getComments()
    {/*{{{*/
        $comments = $this->comment_model->result();
        $this->set_content(0, '获取成功', $comments);
    }/*}}}*/


    public function getCommentsByType($unitTaskId = 0, $type = 0)
    {/*{{{*/
        $comments = $this->comment_model->getCommentsByType($unitTaskId, $type);
        $this->set_content(0, '获取成功', $comments);
    }/*}}}*/


    public function getCommentTasks() {
       $tasks = $this->comment_model->getCommentOpinions();
       $this->set_content(0, '获取成功', $tasks);
    }

    public function updateComment($id = 0) {
        $rows = $this->comment_model->update_where($this->input->post(), array('id' => $id));
        $this->set_content($rows >= 0 ? 0 : -1, $rows >= 0 ? '更新成功' : '更新失败', array('rows' => $rows));
    }

    public function newComment() {
        $data = $this->input->post();
        $id = $this->comment_model->create_id($data);
        $comment = $this->comment_model->get($id);
        if ($id) {
            $this->set_content(0, '创建成功', $comment);
        } else {
            $this->set_content(-1, '提交失败', array());
        }
    }

}
?>
