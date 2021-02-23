<?php
class Clasz extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/

        $data = $this->input->json();
        if (count($data)) 
        {
            $files = $data['file'];
            unset($data['file']);
            $id = $this->clasz_model->create_id($data);
            if ($id && isset($files) && $files) {
                $albums = array();
                if (count($files)) 
                {
                    foreach ($files as $file) 
                    {
                        $albums[] = array('module' => 2, 'moduleId' => $id, 'type' => 1, 'url' => $file['thumbUrl']);
                    }
                    $this->load->model('album_model');
                    $rows = $this->album_model->create_batch($albums);
                }
            }
            $albums = $this->album_model->get_where(array('module' => 2, 'moduleId' => $id, 'type' => 1));
            $data['albums'] = $albums;
            $data['id'] = $id;
            $this->set_content(0, '提交成功', $data);
        }
        else 
        {
            $this->set_content(-1, '提交失败', '');
        }
        
    }/*}}}*/

    public function getClaszs()
    {/*{{{*/
        $claszs = $this->clasz_model->resultOrderBy('createdTime');
        $this->load->model('album_model');
        foreach ($claszs as &$clasz) $clasz['albums'] = $this->album_model->getThumbs(2, $clasz['id'], 1);
        
        $count = $this->clasz_model->result_count();

        $this->load->model('category_model');
        $categorys = $this->category_model->result();

        require 'Qiniu.php';
        $qiniu = new Qiniu();
        $token = $qiniu->getToken('test');


        $result = array("list" => $claszs, 'categorys' => $categorys, "pagination" => array("total" => $count), 'token' => (array('token' => $token)));

        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    public function getClaszsById($id =8)
    {
        $data = $this->clasz_model->get_where(array('id' => $id));
        $this->load->model('album_model');
        $data['albums'] = $this->album_model->get_where(array('module' => 2, 'moduleId' => $id, 'type' => 1));
        $this->set_content(0, '更新成功', $data);
    
    }


    public function update()
    {
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $id = $data['id'];
            unset($data['id']);
            $this->load->model('album_model');
            $rows = $this->album_model->updateThumbs($data['file'], 2, $id);
            unset($data['file']);
            $this->clasz_model->update_where($data,  array('id' => $id));

            $data = $this->clasz_model->get_where(array('id' => $id));

            $data['albums'] = $this->album_model->get_where(array('module' => 2, 'moduleId' => $id, 'type' => 1));

            $this->set_content(0, '更新成功', $data);
        }
        else
        {
            $this->set_content( -1 , '更新失败', '');
        }
    }

    public function remove()
    {
        $data = $this->input->json();
        if (array_key_exists('ids', $data))
        {
             $ids = $data['ids'];
            if (count($ids)) $this->clasz_model->deleteBatch('id', $ids);        
            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(0, '删除失败', '');
        }
       
    }
}
?>