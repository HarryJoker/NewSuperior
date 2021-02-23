<?php
class Store extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
      $data = $this->input->json();
      $file = $data['file'];
      unset($data['file']);
      if (count($file) && array_key_exists('fileName', $file)) 
      {
        $data['logo'] = 'http://img.yooar.com/'.$file['fileName'];
        $id = $this->store_model->create_id($data);
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
      } 
      else 
      {
        $this->set_content(-1, '提交失败', '');
      }
      
    }/*}}}*/

    public function up()
    {
        $config['upload_path']='./uploads/';
        $config['allowed_types']='gif|jpg|png';
        $config['max_width']=1024;
        $config['max_height']=768;
        $this->load->library('upload',$config);
        if(!$this->upload->do_upload('logo')){
          $error=array('error'=>$this->upload->display_errors());
          $this->set_content(-1, '提交失败', $error);
        }else{
          $data=array('upload_data'=>$this->upload->data());
        $this->set_content(0, '上传成功', $data);
        }
    }

    public function getStores()
    {
        $stores = $this->store_model->result();
        $this->set_content( 0 , '获取成功', $stores);
    }

    public function getStore($storeId = 0)
    {/*{{{*/
        $store = $this->store_model->get($storeId);
        $this->set_content(0, '获取成功', $store);
    }/*}}}*/

    public function update() 
    {
        $data = $this->input->post();
        $id = $this->store_model->update_where($data, array('id' => $this->input->post("id")));
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }
}
?>