<?php
class Review extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function newReview() {   
        $data = $this->input->post();
        $id = $this->review_model->create_id($data);
        if ($id) {
            $review = $this->review_model->get($id);
            $this->set_content(0, '创建成功', $review);
        } else {
            $this->set_content(-1, '提交失败', array());
        }
    }

    public function getReviews($type = 0)
    {/*{{{*/
        if ($type == 0) {
            $reviews = $this->review_model->result();
            $this->set_content(0, '获取成功', $reviews);
        } else {
            $reviews = $this->review_model->get_where(array('type' => $type));
            $this->set_content(0, '获取成功', $reviews);
        }
    }/*}}}*/

    public function updateReview($id = 0) {
        $rows = $this->review_model->update_where($this->input->post(), array('id' => $id));
        $this->set_content($rows >= 0 ? 0 : -1, $rows >= 0 ? '更新成功' : '更新失败', array('rows' => $rows));
    }

    public function deletReview($id = 0) {
        $rows = $this->review_model->delete(array('id'=>$id));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }

}
?>
