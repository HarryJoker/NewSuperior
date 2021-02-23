<?php
class Selaoban extends CA_Controller {

	function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getVideos($id = 0)
    {/*{{{*/
        $result = $this->selaoban_model->get_where_with_limit(array('id >' => $id), 20);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

}
?>

