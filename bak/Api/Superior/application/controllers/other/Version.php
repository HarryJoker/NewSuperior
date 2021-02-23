<?php
class Version extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getVersion()
    {/*{{{*/
		$sql = 'select id as version, content, ctime from tbl_version order by id DESC limit 1;';
        $result = $this->version_model->query($sql);
        $result = count($result) ==  1 ? $result[0] : array('version' => '0');
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/
}
?>
