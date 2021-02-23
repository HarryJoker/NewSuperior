<?php
class Banner extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getBanners()
    {/*{{{*/
        $banners = $this->banner_model->get_where(array('availably' => 1));
        if (count($banners) == 0) 
        {
            $banners = array();
            $banner = array('title' => '该月优秀荣誉单位：民政局', 'attchment' => 'banner_honour.png');
            $banners[] = $banner;

            $banner = array('title' => '电视问政：工商长安分局注册登记窗口违纪曝光', 'attchment' => 'banner_media.png');
            $banners[] = $banner;

            $banner = array('title' => '建设博兴新型农村，打造小康生活', 'attchment' => 'banner_city.png');
            $banners[] = $banner;

            $banner = array('title' => '建设博兴自然环境，爱护大自然，开发天然旅游', 'attchment' => 'banner_nature.png');
            $banners[] = $banner;
        }
        $this->set_content(0, '获取成功', $banners);
    }/*}}}*/
}
?>
