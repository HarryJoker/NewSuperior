<?php
class UnitContent extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function newUnitContent()
    {/*{{{*/

        $attachments = array();
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            //$this->do_thumb($data);
            foreach ($data as $image)
            { 
                $attachments[] = $image['file_name'];
            } 
        }

        //post 获取参数
        $data = $this->input->post();
        $data['status'] = 1;
        $data['attachments'] = count($attachments) ? implode(",", $attachments) : '';
        
        $rows = $this->unitcontent_model->newUnitContent($data);

        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '创建成功' : '创建失败', array('rows' => $rows));
    }/*}}}*/


    //退回将status状态置0
    public function update($id = 0) {
        $rows = $this->unitcontent_model->update_where(array('status' => 0), array('id' => $id));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '退回修改成功' : '退回失败', array('rows' => $rows));
    }



     //退回将status状态置0
    public function recommit($unitTaskId = 0) {

        $attachments = array();
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            //$this->do_thumb($data);
            foreach ($data as $image)
            { 
                $attachments[] = $image['file_name'];
            } 
        }


        //post 获取参数
        $data = $this->input->post();
        $data['status'] = 1;

        // var_dump($data);

        //已存在的附件转换为数组
        // $oldAttachments = explode(",", isset($data['attachments']) ? $data['attachments'] : '');

        $oldAttachments = isset($data['attachments']) && strlen($data['attachments']) ? explode(",", $data['attachments']) : array();

        //新老合并
        $newAttachments = array_merge($oldAttachments, $attachments);
        //序列化
        $data['attachments'] = implode(",", $newAttachments);

        // var_dump($data);

        $rows = $this->unitcontent_model->update_where($data, array('unitTaskId' => $unitTaskId));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '退回修改成功' : '退回失败', array('rows' => $rows));
    }


    //退回将status状态置0
    public function commitBack($unitTaskId = 0) {
        $rows = $this->unitcontent_model->update_where(array('status' => 0, 'userId' => 0), array('unitTaskId' => $unitTaskId));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '退回修改成功' : '退回失败', array('rows' => $rows));
    }


    public function delete($unitTaskId = 0)
    {
        $rows = $this->unittask_model->delete(array('id'=>$unitTaskId));
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
    }


    public function getUnitContent($unitTaskId = 0) {
        $unitContents = $this->unitcontent_model->get_where(array('unitTaskId' => $unitTaskId));
        if (count($unitContents) > 0) {
            $this->set_content(0, '获取成功', $unitContents[0]);
        } else {
            $this->set_content(-1, '获取失败', array());
        }
    }

}
?>
