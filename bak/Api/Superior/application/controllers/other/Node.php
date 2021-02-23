<?php
class Node extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/

      $data = $this->input->json();

      if ($data && count($data)) 
      {
          $rows = $this->node_model->create_batch($data);
          $this->set_content(0, '排课成功', $rows);
      }
      else
      {
        $this->set_content(-1, '排课失败', '');
      }
    }/*}}}*/

    public function getPlans($teacherId = 0)
    {
      $nodes = $this->node_model->get_where(array('teacherId' => $teacherId, 'day >=' => date("Y.m.d",time())));
      $plans = array();
      for($n=0; $n<count($nodes); $n++) {
          if (count($plans) == 0 || $plans[count($plans) - 1]['day'] != $nodes[$n]['day']) {
            $plan = array();
            $plan['day'] =  $nodes[$n]['day'];
            $plan['title'] = $nodes[$n]['title'];
            $plan['time'] = $nodes[$n]['time'];
            $plan['week'] = $nodes[$n]['week'];
            $plan['status'] = $nodes[$n]['status'];
            $plan['nodes'][] = $nodes[$n];
            $plans[] = $plan;
          } else {
            $plans[count($plans) - 1]['nodes'][] = $nodes[$n];
          }
      }
      $this->set_content(0, '获取成功', $plans);
    }

    // public function getBanners()
    // {/*{{{*/
    //     $banners = $this->banner_model->resultOrderBy('createdTime');
    //     require 'Qiniu.php';
    //     $qiniu = new Qiniu();
    //     $token = $qiniu->getToken('test');
    //     $result = array("list" => $banners, 'token' => (array('token' => $token)), "pagination" => array("total" => count($banners)));
    //     $this->set_content(0, '获取成功', $result);
    // }/*}}}*/

    public function update() 
    {
        $data = $this->input->json();
        $id = $this->node_model->update_where($data, array('id' => $data['id']));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', '');
    }

    //预约课程
    public function subscribe() 
    {
        $data = $this->input->json();
        $id = $this->node_model->update_where($data, array('id' => $data['id']));

        $params = array('studentId' => $data['studentId'], 'nodeId' => $data['id'], 'name' => $data['title'], 'teacherId' => $data['teacherId'], 'teacherName' => $data['teacherName'], 'type' => 1, 'closure' => 1, 'claszTime' => $data['day'].' ('.$data['time'].'）');
        var_dump($params);

        $this->load->model('claszrecord_model');
        $id = $this->claszrecord_model->create_id($params);
        $result = array('id' => $id);

        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }

    // public function remove($id = 0)
    // {
    //     $data = $this->input->json();
    //     $ids = $data['ids'];
    //     if (count($ids)) $this->banner_model->deleteBatch('id', $ids);        
    //     $this->getBanners();
    // }

}
?>