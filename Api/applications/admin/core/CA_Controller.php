<?php
class CA_Controller extends CI_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
        $this->load->helper('url');
        $this->load->model(strtolower(get_class($this).'_model'), '', true);
    }/*}}}*/


    protected function is_login()
    {/*{{{*/
        $this->load->model('admin_model');
        $cur_user = $this->admin_model->is_login();
        if($cur_user === false)
        {
            redirect('login');
            return false;
        }
        else
        {
            //如果已经登陆，则重新设置cookie的有效期
            $this->input->set_cookie("username",$cur_user['username'],3600);
            $this->input->set_cookie("password",$cur_user['password'],3600);
            $this->input->set_cookie("user_id",$cur_user['id'],3600);
            return true;
        }
    }/*}}}*/

    public function index()
    {/*{{{*/
        $userAgent = $this->input->user_agent(); 
        if (!strpos($userAgent, 'GDIS'))
        {
            $model = strtolower(get_class($this)).'_model';
            echo 'default action index from';
            echo '[Model:'.$model.'] in ';
            echo '[Controller:'.get_class($this).']';
        }
    }/*}}}*/

    /**
     * square: 默认0裁剪正方形，1裁剪6:4比例的
     *
     * */
    protected function do_upload($square = 0, $fileName)
    {/*{{{*/
        $config['upload_path']='./uploads/';
        $config['allowed_types']='gif|jpg|jpeg|png';
        if ($square == 0)
        {
            $config['max_width']=200;
            $config['max_height']=200;
            $config['max_size']= 200;
        }
//        else if ($square == 1)
//        {
//            $config['max_width']=600;
//            $config['max_height']=400;
//            $config['max_size']= 800;
//        }
        $config['overwrite'] = false;
        $config['encrypt_name'] = true;
        $config['remove_spaces'] = true;
        $this->load->library('upload',$config);

        if($this->upload->do_upload($fileName))
        {
            //var_dump($this->upload->data());
            $data = $this->upload->data();
            return $data['file_name'];
        }
        else
        {
            //var_dump($this->upload->display_errors());
            return false;
        }
        
    }/*}}}*/

    protected function do_multiple_upload()
    {/*{{{*/
        $data = array();
        $config['upload_path']='./uploads/';
        $config['allowed_types']='gif|jpg|png';
        //$config['max_size']=100;
        //$config['max_width']=1024;
        //$config['max_height']=768;
        $config['overwrite'] = false;
        $config['encrypt_name'] = true;
        $config['remove_spaces'] = true;
        $this->load->library('upload',$config);
        //循环处理上传文件
        foreach ($_FILES as $key => $value) 
        {
            if ($this->upload->do_upload($key)) 
            {
                //上传成功
                $data[] = $this->upload->data();
            } else {
                //上传失败
                $data[] = $this->upload->display_errors();
            }
        }
        return $data;
    }/*}}}*/

    protected function do_thumb($data, $width = 150, $height = 150)
    {/*{{{*/
        foreach ($data as $image) 
        {
            $fileName = "./uploads/".$image['file_name'];
            $thumbConfig['image_library'] = 'gd2';
            $thumbConfig['source_image'] = $fileName;
            $thumbConfig['create_thumb'] = TRUE;
            $thumbConfig['maintain_ratio'] = TRUE;
            $thumbConfig['width'] = $width;
            $thumbConfig['height'] = $height;
            $this->load->library('image_lib', $thumbConfig);
            if ($this->image_lib->resize())
            {
                var_dump('create Thumb success');
            } 
            else 
            {
                var_dump($this->image_lib->display_errors());
            }
        }
    }/*}}}*/

}
?>
