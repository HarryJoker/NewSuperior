<?php
class Pay extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
//        var_dump($_FILES);
//        return;
        $data = array();
        $config['upload_path']='./pays/';
        $config['allowed_types']='gif|jpg|png';
        //$config['max_size']=100;
        //$config['max_width']=1024;
        //$config['max_height']=768;
        $config['overwrite'] = false;
        $config['encrypt_name'] = true;
        $config['remove_spaces'] = true;
        $this->load->library('upload',$config);
        if ($this->upload->do_upload('userfile')) 
        {
            //上传成功
            $data = $this->upload->data();
            $params = $this->input->post();
            $params['proof'] = $data['file_name'];
            if (!in_array(null, array_values($params)) && count($params) == 4)
            {
                $id = $this->pay_model->create_id($params);
                $this->set_content(0, '上传成功', $id);
            }
            else
            {
                $this->set_content(-1, '上传失败', $params);
            }
        } else {
            //上传失败
            $data = $this->upload->display_errors();
            $this->set_content(-1, '上传失败', $data);
        }
    }/*}}}*/

}
?>

