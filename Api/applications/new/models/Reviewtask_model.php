<?php

class Reviewtask_model extends CA_Model {
// 0:未领取（发布任务后默认生成一条占位trace的状态）
// 1:已领取 （领取任务）
// 2:已逾期
// 3:系统催报
// 4:督查催报
// 5:领导批示
// 6:已报送（报送完成/未审核）
// 7:退回重报
// 8:审核正常
// 9:审核缓慢
// 10:审核较快
// 11:上报领导
// 12:调度完成


    private $statusMeta = array('0' => 'unAcceptCount',
                                '1' => 'acceptCount',
                                '2' => 'overDueCount',
                                '3' => 'sysUrgeCount',
                                '4' => 'officeUrgeCount',
                                '5' => 'leaderCommentCount',
                                '6' => 'submitCount',
                                '7' => 'backCount',
                                '8' => 'normalCount',
                                '9' => 'slowCount',
                                '10' => 'fastCount',
                                '11' => 'reportCount',
                                '12' => 'finishCount',);




     //默认每个部门任务的最后一条trace
     private $traceSql = '(select a.* from tbl_reviewtrace a where id = (select max(id) from tbl_reviewtrace where reviewTaskId = a.reviewTaskId) %s order by a.id) as reviewtrace';


     private $joinSql = 'left join tbl_childtask childtask on task.id = childtask.taskId 
                         left join tbl_reviewtask reviewtask on childtask.id = reviewtask.ctaskId
                         left join %s on reviewtask.id = reviewtrace.reviewTaskId';

     //%s:字段， %s：join表关联，%s：全局where
     private $selectSql = 'select %s from tbl_task task %s %s';


     function __construct ()
     {/*{{{*/
         parent::__construct();

     }/*}}}*/


     private function makeQuerySql($traceWhere = array(), $globeWhere = array(), $isDebug = false) {
        $fileds = $this->makeFields();
        // var_dump('All Fileds:'.$fileds);

        //有附属查询条件加（and）
        $where = $this->makeWhereSql($traceWhere);
        $sqlTrace = sprintf($this->traceSql, (strlen($where) > 0 ? ' and '.$where : ''));
        // var_dump('TraceSql: '.$sqlTrace);
        

        $sqlJoin = sprintf($this->joinSql, $sqlTrace);
        // var_dump('JoinSql: '.$sqlJoin);

        //全局查询条件
        $where = $this->makeWhereSql($globeWhere);
        $sqlSelect = sprintf($this->selectSql, $fileds, $sqlJoin, (strlen($where) > 0 ? ' where '.$where : ''));
        if ($isDebug){
            var_dump($sqlSelect);
        }
        return $sqlSelect;
     }


     //从表里拿出字段名
     private function makeFields() {
        $fielsSql = '';
        $tableAlises = array('task', 'reviewtrace', 'reviewtask', 'childtask');
        foreach ($tableAlises as $tableAlis) {
            $sql = 'select COLUMN_NAME from information_schema.COLUMNS where table_name = "tbl_'.$tableAlis.'" and table_schema ="avatar_gov_new"';
            $fields = $this->query($sql);
            // var_dump($tableAlis.': '.json_encode($fields));
            foreach ($fields as $field) {
                //逗号
                $fielsSql.= strlen($fielsSql) ? ', ' : '';
                //字段
                if ($field['COLUMN_NAME'] == 'id') {
                    //IFNUll 
                    $fielsSql.= 'IFNUll('.$tableAlis.'.'.$field['COLUMN_NAME'].', 0)';
                } else {
                    if ($tableAlis == 'reviewtrace' && $field['COLUMN_NAME'] == 'status') {
                        $fielsSql.= 'IFNUll('.$tableAlis.'.'.$field['COLUMN_NAME'].', 0)';
                    } else {
                        $fielsSql.= $tableAlis.'.'.$field['COLUMN_NAME'];
                    }
                }
                //别名
                $fielsSql.= ' as "'.$tableAlis.'.'.$field['COLUMN_NAME'].'"';
            }
        }
        return $fielsSql;
     }

