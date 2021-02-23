<?php
class Version_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     function getNewVersion() {
     	$sql = 'select * from tbl_version order by id desc limit 1';
     	return $this->queryRow($sql);
     }

}
?>
