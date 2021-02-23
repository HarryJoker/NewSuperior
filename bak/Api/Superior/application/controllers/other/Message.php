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

    public function getAllMessage($unitId = 0)
    {/*{{{*/
        $sql = 'select * from tbl_message where unitId = 0 or unitId = '.$unitId.';';
        $result = $this->message_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

}
?>

