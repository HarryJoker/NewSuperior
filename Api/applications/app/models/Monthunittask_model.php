<?php

class Monthunittask_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


    function saveMonthUnitTaskStatus($unitTaskId, $taskId, $monthTime, $progressStatus) {
        $sql = 'select * from tbl_monthunittask where unitTaskId = '.$unitTaskId.' and to_days("'.$monthTime.'") = to_days(monthTime)';
        $result = $this->query($sql);
        if (count($result) > 0) {
            $this->update_where(array('progressStatus' => $progressStatus), array('unitTaskId' => $unitTaskId));
        } else {
            $this->create_id(array('unitTaskId' => $unitTaskId, 'taskId' => $taskId, 'progressStatus' => $progressStatus, 'monthTime' => $monthTime));
        }
    }


    function getTasksMonthStatusSumarry($category = 1) {
        $sql = "select  DATE_FORMAT(monthTime,'%m') as monthXs,  
                        IFNULL(sum(progressStatus = 71 OR NULL), 0) AS normalCount, 
                        IFNULL(count(progressStatus = 72 OR NULL), 0) AS slowCount, 
                        IFNULL(count(progressStatus = 73 OR NULL), 0) AS fastCount, 
                        DATE_FORMAT(monthTime,'%Y%m') as months from tbl_monthunittask 
                where taskId in (select id from tbl_task where category = 1)        
                group by months";
        $arrs = $this->query($sql);
        if (count($arrs) > 0) {
            $arrs = array_column($arrs, null, 'monthXs');
        }
        $monthCounts = array();
        for($n = 1; $n <= 12; $n++) {
            if (array_key_exists($n, $arrs)) {
                $monthCounts['nomal'][] = $arrs[$n]['normalCount'];
                $monthCounts['slow'][] = $arrs[$n]['slowCount'];
                $monthCounts['fast'][] = $arrs[$n]['fastCount'];
            } else {
                $monthCounts['nomal'][] = 0;
                $monthCounts['slow'][] = 0;
                $monthCounts['fast'][] = 0;
            }
        }
        return $monthCounts;
    }


    function getTasksMonthCountSummary() {
        $monthCounts = array();
        $sql = "select count(DISTINCT unitTaskId) as num from tbl_trace WHERE DATE_FORMAT(createtime,'%Y%m') = ;";
        for($n = 1; $n <= 12; $n++) {
            $time = $n < 10 ? '0'.$n : $n;
            $monthCount = $this->queryRow($sql.$time);
            $monthCounts[] = $monthCount['num'];
        }
        return array('monthCounts' => $monthCounts);
    }

}
?>