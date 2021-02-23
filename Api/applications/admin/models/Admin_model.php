<?php
class Admin_model extends CA_Model {

     function __construct ()
     {
         parent::__construct();
     }

    public function is_login()
    {/*{{{*/
        //获取cookie中的值
        if(empty($_COOKIE['username']) || empty($_COOKIE['password']))
        {
            $user_info = false;
        }
        else
        {
            $user_info=$this->check_user_login($_COOKIE['username'],$_COOKIE['password']);
        }
        return $user_info;
    }/*}}}*/

    /**
     *根据用户名及加密密码从数据库中获取用户信息，如果能获取到，则返回获取到的用户信息，否则返回false，注意：密码为加密密码
     *
     * */
    public function check_user_login($username,$password)
    {/*{{{*/
        //这里大家要注意：$password为md5加密后的密码
        //$this->db->query("select * from ");
        //快捷查询类的使用：能为我们提供快速获取数据的方法
        //此数组为查询条件
        //注意：关联数组
        $arr=array(
            'username'=>$username,//用户名
            'password'=>$password,//加密密码
        );
        //在database.php文件中已经设置了数据表的前缀，所以此时数据表无需带前缀
        $query = $this->db->get_where('admin', $arr);
        $user_info=$query->row_array();
        if(!empty($user_info))
        {
            return $user_info;
        }
        else
        {
            return false;
        }
    }/*}}}*/

 }
 ?>