     //表里字段名与where组合
     private function makeWhereSql($where = array()) {
        $whereSql = '';
        if (empty($where)) return $whereSql;
        foreach ($where as $key => $value) {
            // $symbol = (strpos($key, '>') === false && strpos($key, '<') === false) ? ' = ' : '';
            $whereSql.= (strlen($whereSql) > 0 ? ' and ' : '');
            $whereSql.= $key . $value;
        }

        // var_dump($whereSql);
        return $whereSql;
     }


     //所有字段转换为数组 [{task={},childTask={},unitTask={}, trace={}},........]
     private function splitToArray($rows = array()) {
        $tasks = array();
        foreach ($rows as $row) {
            $task = array();
            $cTask = array();
            $trace = array();
            $unittask = array();
            foreach ($row as $key => $value) {
                if (strpos($key, 'task.') === 0) {
                    $task[str_replace('task.', '', $key)] = $value;
                }

                if (strpos($key, 'reviewtrace.') === 0) {
                    $trace[str_replace('reviewtrace.', '', $key)] = $value;
                }

                if (strpos($key, 'childtask.') === 0) {
                    $cTask[str_replace('childtask.', '', $key)] = $value;
                }

                if (strpos($key, 'reviewtask.') === 0) {
                    $unittask[str_replace('reviewtask.', '', $key)] = $value;
                }
                $unittask['reviewtrace'] = $trace;
            }
            $tasks[] = array('task' => $task, 'cTask' => $cTask, 'reviewtask' => $unittask);
        }
        return $tasks;
     }


     private function makeTasks($rows = array()) {
        $rows = $this->splitToArray($rows);
        // return $rows;
        $childTaskGroups = $this->makeChildTaskGroup($rows);
        // return $childTaskGroups;
        $tmpTaskGroups = array();
        foreach ($childTaskGroups as $group) {
            $tmpTaskGroups[$group['task']['id']][] = $group;
        }
    
        $tasks = array();
        foreach (array_values($tmpTaskGroups) as $group) {
            $task = $group[0]['task'];
            $task['childTasks'] = $this->extractChildTasks($group);
            $tasks[] = $task;
        }
        return $tasks;
     }

     //通过childtask的id进行分组[{task={},childTask={.....unitTasks=[]}},........]
     private function makeChildTaskGroup($rows = array()) {
        $tmpChildTaskGroup = array();
        foreach ($rows as $row) {
            $tmpChildTaskGroup[$row['cTask']['id']][] = $row;
        }

        $childTaskGroups = array();
        foreach (array_values($tmpChildTaskGroup) as $value) {
            $cTask = $value[0]['cTask'];
            $cTask['unittasks'] = $this->extractUnitTasks($value);
            $childTaskGroups[] = array('task' => $value[0]['task'], 'cTask' => $cTask);
        }
        return $childTaskGroups;
     }

     //提取childTask数组
     private function extractChildTasks($rows = array()) {
        $childTasks = array();
        foreach ($rows as $row) {
            $childTasks[] = $row['cTask'];
        }
        return $childTasks;
     }
     //提取unittasks
     private function extractUnitTasks($rows = array()) {
        $unitTasks = array();
        foreach ($rows as $row) {
            $unitTasks[] = $row['reviewtask'];
        }
        return $unitTasks;
     }




    // function deleteTask($taskId = 0) {
    //     $this->load->model('childtask_model');
    //     $this->load->model('unittask_model');
    //     $this->load->model('trace_model');
    //     //开启事务
    //     $this->db->trans_start();
    //     $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
    //     $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
    //     //.....
    //     // unittak , trace
    //     //提交事务
    //     $complete = $this->db->trans_complete();
    // }

    // function deleteChildTask($childTaskId = 0) {
    //     $this->load->model('childtask_model');
    //     $this->load->model('unittask_model');
    //     $this->load->model('trace_model');
    //     //开启事务
    //     $this->db->trans_start();
    //     $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
    //     $this->childtask_model->update_where(array('valid' => 0), array('taskId' => $taskId));
    //     //.....
    //     // unittak , trace
    //     //提交事务
    //     $complete = $this->db->trans_complete();
    // }

    // function deleteTaskById($taskId = 0) {
    //     $this->load->model('childtask_model');
    //     $childTaskIds = $this->childtask_model->select_where_in(array('id'), 'taskId', array($taskId));
    //     $childTasks = $this->deleteChildTasksByIds($childTaskIds);
    //     $task = $this->delete(array('id' => $taskId));
    //     return array('task' => $task, 'childs' => $childTasks);
    // } 

