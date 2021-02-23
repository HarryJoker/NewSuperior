<?php
class Favorite extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function favorite($userId, $crackId)
    {/*{{{*/
        $id = $this->favorite_model->create(array('userId' => $userId, 'crackId' => $crackId));
        $this->set_content(0, '收藏成功', array('id' => $id));
    }/*}}}*/

    public function disFavorite($userId, $crackId)
    {/*{{{*/
        $sql = 'delete from tbl_favorite where userId='.$userId.' and crackId='.$crackId.';';
        $rows = $this->favorite_model->excute($sql);
        $this->set_content(0, '收藏成功', array('rows' => $rows));
    }/*}}}*/

}
?>

