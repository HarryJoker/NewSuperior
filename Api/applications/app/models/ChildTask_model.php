<?php

class Childtask_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     public function createUnitTasks($unitTasks = array()) {
     	$this->load->model('unittask_model');
		$rows = $this->unittask_model->create_batch($unitTasks);
		return $rows;
     }



    public function getChildTaskListByTask($taskId = 0) {
    	$childTasks = $this->get_where(array('taskId' => $taskId));

		$this->load->model('unittask_model');

		for ($n = 0; $n < count($childTasks); $n++) {
			$childTasks[$n]['unitTasks'] = $this->unittask_model->getUnitTaskListByChildTask($childTasks[$n]['id']);

            $unit = $this->unit_model->get($childTasks[$n]['leaderUnitId']);
            $childTasks[$n]['leaderUnitName'] = $unit ? $unit['name'] : '';
		}

    	return $childTasks;
    }


 }
 ?>
