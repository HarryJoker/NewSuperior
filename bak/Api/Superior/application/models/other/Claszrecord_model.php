<?php

class Claszrecord_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/
     

    public function getRecordsBy($studentId = 0)
    {/*{{{*/
        $records = $this->get_where_with_order(array('studentId' => $studentId));
        foreach ($records as &$record) {
        	$labelsStr = $record['labels'];
            $record['labels'] = strlen($labelsStr) ? explode(",", $labelsStr) : array(); 
        }
        return $records;
    }/*}}}*/

    public function getStudentLabels($studentId = 0)
    {
        $records = $this->select_where(array('studentId' => $studentId, 'closure' => 2));
        $labelsStr = '';
        foreach ($records as $record) $labelsStr .= ','. $record['labels'];
        $labelsStr = ltrim($labelsStr, ',');
        if (!strlen($labelsStr)) return array();
        $labels = array_flip(array_count_values(explode(',', $labelsStr)));
        krsort($labels);
        $labels = array_values($labels);
        $labels = array_splice($labels, 0, count($labels) > 10 ? 10 : count($labels));
        return $labels;
    }

    public function getStudentScores($studentId = 0)
    {
        $records = $this->select_where(array('studentId' => $studentId, 'closure' => 2));
        $scores = array();
        $claszIndexs = array();
        $index = 1;
        foreach ($records as $record) {
            $scores[] = $record['score'];
            $claszIndexs[] = '' . $index++;
        }
        return array('scores' => $scores, 'claszIndexs' => $claszIndexs);
    }

    //老师上课数量
    public function getClaszRecordCount($teacherId = 0)
    {
        $count = $this->result_count(array('teacherId' => $teacherId));
        return $count;
    }

    // //老师当日上课数量
    // public function getDayCount($teacherId = 0)
    // {
    //     $count = $this->result_count(array('teacherId' => $teacherId));
    //     return $count;
    // }

    //老师当周上课数量
    public function getDayCount($openId = 0)
    {
        // select * from tbl_claszrecord where left(claszTime, 10) = '1987-06-26';
        $count = $this->result_count(array('openId' => $openId, 'left(claszTime, 10)' => date("Y-m-d")));
        return $count;
    }

    // //老师当月上课数量
    // public function getDayCount($teacherId = 0)
    // {
    //     $count = $this->result_count(array('teacherId' => $teacherId));
    //     return $count;
    // }

    //老师学员数
    public function getStudentCount($teacherId = 0)
    {
        $count = $this->distinct_result_count(array('studentId'), array('teacherId' => $teacherId));
        return $count;
    }

}
?>