<?php
class Reporttask_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/




     function reportUnitTask2Leader($leaderUnitId = 0, $unitTaskId = 0) {
     	if ($leaderUnitId && $unitTaskId) {
     		$this->create_id(array('unitId' => $leaderUnitId, 'unitTaskId' => $unitTaskId));
     	}
     }


     function reportUnitTask2Leaders($leaderUnitIds = array(), $unitTaskId = 0) {
     	if($unitTaskId && count($leaderUnitIds)) {
     		foreach ($leaderUnitIds as $unitId) {
     			$this->reportUnitTask2Leader($unitId, $unitTaskId);
     		}
     	}
     }


}
?>
