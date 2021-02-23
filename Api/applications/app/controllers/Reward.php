<?php
class Reward extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    // public function getRewards()
    // {/*{{{*/
    //     $cules = $this->reward_model->getUnitTaskRewards();
    //     $this->set_content(0, '获取成功', $cules);
    // }/*}}}*/


    // public function newReward() {
    //     $data = $this->input->post();
    //     $id = $this->reward_model->create_id($data);
    //     if ($id) {
    //         $this->set_content(0, '创建成功', array());
    //     } else {
    //         $this->set_content(-1, '提交失败', array());
    //     }
    // }


    // public function getUnitTaskReward($rewardId = 0){/*{{{*/
    //     $reward = $this->reward_model->get($rewardId);
    //     if (count($reward) == 0) {
    //         $this->set_content(-1, '获取失败', array('rewardId' => $rewardId));
    //         return;
    //     }
    //     $this->load->model('task_model');
    //     $task = $this->task_model->getUnitTask($reward['unitTaskId']);

    //     if (count($task['unitTasks']) == 1) {
    //         $task['unitTasks'][0]['reward'] = $reward['content'];
    //     }
    //     $this->set_content(0, '获取成功', $task);
    // }/*}}}*/




    public function newReward() {   
        $data = $this->input->post();
        $id = $this->reward_model->create_id($data);
        if ($id) {
            $review = $this->reward_model->get($id);
            $this->set_content(0, '创建成功', $review);
        } else {
            $this->set_content(-1, '提交失败', array());
        }
    }

    public function getRewards()
    {/*{{{*/
        $reviews = $this->reward_model->result();
        $this->set_content(0, '获取成功', $reviews);
    }/*}}}*/

    public function updateReward($id = 0) {
        $rows = $this->reward_model->update_where($this->input->post(), array('id' => $id));
        $this->set_content($rows >= 0 ? 0 : -1, $rows >= 0 ? '更新成功' : '更新失败', array('rows' => $rows));
    }

    public function deletReview($id = 0) {
        $rows = $this->reward_model->delete(array('id'=>$id));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }



}
?>
