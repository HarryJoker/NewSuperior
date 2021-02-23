<?php
class Message extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function get($messageId)
    {/*{{{*/
        $result = $this->message_model->get($messageId);
        $this->message_model->update_where(array('read' => '1'), array('id' => $messageId));
        $this->set_content(0, '获取成功', count($result) == 0 ? array('content' => '') : $result);
    }/*}}}*/


    public function newMessage()
    {/*{{{*/
        $id = $this->message_model->create_id($this->input->post());
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', array('id' => $id));
    }/*}}}*/

    public function update($messageId = 0) {
        $id = $this->message_model->update_where($this->input->post(), array('id' => $messageId));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '更新成功' : '更新失败', array('rows' => $id));
    }

    public function delete($messageId)
    {
        $id = $this->message_model->delete(array('id'=>$messageId));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '删除成功' : '创建失败', array('rows' => $id));
    }

    public function getAllMessage($unitId = 0)
    {/*{{{*/
        $sql = 'select * from tbl_message where unitId = 0 or unitId = '.$unitId.' order by id desc;';
        $result = $this->message_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getMessageList()
    {/*{{{*/
        $sql = 'select * from tbl_message order by id desc;';
        $result = $this->message_model->query($sql);
        $result = array_values($result);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

}
?>

