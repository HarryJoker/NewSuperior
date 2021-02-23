<?php
class Banner extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getBanners()
    {/*{{{*/
        $banners = $this->banner_model->result();
        if (count($banners) == 0) 
        {
            $banners = array();
            $banner = array('title' => '该月优秀荣誉单位：民政局', 'image' => 'banner_honour.png');
            $banners[] = $banner;

            $banner = array('title' => '电视问政：工商长安分局注册登记窗口违纪曝光', 'image' => 'banner_media.png');
            $banners[] = $banner;

            $banner = array('title' => '建设博兴新型农村，打造小康生活', 'image' => 'banner_city.png');
            $banners[] = $banner;

            $banner = array('title' => '建设博兴自然环境，爱护大自然，开发天然旅游', 'image' => 'banner_nature.png');
            $banners[] = $banner;
        }
        $this->set_content(0, '获取成功', $banners);
    }/*}}}*/

    public function updateBanner($bannerId = 0) {
        $id = $this->banner_model->update_where($this->input->post(), array('id' => $bannerId));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '更新成功' : '更新失败', array('rows' => $id));
    }

    public function newBanner() 
    {
        $data = $this->input->post();
        unset($data['B_Url']);
        unset($data['file']);
        $id = $this->banner_model->create_id($data);
        if ($id) {
            $this->set_content(0, '创建成功', array('id' => $id));
        } else {
            $this->set_content(-1, '创建成功', '');
        }
        
    }

    public function deleteBanner($id = 0)
    {
        $rows = $this->banner_model->delete(array('id'=>$id));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }
}
?>
