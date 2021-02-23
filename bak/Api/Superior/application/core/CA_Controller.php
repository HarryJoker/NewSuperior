<?php
class CA_Controller extends CI_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
        $this->load->model(strtolower(get_class($this).'_model'), '', true);
        $userAgent = $this->input->user_agent(); 
        //$this->output->enable_profiler(strpos($userAgent, '') === 0 ? false : true);
        $this->output->enable_profiler(false);
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

    protected function set_content($errorCode = 0, $msg = '', $content)
    {/*{{{*/
        $content = false == isset($content) ? '' : $content;
        $out = array('signature' => '', 'errorCode' => ''.$errorCode, 'errorMsg' => $msg, 'content' => $content);
        //$this->output->set_output(json_encode($out, true));
        $this->output->set_content_type('application/json')->set_output(json_encode($out));
    }/*}}}*/

    //form 单张图片
    protected function do_upload($fileName)
    {/*{{{*/
        $config['upload_path']='./uploads/';
        $config['allowed_types']='gif|jpg|png';
        //$config['max_size']=100;
        //$config['max_width']=1024;
        //$config['max_height']=768;
        $config['overwrite'] = false;
        $config['encrypt_name'] = true;
        $config['remove_spaces'] = true;

        $this->load->library('upload',$config);
        return $this->upload->do_upload($fileName);
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
