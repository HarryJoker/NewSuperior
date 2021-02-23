<?php
class Studentclass_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     function getClassCount($studentId) 
     {
     	$sql = 'select sum(specNum) as count from tbl_studentclass where studentId='.$studentId;
     	$result = $this->query($sql);
     	if (count($result) == 1 && count($result[0]) == 1) return $result[0]['count'];
     	return 0;
     }

}
?>