    // function deleteChildTasksByIds($childTaskIds = array()) {
    //     $this->load->model('unittask_model');
    //     $this->load->model('childtask_model');
    //     $unitTaskIds = $this->unittask_model->select_where_in(array('id'), 'ctaskId', $childTaskIds);
    //     $unitTasks = $this->deleteUnitTasksByIds($unitTaskIds);
    //     $childs = $this->childtask_model->deleteIn('id', array('id' => $childTaskIds));
    //     return array('$childs' => $childs, 'units' => $unitTasks);
    // }

    // function deleteUnitTasksByIds($unitTaskIds = array()) {
    //     $this->load->model('trace_model');
    //     $this->load->model('unittask_model');
    //     $traceIds = $this->trace_model->select_where_in(array('id'), 'unitTaskId', $unitTaskIds);
    //     $traces = $this->deleteTracesByIds($unitTaskIds);
    //     $units = $this->unittask_model->deleteIn('id', array('id' => $unitTaskIds));
    //     return array('units' => $units, 'traces' => $traces);
    // }

    // function deleteTracesByIds($traceIds = array()) {
    //     $this->load->model('trace_model');
    //     $this->trace_model->deleteIn('id', $traceIds);
    //     return $traceIds;
    // }

    //  //获取单个任务详情（主）
    //  function getTask($taskId) {
    //     $sql = $this->makeQuerySql(array(), array('task.id = ' => $taskId));
    //     $rows = $this->query($sql);
    //     $tasks = $this->makeTasks($rows);
    //     return $tasks;
    //  }


    //  //获取单个任务详情（子任务）
    //  function getChildTask($cTaskId) {
    //     $sql = $this->makeQuerySql(array(), array('childTask.id = ' => $cTaskId));
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    //  }

    //  //获取单位所有任务
    //  function getUnitAllTasks($unitId = 0, $category = 0) {
    //     $where = array('task.category = ' => $category, 'unittask.unitId = ' => $unitId);
    //     $sql = $this->makeQuerySql(array(), $where);
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    //  }

     //获取单位内部审核的所有任务
     function getUnitReviewTasks($unitId = 0, $category = 0) {
        $where = array('reviewtask.category = ' => $category, 'reviewtask.unitId = ' => $unitId);
        $sql = $this->makeQuerySql(array(), $where);
        $tasks = $this->makeTasks($this->query($sql));
        return $tasks;
     }

     function createReviewTask($data = array()) {
        if (!empty($data)) {
            $id = $this->create_id($data);
            return $id;
        }

        return 0;
     }


     //获取部门任务（分已领取，及未领取）
    //  function getTasksByUnitForAccept($unitId = 0, $category = 0) {
    //  	$unAcceptTasks = $this->getTasksByUnitWithAccept($unitId, $category, 0);
    //     $acceptedTasks = $this->getTasksByUnitWithAccept($unitId, $category, 1);
    //     return array('unAcceptTasks' => $unAcceptTasks, 'acceptedTasks' => $acceptedTasks);
    //  }

    //  //获取是否领取的任务
    //  function getTasksByUnitWithAccept($unitId = 0, $category = 0, $accept = 0) {
    //     $where = array('task.category = ' => $category, 'unittask.unitId = ' => $unitId);
    //     if ($accept == 0) {
    //         $where['trace.status ='] = 0;
    //     } else {
    //         $where['trace.status >'] = 0;
    //     }  	
    //     $sql = $this->makeQuerySql(array(), $where);
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    //  }


    //  //获取是否审核的任务
    //  function getTasksForVerify($verify = 0, $category = 0) {
    //     $whereTrace = $verify == 0 ? array('status =' => 6) : array('status > ' => 6);
    //     $sql = $this->makeQuerySql($whereTrace, array('trace.status !=' => 0));
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    // }

    // //获取上报给领导部门的任务
    // function getReportLeaderUnitTasks($leaderUnitId = 0, $category = 0) {
    //     $reportUnitTaskIds = '(select unitTaskId from tbl_reporttask where leaderUnitId = '.$leaderUnitId.')';
    //     $where = array('task.category = ' => $category, 'unittask.id in ' => $reportUnitTaskIds);
    //     $sql = $this->makeQuerySql(array(), $where);
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    // }

