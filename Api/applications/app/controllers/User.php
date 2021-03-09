<?php
class User extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    private function makeDoUpload() {
        $logo = '';
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            foreach ($data as $image) { 
                $logo .= $image['file_name'];
            } 
        }
        return $logo;
    }

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
            $user = $this->user_model->get($id);
            $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', $user);
        }
    }/*}}}*/


    public function getUsers($role = 0) {
        $users = $this->user_model->getUsers($role);
        $this->set_content(0, '获取成功', $users);
    }

    public function getUsersByUnit($unitId = 0) {
        $users = $this->user_model->get_where(array('unitId' => $unitId));
        $this->set_content(0, '获取成功', $users);
    }

    //更新个人信息
    public function update($id = 0)
    {/*{{{*/
        $data = $this->input->post();
        $data['logo'] = $this->makeDoUpload();
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


    //重置用户密码
    public function resetUserPassword($userId = 0)
    {/*{{{*/
        if (!empty($userId)) {
            $this->user_model->update_where(array('password' => '123456'), array('id'=> $userId));
            $this->set_content(0, '重置成功', array('id' => '0'));
        } else{
            $this->set_content(-1, '更新失败', array('id' => '0'));
        }
    }/*}}}*/


    //修改用户密码
    public function modifyUserPassword($userId = 0)
    {/*{{{*/
        $password = $this->input->post('password');
        if (!empty($userId)) {
            $this->user_model->update_where(array('password' => $password), array('id'=> $userId));
            $this->set_content(0, '修改成功', array('id' => '0'));
        } else{
            $this->set_content(-1, '更新失败', array('id' => '0'));
        }
    }/*}}}*/


        //更新个人信息
    public function updateUnit($id = 0)
    {/*{{{*/
        $user = $this->user_model->get($id);
        if (count($user) == 0) {
            $this->set_content(-1, '更新失败', array('errorId' => $id));
            return;
        }
        $data = array('unitId' => $this->input->post('unitId'));
        $this->user_model->update_where($data, array('id'=> $id));
        $user = $this->user_model->get($id);
        $this->load->model('unit_model');
        $user['unit'] = $this->unit_model->get($user['unitId']);
        $this->set_content(0, '更新成功', $user);
    }/*}}}*/

    //通过id获取用户
    public function get($id = 0)
    {/*{{{*/
        $result = $this->user_model->get($id);
        if ($result['unitId']) {
            $this->load->model('unit_model', '', true);
            $unit = $this->unit_model->get($result['unitId']);
            $result['unit'] = $unit;
        }
        $this->set_content(0, '更新成功', $result);
    }/*}}}*/

    //通过phone获取用户
    public function login($phone = 0)
    {/*{{{*/
        $result = $this->user_model->get_where(array('phone' => $phone));
        $result = isset($result) && count($result) == 1 ? $result[0] : array('id' => '0');
        if ($result['role'] == 0) {
            $this->set_content(0, '登录成功', $result);
        } else {
            if ($result['verify'] == 0) {
                $this->set_content(-1, '帐号未审核，请等待审核', $result);
            } else if ($result['verify'] == 1) {
                $this->load->model('unit_model', '', true);
                $unit = $this->unit_model->get($result['unitId']);
                $result['unit'] = $unit;
                $this->set_content(0, '登录成功', $result);
            } else if ($result['verify'] == 2) {
                $this->set_content(-1, '帐号被冻结，请与管理员联系', $result);
            } else {
                $this->set_content(-2, '账号异常,请联系管理员', $result);
            }
        }

    }/*}}}*/


     //通过phone获取用户
    public function userLogin()
    {/*{{{*/
        $result = $this->user_model->get_where($this->input->post());
        $result = isset($result) && count($result) == 1 ? $result[0] : array('id' => '0');
        if ($result['role'] == 0) {
            $this->set_content(0, '登录成功', $result);
        } else {
            if ($result['verify'] == 0) {
                $this->set_content(-1, '帐号未审核，请等待审核', $result);
            } else if ($result['verify'] == 1) {
                $this->load->model('unit_model', '', true);
                $unit = $this->unit_model->get($result['unitId']);
                $result['unit'] = $unit;
                $this->set_content(0, '登录成功', $result);
            } else if ($result['verify'] == 2) {
                $this->set_content(-1, '帐号被冻结，请与管理员联系', $result);
            } else {
                $this->set_content(-2, '账号异常,请联系管理员', $result);
            }
        }

    }/*}}}*/
}
?>

