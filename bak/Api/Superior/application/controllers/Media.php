<?php
require 'Qiniu.php';

class Media extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->json();
        $id = $this->media_model->create_id($data);
        $data['id'] = $id;
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', $data);
    }/*}}}*/

    public function get($id = 0)
    {/*{{{*/
        $article = $this->media_model->get($id);
        $this->set_content(0, '获取成功', $article);
    }/*}}}*/

    public function update()
    {/*{{{*/
        
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $this->media_model->update_where($data,  array('id' => $data['id']));
            $data = $this->media_model->get_where(array('id' => $data['id']));
            $this->set_content(0, '更新成功', $data);
        }
        else
        {
            $this->set_content( -1 , '更新失败', '');
        }
    }/*}}}*/

    public function remove()
    {
        $data = $this->input->json();
        if (array_key_exists('ids', $data))
        {
            if (count($data['ids'])) $this->media_model->deleteBatch('id',  $data['ids']);        
            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(0, '删除失败', '');
        }
    }

    public function getMedias()
    {/*{{{*/
        $articles = $this->media_model->result();

        $token = (new Qiniu())->getToken('test');

        $result = array("list" => $articles, 'token' => array('token' => $token), "pagination" => array("total" => count($articles)));
        $this->set_content(0, '获取成功', $result);

    }/*}}}*/
}
?>