    // private function getDefaultTraceSummary() {
    //     return array_flip($this->statusMeta);
    // }

    // private function getDefaultTaskSummary() {
    //     return array('totalCount' => 0, 'doingCount' => 0, 'finishCount' => 0, 'reportCount' => 0);
    // }

    // //获取领导部门工作汇总信息
    // function getLeadUnitSummaryInfo($unitId = 0, $category = 0) {
    //     $taskSummary = $this->getLeaderUnitTaskSummaryInfo($unitId, $category);
    //     $traceSummary = $this->getLeaderUnitTraceSummaryInfo($unitId, $category);
    //     $reportSummary = $this->getLeaderUnitReportSummaryInfo($unitId, $category);
    //     return array('traceSummary' => $traceSummary, 'taskSummary' => $taskSummary, 'reportSummary' => $reportSummary);
    // }

    // //获取领导部门上报的数据
    // private function getLeaderUnitReportSummaryInfo($unitId, $category) {
    //     $sql = 'select count(id) as reportTaskCount from tbl_reporttask where leaderUnitId='.$unitId.' and category = '.$category;
    //     $result =  $this->query($sql);
    //     $reportSummary = count($result) == 1 ? $result[0] : array('reportTaskCount' => 0);
    //     return $reportSummary;
    // }

    // //获取领导部门的task的汇总
    // private function getLeaderUnitTaskSummaryInfo($unitId, $category) {
    //     $sql = 'SELECT taskStatus, count(id) AS total FROM tbl_task where leaderUnitId = '.$unitId.' and category = '.$category.' GROUP BY taskStatus ORDER BY taskStatus';

    //     $taskSummary = $this->getDefaultTaskSummary();
    //     $result = $this->query($sql);
    //     foreach ($result as $value) {
    //         if ($value['taskStatus'] == 0) {
    //             $taskSummary['doingCount'] = $value['total'];
    //         }
    //         if ($value['taskStatus'] == 1) {
    //             $taskSummary['finishCount'] = $value['total'];
    //         }
    //     }
    //     $taskSummary['totalCount'] = $taskSummary['doingCount'] + $taskSummary['finishCount'];
    //     return $taskSummary;
    // }

    // //获取领导部门的trace的汇总
    // private function getLeaderUnitTraceSummaryInfo($unitId = 0, $category = 0) {
    //     $sqlTaskIds      = 'select id from tbl_task where leaderUnitId = '.$unitId.' and category = '.$category;
    //     $sqlChildTaskIds = 'select id from tbl_childtask where taskId in ('.$sqlTaskIds.')';
    //     $sqlUnitTaskIds  = 'select id from tbl_unittask where ctaskId in ('.$sqlChildTaskIds.')';
    //     $sqlTraces       = 'select status, count(id) AS total FROM tbl_trace where unitTaskId in ('.$sqlUnitTaskIds.') GROUP BY status ORDER BY status';
    //     $traceSummary = $this->getDefaultTraceSummary();
    //     $result = $this->query($sqlTraces);
    //     foreach ($result as $value) {
    //         $key = $this->statusMeta[$value['status']];
    //         $traceSummary[$key] = $value['total'];
    //     }
    //     return $traceSummary;
    // }

    // //获取leaderUnit分管工作
    // function getLeaderUnitTasks($leaderUnitId = 0, $category = 0) {
    //     $where = array('task.category = ' => $category, 'task.leaderUnitId = ' => $leaderUnitId);
    //     $sql = $this->makeQuerySql(array(), $where);
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    // }


    // //搜索任务
    // function getTasksByKeyWord($keyWord = '', $unitId = 0, $role = 0) {
    //     $keyWord = $this->input->post('keyWord');
    //     $like = ' like "%'.$keyWord.'%" or childtask.name like "%'.$keyWord.'%"';
    //     $where = array('task.name' => $like);
    //     $sql = $this->makeQuerySql(array(), $where);
    //     $tasks = $this->makeTasks($this->query($sql));
    //     return $tasks;
    // }


}
?>