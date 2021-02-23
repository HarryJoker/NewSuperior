<?php
class Banner extends CA_Controller {
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
        $data['src'] = 'http://img.yooar.com/'.$file['fileName'];
        $id = $this->banner_model->create_id($data);
        $banner = $this->banner_model->get($id);
        $this->set_content(0, '提交失败', $banner);
      } 
      else 
      {
        $this->set_content(-1, '提交失败', '');
      }
    }/*}}}*/

    public function getBanners()
    {/*{{{*/
        $banners = $this->banner_model->resultOrderBy('createTime');
        $result = array("list" => $banners, "pagination" => array("total" => count($banners)));
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

     public function update($bannerId = 0) 
    {
        $data = $this->input->post();
        $id = $this->banner_model->update_where($data, array('id' => $bannerId));
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }

    public function remove($id = 0)
    {
        $data = $this->input->json();
        $ids = $data['ids'];
        if (count($ids)) $this->banner_model->deleteBatch('id', $ids);        
        $this->set_content(0, '删除成功', '');
    }
}
?>