<?php
class Login extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function login()
    {/*{{{*/
//        var_dump(date('Y-m-d H:i:s'));
//        var_dump(date('a') == 'am');
//        return;
        if(empty($_POST['username']) || empty($_POST['password']))
        {
            $this->load->view('login.php');
        }
        else
        {
            $username = isset($_POST['username'])&&!empty($_POST['username'])?trim($_POST['username']):'';
            $password = isset($_POST['password'])&&!empty($_POST['password'])?trim($_POST['password']):'';
            $this->load->model('login_model');

            $user_info = $this->login_model->check_user_login($username, $password);
            //验证用户名密码是否正确
            if($user_info['id'] > 0) 
            {
                echo "登陆失败";
            }
            else
            {
                $this->input->set_cookie("username",$username,3600);
                $this->input->set_cookie("password",$password,3600);
                $this->input->set_cookie("user_id",$user_info['id'],3600);
                echo 0;
            }
        }
    }/*}}}*/

}
