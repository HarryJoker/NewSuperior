<?php

class Trace_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



// insert into `tbl_trace` (`childTaskId`, `content`, `category`, `type`, `status`) values ('34', '未领取', '1', '1', 0);


     function createForTaskFinished($taskId = 0, $status = 11)
     {
     	if ($taskId == 0) return 0;
     	$sql = 'select id, category from tbl_childTask where taskId = '.$taskId;
     	$result = $this->query($sql);
     	$data = array();
     	foreach ($result as $item) {
     		$data[] = array('childTaskId' => $item->id, 'content' => '调度完成', 'category' => $item->category, 'status' => $status);
     	}
     	return $this->create_batch($data);
     }

}
?>

