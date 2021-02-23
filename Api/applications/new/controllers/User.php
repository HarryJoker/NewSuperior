<?php
class User extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getAllUnits()
    {/*{{{*/
        $result = $this->unit_model->result();
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //用手机号创建账号
    public function create()
    {/*{{{*/
        $data = array('phone' => $this->input->post('phone'));
        $users = $this->user_model->get_where($data);
        if (count($users) > 0)
        {
            $this->set_content(-1,'该手机号已被注册', array('err' => 0));
        }
        else
        {
            $id = $this->user_model->create_id($data);
            $result = array('id' => $id);
            $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', $result);
        }
    }/*}}}*/

    //更新个人信息
    public function update($id = 0)
    {/*{{{*/
        //图片上传成功
        //if ($this->do_upload('logo'))
        //取图片名字
        //$data['logo'] = $this->upload->data()['file_name'];
        $data = array(
            'name' => $this->input->post('name'),
            'unitId' => $this->input->post('unitId'),
            'duty' => $this->input->post('duty'),
            'sex' => $this->input->post('sex'),
        );
        $result = $this->user_model->update_where($data, array('id'=> $id));
        if ($result == 1)
        {
            $this->get($id);
        }
        else
        {
            $this->set_content(-1, '更新失败', array('id' => '0'));
        }

    }/*}}}*/

    //通过id获取用户
    public function get($id = 0)
    {/*{{{*/
        $result = $this->user_model->get($id);
//        $this->load->model('unit_model', '', true);
//        $unit = $this->unit_model->get($result->unitId);
//        $result->unit = $unit;
//        $this->load->model('taskuserrelation_model', 'relation', true);
//        $taskRelations = $this->relation->select_where(array('taskId'), array('userId' => $id));
//        $taskIds = array();
//        if (count($taskRelations) > 0)
//        {
//            $taskRelations = json_decode(json_encode($taskRelations), true);
//
//            foreach($taskRelations as $key=>$value)
//            { 
//                $taskIds = array_merge_recursive($taskIds, array_values($value));
//            } 
//        }
//        $result->taskIds = $taskIds;
        $this->set_content(0, '更新成功', $result);
    }/*}}}*/

    //通过phone获取用户
    public function login($phone = 0)
    {/*{{{*/
        $result = $this->user_model->get_where(array('phone' => $phone));
        $result = isset($result) && count($result) == 1 ? $result[0] : array('id' => '0');
        if (is_array($result))
        {
            $this->set_content(-1, '账号不存在', $result);
        }
        else
        {
            if ($result->verify == 0)
            {
                $this->set_content(-1, '帐号未审核，请等待审核', $result);
            }
            else if ($result->verify == 2)
            {
                $this->set_content(-1, '帐号被冻结，请与管理员联系', $result);
            }
            else
            {
                $this->load->model('unit_model', '', true);
                $unit = $this->unit_model->get($result->unitId);
                $result->unit = $unit;
                $this->load->model('taskuserrelation_model', 'relation', true);
                $taskRelations = $this->relation->select_where(array('taskId'), array('userId' => $result->id));
                $taskIds = array();
                if (count($taskRelations) > 0)
                {
                    $taskRelations = json_decode(json_encode($taskRelations), true);

                    foreach($taskRelations as $key=>$value)
                    { 
                        $taskIds = array_merge_recursive($taskIds, array_values($value));
                    } 
                }
                $result->taskIds = $taskIds;
                $this->set_content(0, '登录成功', $result);
            }
        }

    }/*}}}*/

}
?>

