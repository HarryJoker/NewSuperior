<?php
class Crack extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
        $data = $this->input->post();
        if (isset($data['attachments']))
        {
            $data['attachments'] = $this->download($data['attachments']);
        }

        $count = $this->crack_model->select_count(array('sourceId' => $data['sourceId'], 'source' => $data['source']));
        if(isset($data['sourceId']) && $count)
        {
            $sourceId = $data['sourceId'];
            unset($data['sourceId']);
            $this->crack_model->update_where($data, array('sourceId' => $sourceId, 'source' => $data['source']));
            $result = array('sourceId' => $sourceId);
            $this->set_content(0, '持久化更新成功', $result);
        }
        else
        {
            $id = $this->crack_model->create_id($data);
            $result = array('sourceId' => $data['sourceId']);
            $this->set_content(0, '持久化成功', $result);
        }
    }/*}}}*/

    public function download($source = '')
    {/*{{{*/
        //
        $attachments = array();
        if ($source)
        {
            $imgs = explode(',',$source);
            foreach($imgs as $img)
            { 
                $dir = '.'.substr(strtolower($img), 0, 19);
                if(!is_dir($dir)) mkdir($dir); //创建文件夹
                $pic = file_get_contents('http://www.weike006.com'.$img);
                if (file_put_contents('.'.strtolower($img), $pic))
                {
                    $attachments[] = $img;
                }
            } 
        }
        return implode(',', $attachments);
    }/*}}}*/

    public function getCrackIds()
    {/*{{{*/
        //$sql = "select group_concat(id) as ids from tbl_crack where verify =1 and title != '';";
        $sql = "select group_concat(id) as ids from tbl_crack where verify = 0 and id > 8548;";
        $result = $this->crack_model->query($sql);
        $result = count($result) >= 1 ? $result[0] : array('ids' => 0);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getPageCracksById($id = 0, $type = 0, $city = 0)
    {/*{{{*/
        $sql = "select * from tbl_crack where title != '' and id > ".$id; 
        if ($city > 0) {
            $sql = $sql." and city = ".$city;
        }
//        else
//        {
//            if ($province > 0 ) $sql = $sql." and province =".$province;
//        }
        if ($type > 0)
        {
            $sql = $sql." and type = ".$type;
        }
        $sql = $sql." limit 15;";
        $result = $this->crack_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getAreas()
    {/*{{{*/
        $sql = "select province as id, if(provinceName='', '全部', provinceName) as name from tbl_crack group by province;";
        $result = $this->crack_model->query($sql);
        foreach($result as $area)
        {
            $sql = "select city as id, if(cityName='选择县区', '全部', cityName) as name from tbl_crack where province=".$area->id." group by cityName order by city;";
            $citys = $this->crack_model->query($sql);
            $area->citys = $citys;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getFavoriteByUser($userId)
    {/*{{{*/
        $sql = 'select * from tbl_crack where id in (select crackId from tbl_favorite where userId = '.$userId.');';
        $result = $this->crack_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

}
?>

