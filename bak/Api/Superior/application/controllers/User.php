<?php
class User extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function getUsers()
    {/*{{{*/
        $data = $this->input->json();

        $users = $this->user_model->resultOrderBy('createTime');

        $result = array("list" => $users, "pagination" => array("total" => count($users)));

        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function update()
    {
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $this->user_model->update_where($data,  array('id' => $data['id']));
            $data = $this->user_model->get_where(array('id' => $data['id']));

            $this->load->model('store_model');
            $this->store_model->increCountByRole($data['role'], true);


            if ($data['role'] && $data['role'] == 1) {
                $this->load->model('student_model');
                $student = $this->student_model->get_where(array('openId' => $data['openId']));
                if ($student) $data['student'] = $student;
            }
            if ($data['role'] && $data['role'] == 2) {
                $this->load->model('teacher_model');
                $teacher = $this->teacher_model->get_where(array('openId' => $data['openId']));
                if ($teacher) $data['teacher'] = $teacher;
            }
            $this->load->model('store_model');
            $store = $this->store_model->get(1);
            if ($store) $data['store'] = $store;

            $this->set_content(0, '更新成功', $data);
        }
        else
        {
            $this->set_content( -1 , '更新失败', '');
        }
    }

    public function remove()
    {
        $data = $this->input->json();
        var_dump($data['role']);
        if (array_key_exists('ids', $data) && count($data['ids']) == 1 && $data['role'])
        {
            $this->user_model->update_where(array('role' => 0),  array('id' => $data['ids'][0]));  
            $this->load->model('store_model');
    
            $this->store_model->increCountByRole($data['role'], false);
            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(-1, '删除失败', '');
        }
       
    }

}
?>