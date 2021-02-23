<?php

class Comment_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     function getCommentsByType($unitTaskId = 0, $type = 0) {
     	$sql = 'select c.*, u.name, u.logo from tbl_comment c left join tbl_user u on c.userId = u.id where c.unitTaskId = '.$unitTaskId;
     	if ($type == 0) {
			
     	} else if ($type == 1) {
     		$sql .= ' and c.score >= 4 ';
     	} else if ($type == 2) {
     		$sql .= ' and c.score < 4 and c.score >=3 ';
     	} else if ($type = 3) {
     		$sql .= ' and c.score < 3 ';
     	}
     	$sql .= ' order by c.id desc';
     	$comments = $this->query($sql);
     	return $comments;
     }


     function getCommentOpinions() {
     	$this->load->model('task_model');
     	$tasks = $this->task_model->getTaskListForPeople();
     	for($n = 0; $n < count($tasks); $n++) {
     		for ($i=0; $i < count($tasks[$n]['unitTasks']); $i++) { 
     			$tasks[$n]['unitTasks'][$i]['commentSummary'] = $this->getUnitTaskCommentSummary($tasks[$n]['unitTasks'][$i]['id']);
     		}
     	}
     	return $tasks;
     }


     function getUnitTaskCommentSummary($unitTaskId = 0) {
     	if (!$unitTaskId) return array('total' => 0, 'goodNum' => 0, 'commNum' => 0, 'badNum' => 0);
     	$summary = array();
     	$summary['total'] = $this->excute('select id from tbl_comment where unitTaskId = '.$unitTaskId);
     	$summary['goodNum'] = $this->excute('select id from tbl_comment where score >= 4 and unitTaskId = '.$unitTaskId);
     	$summary['commNum'] = $this->excute('select id from tbl_comment where score < 4 and score >= 3 and unitTaskId = '.$unitTaskId);
     	$summary['badNum'] = $this->excute('select id from tbl_comment where score < 3 and unitTaskId = '.$unitTaskId);
     	return $summary;
     }

}
?>