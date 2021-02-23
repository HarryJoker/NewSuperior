<?php

class Drafttask_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


	function deployDraftTasks($ids = array()) {
     	$sql = 'select * from tbl_drafttask where id in ('.implode(',', $ids).')';
     	$draftTasks = $this->query($sql);
     	$this->load->model('task_model');
     	$this->load->model('unittask_model');
     	$this->load->model('trace_model');
     	foreach ($draftTasks as $value) {
     		$unitIds = explode(',', $value['unitIds']);
     		$draftTaskId = $value['id'];
     		unset($value['id']);
     		unset($value['unitIds']);
     		unset($value['unitNames']);
     		unset($value['draftId']);
     		$taskId = $this->deployTask($value);
     		$this->deployUnitTasks($taskId, $unitIds, $value);
     		$this->delete(array('id' => $draftTaskId));
     	}
     }

     private function deployTask($task) {
     	$id = $this->task_model->create_id($task);
     	return $id;
     }

    private function deployUnitTasks($taskId, $unitIds, $task) {
     	foreach ($unitIds as $value) {
     		$unitTaskId = $this->unittask_model->create_id(array('taskId' => $taskId, 'unitId' => $value));
            if ($task && strcmp($task['taskLabel'], "民生实事") == 0) {
                $this->load->model('opinion_model');
                $this->opinion_model->create_id(array('content' => $task['content'], 
                                                        'title' => $task['title'], 
                                                        'category' => -1, 
                                                        'unitTaskId' => $unitTaskId, 
                                                        'status' => 6));
            }
     		$this->deployUnitTaskTrace($unitTaskId, $value);
     	}
    }

    private function deployUnitTaskTrace($unitTaskId, $unitId) {
    	$id = $this->trace_model->create_id(array('unitTaskId' => $unitTaskId, 'unitId' => $unitId, 'status' => 0, 'content' => '已发布任务给该单位'));
    }
 }
 ?>
