<?php

class Trace_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     
    public function newDefault($unitTaskId) {

    }

    public function newDefaultArray($unitTaskIds = array()) {
        $data = array();
        foreach ($unitTaskIds as $val) {
            $data[] = array('unitTaskId' => $val, 'content' => '已发布任务给该单位');
        }
        return $this->create_batch($data);
    }

    //hasUnitSaty 是否包含部门内部报送审核trace
    function getTracesByUnitTask($unitTaskId = 0, $hasUnitSaty = 0) {
        $sql = 'select e.*, u.name as userName, n.name as unitName, n.logo as unitLogo from tbl_trace e left join tbl_user u on e.userId = u.id left join tbl_unit n on e.unitId = n.id where e.status >= '.($hasUnitSaty > 0 ? -3 : 0).' and e.unitTaskId ='.$unitTaskId.' order by id desc';
        $traces = $this->query($sql);
        return $traces;
    }


    function getLastTraceForTask($taskId) {
        $traces = $this->getLastTraceForTasks(array($taskId));
        return $traces ? $traces[0] : array(); 
    }


    function getLastTraceForTasks($taskIds) {
        $taskIds = implode(',',$taskIds);
        $sql = 'select * from tbl_trace where id in 
        (select max(id) from tbl_trace where taskId in ('.$taskIds.') group by taskId)';
        $traces = $this->query($sql);
        $traces = $traces ? array_column($traces, null, "taskId") : array();
        return $traces ? $traces : array();
    }


     function getLastTraceByUnitTask($unitTaskId) {
     	$sql = 'select * from tbl_trace where unitTaskId = '.$unitTaskId.' order by id desc limit 1';
     	$trace = $this->queryRow($sql);
     	return $trace ? $trace : array();
     }


}
?>

