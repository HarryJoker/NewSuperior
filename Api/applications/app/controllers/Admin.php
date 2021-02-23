<?php
class Admin extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
        $data = array(
            'content' => $this->input->post('content'),
            'userId' => $this->input->post('userId') 
        );
        //hahhh
        $id = $this->advice_model->create_id($data);
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/


    //通过phone获取用户
    public function login(){/*{{{*/
        $result = $this->admin_model->get_where($this->input->post());
        if (isset($result) && count($result) == 1) {
            $token = $this->generateToken($this->input->post('username'));
            $this->admin_model->update_where(array('token' => $token), $this->input->post());
            $this->set_content(0, '登录成功', array('token' => $token));
            // echo '{"code":0, "data":{"token":"admin-token"}}';
        } else {
            $this->set_content(-1, '登录失败', '');
        }
    }/*}}}*/


    public function info($token = "") {
        $admin = $this->admin_model->get_where(array('token' => $token));

        // echo '{"code":0, "data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}';
        if ($admin && count($admin) == 1) {
            $admin = $admin[0];
            $admin['roles'] = array('admin');
            $this->set_content(0, '获取成功', $admin);
        } else {

        }
    }

    public function logout() {
        $this->set_content(0, '退出成功', '');
    }

    /**
     * 设置登录token  唯一性
     * @param $userName 用户名
     * @retrun  String 
     */
    private function generateToken($userName = ''){
        $str = md5(uniqid(md5(microtime(true)),true));
        $token = sha1($str.$userName);
        return $token;
    }

}
?>
