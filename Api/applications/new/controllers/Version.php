<?php
class Version extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getVersion()
    {/*{{{*/
        $result = $this->version_model->get_where(array('id' => '1'));
        $result = count($result) ==  1 ? $result[0] : array('version' => '0');
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/
}
?>
