<?php

class Unittask_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/

	// 0：未领取，1：已领取，   
	// 21：领导批示，   
	// 31：已报送，          
	// 51：系统催报，52：督查催报                 
	// 71：已审核(正常)，72：进度缓慢，73：进度较快， 74：已退回，       
	// 91：完成




     private $status = array('0' => '未领取', '1' => '已领取', '20' => '上报领导', '21' => '领导批示', '31' => '已报送', '51' => '系统催报', '52' => '督查催报', '71' => '已审核', '72' => '进度缓慢', '73' => '进度较快', '74' => '已退回', '91' => '已完成');

     //创建unitask
     function newUnitTask($taskId = 0, $unitId = 0) {
     	if ($taskId && $unitId) {
     		$unitTaskId = $this->create_id(array('taskId' => $taskId, 'unitId' => $unitId));
            $this->load->model('trace_model');
            $this->trace_model->create_id( array('unitTaskId' => $unitTaskId, 'content' => '已发布任务给该单位'));
     	}
     	return 0;
     }

     //批量创建unitTasks
     function newUnitTasks($taskId = 0, $unitIds = array()) {
        if (count($unitIds)) {
        	$unitTasks = array();
            foreach ($unitIds as $unitId) {
                // $unitTasks[] = array('unitId' => $unitId, 'taskId' => $taskId);
                $this->newUnitTask($taskId, $unitId);
            }
            // $ids = $this->create_batch($unitTasks);
            // var_dump($ids);
            // return $ids;
        }
        // return array();
     }

     //批量创建unitTasks
     function newUnitTasksWithStatus($taskId = 0, $unitIds = array(), $status = 0) {
        if (count($unitIds)) {
            $unitTasks = array();
            foreach ($unitIds as $unitId) {
                $unitTasks[] = array('unitId' => $unitId, 'taskId' => $taskId, 'status' => $status);
            }
            return $this->create_batch($unitTasks);
        }
        return array();
     }

     // function updateUnitTask($taskId = 0, $unitIds = array()) {
     //    $sql = 'select unitId from tbl_unittask where taskId = '.$taskId;
     //    $result = $this->queryRow($sql);
     //    $delArr = array_diff($result, $unitIds);
     // }


     //更新单位任务状态
     function udpateUnitTaskById($unitTaskId = 0, $status = 0, $progressStatus = 0, $reportProgress = 0, $verifyProgress = 0) {
        if (!$unitTaskId) return;
        $data = array();
        if ($status) $data['status'] = $status;
        if ($progressStatus) $data['progressStatus'] = $progressStatus;
        if ($reportProgress) $data['reportProgress'] = $reportProgress;
        if ($verifyProgress) $data['verifyProgress'] = $verifyProgress;
        if (count($data)) {
            $rows = $this->update_where($data, array('id' => $unitTaskId));
            return $rows;
        }
     }


    //更新单位报送工作月份
    function udpateUnitTaskMonthById($unitTaskId = 0, $monthTime = 0) {
        if (!$unitTaskId) return;
        $rows = $this->update_where(array('taskReportTime' => $monthTime), array('id' => $unitTaskId));
        return $rows;
    }

    //更新单位报送工作月状态
    function udpateMonthUnitTaskStatusById($unitTaskId, $progressStatus) {
        if (!$unitTaskId) return;
        if ($progressStatus == 71 || $progressStatus == 72 || $progressStatus == 73) {
            $unitTask = $this->unittask_model->get($unitTaskId);
            $this->load->model('monthunittask_model');
            $this->monthunittask_model->saveMonthUnitTaskStatus($unitTaskId, $unitTask['taskId'], $unitTask['taskReportTime'], $unitTask['progressStatus']);
        }
    }


     //更新报送进度
     function udpateReportProgress($taskId = 0, $unitId = 0, $progress = 0) {
        $rows = $this->update_where(array('reportProgress' => $progress), array('taskId' => $taskId, 'unitId' => $unitId));
        return $rows;
     }

      //更新报送进度
     function udpateUnitTaskReportProgress($unitTaskId = 0, $progress = 0) {
        if ($unitTaskId && $progress) {
            $rows = $this->update_where(array('reportProgress' => $progress), array('id' => $unitTaskId));
            return $rows;
        }
     }

     //更新审核进度
     function udpateVerifyProgress($taskId = 0, $unitId = 0, $progress = 0) {
        $rows = $this->update_where(array('verifyProgress' => $progress), array('taskId' => $taskId, 'unitId' => $unitId));
        return $rows;
     }

     //更新审核状态
     function udpateVerifyStatus($taskId = 0, $unitId = 0, $status = 0) {
        $rows = $this->update_where(array('status' => $status), array('taskId' => $taskId, 'unitId' => $unitId));
        return $rows;
     }

     //更新责任部门
     function udpateTaskUnits($taskId = 0, $unitIds = array()) {
        $values = '';
        foreach ($unitIds as $key => $unitId) {
            if (strlen($unitId) > 0) {
                $values.= strlen($values) > 0 ? ', ' : '';
                $values.= '('.$taskId.',"'.date("Y-m-d H:i:s").'",'.$unitId.')';
            }
        }
        if (strlen($values) > 0) {
            $sql = 'insert into tbl_unittask(taskId, updateTime, unitId) values '.$values.' ON DUPLICATE KEY UPDATE updateTime = values(updateTime)';
            $rows = $this->excute($sql);
            return $rows;
        }
        return false;
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
				$unit = $this->unit_model->get($unitTasks[$n]['unitId']);
				$unit = ($unit && !empty($unit) ? $unit : array('name' => "系统", "logo" => ""));
				$unitTask['id'] = $unitTasks[$n]['id'];
                $unitTask['childTaskId'] = $unitTasks[$n]['childTaskId'];
				$unitTask['traceId'] = $trace['id'];
                $unitTask['progress'] = $unitTasks[$n]['progress'];
				$unitTask['unitName'] = $unit['name'];
				$unitTask['unitLogo'] = $unit['logo'];
                $unitTask['unitId'] = $unit['id'];
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


    function getPlatformUnitTaskCount() {
        $sql = 'select count(*) as unitTaskCount from tbl_unittask';
        $result = $this->queryRow($sql);
        return $result;
    }

    function saveMonthStatus($taskId = 0, $unitTaskId = 0, $status) {

    }


//    function getPlatformUnitTaskCount() {
//        $sql = 'select count(*) as unitTaskCount from tbl_unittask';
//        $result = $this->queryRow($sql);
//        return $result;
//    }

 }
 ?>
