<?php

class Unitcontent_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     private $status = array('0' => '待完善', '1' => '待签字');

     //创建unitask
     function newUnitContent($data = array()) {
        if (count($data)) {
            return $this->create_id($data);
        }
     	return 0;
     }

     //批量创建unitTasks
     function newUnitTasks($childTaskId = 0, $unitIds = array()) {
        if (count($unitIds)) {
        	$unitTaskIds = array();
            foreach ($unitIds as $unitId) {
                $unitTaskId = $this->newUnitTask($childTaskId,  $unitId);
                $unitTaskIds[] = $unitTaskId;
            }


           	if ($unitTaskIds && count($unitTaskIds)) {
           		$this->load->model('trace_model');
           		$this->trace_model->newDefaultArray($unitTaskIds);
           	}

            return $unitTaskIds;
        }
        return array();
     }


     //将trace转换为UnitTask
     function getUnitTaskListByChildTask($childTaskId = 0) {

		$unitTasks = $this->get_where(array('childTaskId' => $childTaskId));


		$this->load->model('trace_model');

		$this->load->model('unit_model');
 
		$newUnitTasks = array();

		for ($n = 0; $n < count($unitTasks); $n++) {
			$trace = $this->trace_model->getLastTraceByUnitTask($unitTasks[$n]['id']);
			if ($trace && count($trace)) {
				$unitTask = $trace;
				$unit = $this->unit_model->get($trace['unitId']);
				$unit = ($unit && !empty($count) ? $unit : array('name' => "系统", "logo" => ""));
				$unitTask['id'] = $unitTasks[$n]['id'];
                $unitTask['childTaskId'] = $unitTasks[$n]['childTaskId'];
				$unitTask['traceId'] = $trace['id'];
                $unitTask['progress'] = $unitTasks[$n]['progress'];
				$unitTask['unitName'] = $unit['name'];
				$unitTask['unitLogo'] = $unit['logo'];
				$unitTask['statusTip'] = $this->status[$trace['status'].''];
				$newUnitTasks[] = $unitTask;
			}
		}
		return $newUnitTasks;
     }

     //获取unitTask
     function getUnitTask($unitTaskId = 0) {
        if (!$unitTaskId) return array();
        $this->load->model('trace_model');
        $this->load->model('unit_model');
        $this->load->model('childtask_model');
        $this->load->model('task_model');

        $unitTaskTmp = $this->get($unitTaskId);
        if (!$unitTaskTmp) return array();
        
        $childTask   = $this->childtask_model->get($unitTaskTmp['childTaskId']);
        if (!$childTask) return array();
        
        $task        = $this->task_model->get($childTask['taskId']);
        if (!$task) return array();
        

        $leaderUnit  = $this->unit_model->get($childTask['leaderUnitId']);
        
        $unit = $this->unit_model->get($unitTaskTmp['unitId']);
        $unit = ($unit && count($unit) ? $unit : array('name' => "系统", "logo" => ""));

        $trace = $this->trace_model->getLastTraceByUnitTask($unitTaskId);
        if ($trace && count($trace)) {
            $unitTask = $trace;
            
            $unitTask['id'] = $unitTaskTmp['id'];
            $unitTask['childTaskId'] = $unitTaskTmp['childTaskId'];
            $unitTask['traceId'] = $trace['id'];
            $unitTask['progress'] = $unitTaskTmp['progress'];
            $unitTask['unitName'] = $unit['name'];
            $unitTask['unitLogo'] = $unit['logo'];
            $unitTask['unitId'] = $unitTaskTmp['unitId'];
            $unitTask['statusTip'] = $this->status[$trace['status'].''];
            $newUnitTasks[] = $unitTask;
        }
        
        $unitTask['childTask'] = $childTask;
        $unitTask['task'] = $task;
        $unitTask['leaderUnit'] = $leaderUnit;

        return $unitTask;
     }

 }
 ?>
