<?php
class Category extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->json();

        $count = $this->category_model->result_count(array('name' => $data['name']));
            if (!$count)
            {
                $id = $this->category_model->create_id($data);
                $data['id'] = $id;
                $this->set_content(0 , '创建成功', $data);
            }
            else
            {
                $this->set_content( -1 ,  '['.$data['name'].'] 已存在', '');
            }
        
    }/*}}}*/

    public function getCategorys()
    {/*{{{*/
        $categorys = $this->category_model->result();
        $count = $this->category_model->result_count();
        $result = array ('list' => $categorys, "pagination" => array("total" => $count));
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function update()
    {
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $count = $this->category_model->result_count(array('name' => $data['name']));
            if (!$count)
            {
                $this->category_model->update_where($data,  array('id' => $data['id']));
                $this->set_content(0, '更新成功', $data);
            }
            $this->set_content(-1,  '['.$data['name'].'] 已存在', $data);
            return;
        }
        else
        {
            $this->set_content( -1 , '数据错误', '');
        }
    }

    public function remove()
    {
        $data = $this->input->json();
        unset($data['ids']);
        if (array_key_exists('ids', $data))
        {
             $ids = $data['ids'];
            // if (count($ids)) $this->category_model->deleteBatch('id', $ids);   
            $this->set_content(0, '删除成功', '');
            return;
        }
        else
        {
            $this->set_content(-1, '删除失败', '');
        }
       
    }




}
?>