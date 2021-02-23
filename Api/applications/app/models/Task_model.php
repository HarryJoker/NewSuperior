<?php

class Task_model extends CA_Model {

     private $units = array();

     private $tables = array(array('name' => 'tbl_task', 'prefix' => 'task.', 'alis' => 't'), 
                             // array('name' => 'tbl_childtask', 'prefix' => 'child.','alis' => 'c'),
                             // array('name' => 'tbl_trace', 'prefix' => 'trace.','alis' => "e"),
                              array('name' => 'tbl_unittask', 'prefix' => 'dask.','alis' => 'ut')
                              // array('name' => 'tbl_unit', 'prefix' => 'unit.','alis' => 'u')
                         );

     function __construct ()
     {/*{{{*/
          parent::__construct();

          $this->units = $this->loadUnits();

     }/*}}}*/



     //生成别名字段
     private function makeFields() {
          foreach ($this->tables as $table) {
               $result = $this->query("select column_name from information_schema.columns 
                    where table_name='".$table['name']."' and table_schema='avatar_gov_2'");
               foreach ($result as $val) {
                    $fileds[] = $table['alis'].'.'.$val['column_name'] . ' as `' . $table['prefix'].$val['column_name'].'` ';
               }
          }

          return implode(',', $fileds);
     }

     private function makeRows2Task($rows = array()) {
          $tasks = array();
          foreach ($rows as $row) {
               $task = array();
               $unitTask = array();
               foreach ($row as $key => $value) {

                    if (strpos($key, "task.") !== false) {
                         $task[str_replace('task.', '', $key)] = $value;
                    }

                    // if (strpos($key, 'child.') !== false) {
                    //      $childtask[str_replace('child.', '', $key)] = $value;
                    // }
               
                    // if (strpos($key, 'trace.') !== false) {
                    //      $unittask[str_replace('trace.', '', $key)] = $value;
                    // }

                    // if (strpos($key, 'dask.childTaskId') !== false) {
                    //      $unittask['childTaskId'] = $value;
                    // }

                    // if (strpos($key, 'dask.progress') !== false) {
                    //      $unittask['progress'] = $value;
                    // }
                    if (strpos($key, 'dask.') !== false) {
                         $unitTask[str_replace('dask.', '', $key)] = $value;
                    }

                    // if (strpos($key, 'unit.') !== false) {
                    //      $unit[str_replace('unit.', '', $key)] = $value;
                    // }

               }
               // $unittask['unit'] = $unit;
               // $unittasks[] = array('task' => $task, 'childTask' => $childtask, 'unitTask' => $unittask);
               $task['unitTask'] = $unitTask;
               $tasks[] = $task;
          }
          return $tasks;
     }

     
     private function loadUnits() {
          $this->load->model('unit_model');
          $units = $this->unit_model->getAllUnits();
          $units = array_column($units, null, "id");
          return $units;
     }


     //获取任务列表
     function getTaskList($category = 0, $unitId = 0, $parentUnitId = 0, $reportLeaderUnitId = 0, $extrWhereSql = '') {

          $sql = "select ".$this->makeFields().", un.name as `dask.unitName`, un.parentid as `dask.unitParentId` from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_unit un on ut.unitId = un.id where ";
          //正常category
          if ($category > 0) {
               $sql .= " t.category = ".$category;
          } else {
               //否则全部的category
               $sql .= " t.category > 0";
          }

          if ($unitId) {
               $sql .= ' and ut.unitId = '. $unitId;
          }

          //分管列表
          if ($parentUnitId) {
               $sql .= ' and ut.unitId in (select id from tbl_unit where parentId = '.$parentUnitId.')';
          }


          //上报任务列表
          if ($reportLeaderUnitId) {
               $sql .= ' and ut.id in (select unitTaskId from tbl_reporttask where unitId = '.$reportLeaderUnitId.')';
          }

          if ($extrWhereSql) {
               $sql .= $extrWhereSql;
          }

          if ($category == 1) {
               $sql .= ' order by t.taskSerial';
          }

          $tasks = $this->query($sql);
          return $this->makeGroupUnitTasks($tasks);
     }

     public function deleteTasks($ids = '') {
         $sql = 'delete from tbl_trace where unitTaskId in (select id from tbl_unittask where taskId in ('.$ids.'))';
         $this->excute($sql);
         $sql = 'delete from tbl_unittask where taskId in ('.$ids.')';
         $this->excute($sql);
         $sql = 'delete from tbl_task where id in ('.$ids.')';
         $this->excute($sql);
    }

     //将task的unit任务进行分组
     private function makeGroupUnitTasks($tasks = array()) {
          $tasks = $this->makeRows2Task($tasks);

          $newTasks = array();
          foreach ($tasks as $task) {
               $task['unitTask']['unitParentName'] = $this->makeUnitParentName($task['unitTask']['unitParentId']);

               if (array_key_exists($task['id'], $newTasks) === false) {
                    $newTasks[$task['id']] = $task;
               }
               $newTasks[$task['id']]['unitIds'][] = $task['unitTask']['unitId'];
               $newTasks[$task['id']]['unitTasks'][] = $task['unitTask'];
               unset($newTasks[$task['id']]['unitTask']);
          }
          return $newTasks ? array_values($newTasks) : array();
     }

     private function makeUnitParentName($parentId = 0) {
          if (array_key_exists($parentId, $this->units)) {
               $unit = $this->units[$parentId];
               return $unit['name']; 
          }
          return '';
     }

      //获取群众端民生事项列表（政府工作报告中的民生实事及意见调度：8） 
     function getTaskListForPeople() {
          $sql = ' and (t.category = 8 or t.taskLabel = "民生实事")';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
     }

     //获取未领取工作列表 
     function getUnAcceptTaskListByUnit($category, $unitId) {
          $sql = ' and ut.status = 0';
          $tasks = $this->getTaskList($category, $unitId, 0, 0, $sql);
          return $tasks;
     }

     function getGovermentPublicTaskList($progressStatus) {
          // $sql = 'select t.*, ut.status, un.name as unitName from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_unit un on ut.unitId = un.id where (t.category = 1 or t.category = 11) and ut.status = '.$status;
          $sql = ' and (t.category = 1 or t.category = 11) and ut.progressStatus = '.$progressStatus;
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
     }

     function getPeopleProgressTaskList() {
          $sql = 'select t.*, ut.status, un.name as unitName from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_unit un on ut.unitId = un.id where t.opinionId > 0 and t.category = 9';
          $tasks = $this->query($sql);
          return $tasks;
     }

     //获取某个任务
     function getTask($taskId = 0) {
          $sql = "select ".$this->makeFields().", un.name as `dask.unitName`, un.parentid as `dask.unitParentId` from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_unit un on ut.unitId = un.id where t.id = ".$taskId;
          $tasks = $this->query($sql);
          $tasks = $this->makeGroupUnitTasks($tasks);
          return count($tasks) == 1 ? $tasks[0] : array();
     }


     //获取部门某个任务
     function getUnitTask($unitTaskId = 0) {
          $sql = "select ".$this->makeFields().", un.name as `dask.unitName`, un.parentid as `dask.unitParentId` from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_unit un on ut.unitId = un.id where ut.id = ".$unitTaskId;
          $tasks = $this->query($sql);
          $tasks = $this->makeGroupUnitTasks($tasks);
          return count($tasks) == 1 ? $tasks[0] : array();
     }

     //获取部门内部待完善或待审核任务列表
     function getStayDealTaskList($category, $unitId, $status = 0) {
          // $sql = 'select t.* from tbl_task t left join tbl_unittask ut on t.id = ut.taskId left join tbl_trace e on ut.id = e.unitTaskId where ut.unitId = '. $unitId . ' and t.category = '. $category. ' and e.status = '.$status;
          $extraSql = ' and ut.id in (select unitTaskId from tbl_trace where status = '.$status.')';

          $tasks = $this->getTaskList($category, $unitId, 0, 0, $extraSql);
          return $tasks ? $tasks : array();
     }


     //筛选任务列表
     function getTaskListByFilter($category = 0, $leaderUnitId = 0, $status = 0) {
          $extraSql = 0;
          if (strlen(($status.'')) > 0) {
               $extraSql = ' and ut.id in (select unitTaskId from (select max(id), unitTaskId from tbl_trace where status = '.$status.' group by unitTaskId) tmp)';
          }
          $tasks = $this->getTaskList($category, 0, $leaderUnitId, 0, $extraSql);
          return $tasks;
     }


     //我评价的任务
     function getMyCommentTaskList($userId = 0) {
          $sql = ' and ut.id in (select unitTaskId from tbl_comment where userId = '.$userId.')';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
     }


     //我的线索任务
    function getMyClueTaskList($userId = 0) {
          $sql = ' and ut.id in (select unitTaskId from tbl_clue where userId = '.$userId.')';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
    }


    //我的意见任务
    function getMyOpinionTaskList($userId = 0) {
          $sql = ' and ut.id in (select unitTaskId from tbl_opinion where userId = '.$userId.')';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
    }


     //我的关注的事项
     function getMyFavoriteTaskList($userId = 0) {
          $sql = ' and ut.id in (select unitTaskId from tbl_favorite where userId = '.$userId.')';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
     }


    //领导批注的事项
    function getMyNotationTaskList($unitId = 0) {
          $sql = ' and ut.id in (select unitTaskId from tbl_trace where status = 21 and unitId = '.$unitId.' group by unitTaskId)';
          $tasks = $this->getTaskList(0, 0, 0, 0, $sql);
          return $tasks;
    }




     function doneTask($taskId = 0) {
     	$rows = $this->update_where(array('done' => 1), array('id' => $taskId));
     	return $rows;
     }

     
     //生成别名字段
     // private function makeFields() {
     //      foreach ($this->tables as $table) {
     //           $result = $this->query("select column_name from information_schema.columns 
     //                where table_name='".$table['name']."' and table_schema='avatar_gov_2'");
     //           foreach ($result as $val) {
     //                $fileds[] = $table['alis'].'.'.$val['column_name'] . ' as `' . $table['prefix'].$val['column_name'].'` ';
     //           }
     //      }

     //      return implode(',', $fileds);
     // }
          

     private function getTaskListByWhere($where = '') {
          if ($where) {
               $sql = 'select '.$this->makeFields()
               .' from ' . $this->joinSql.' where '
               . $where.' order by e.id desc limit 100000';



               $distinctSql = 'select * from (' . $sql . ') as tmp group by `trace.unitTaskId`';

               // var_dump($distinctSql);

               return $this->query($distinctSql); 
          }
          return array();
     }


     // private function makeRows2Task($rows = array()) {
     //      $unittasks = array();
     //      foreach ($rows as $row) {
     //           $task = array();
     //           $childtask = array();
     //           $unittask = array();
     //           foreach ($row as $key => $value) {

     //                if (strpos($key, "task.") !== false) {
     //                     $task[str_replace('task.', '', $key)] = $value;
     //                }

     //                if (strpos($key, 'child.') !== false) {
     //                     $childtask[str_replace('child.', '', $key)] = $value;
     //                }
               
     //                if (strpos($key, 'trace.') !== false) {
     //                     $unittask[str_replace('trace.', '', $key)] = $value;
     //                }

     //                if (strpos($key, 'dask.childTaskId') !== false) {
     //                     $unittask['childTaskId'] = $value;
     //                }

     //                if (strpos($key, 'dask.progress') !== false) {
     //                     $unittask['progress'] = $value;
     //                }

     //                if (strpos($key, 'unit.') !== false) {
     //                     $unit[str_replace('unit.', '', $key)] = $value;
     //                }

     //           }
     //           $unittask['unit'] = $unit;
     //           $unittasks[] = array('task' => $task, 'childTask' => $childtask, 'unitTask' => $unittask);
     //      }
     //      return $unittasks;
     // }


    


      //获取部门内部审核或待修改的任务
     function getContentUnitTaskListByStatus($category = 0, $unitId = 0, $contentStatus = -1) {
          $unitTaskIdsSql = 'select unitTaskId from tbl_unitcontent where status = '.$contentStatus;
          $where = 't.category = '.$category.' and ut.unitId = '.$unitId.' and ut.id in ('.$unitTaskIdsSql.')';
          $rows = $this->getTaskListByWhere($where);
          $unittasks = $this->makeRows2Task($rows);
          return $unittasks;
     }

     //获取部门任务列表
     // function getTaskListByCategory($category = 0) {
     //      $where = 't.category = '.$category;
     //      $rows = $this->getTaskListByWhere($where);
     //      $unittasks = $this->makeRows2Task($rows);
     //      return $unittasks;
     // }


     //获取部门任务列表
     function getTaskListByVerify($category = 0, $verify) {
          if ($verify) {
              $where = 't.category = '.$category. ' and e.status >= 71'; 
          } else {
               $where = 't.category = '.$category. ' and e.status < 71';
          } 
          $rows = $this->getTaskListByWhere($where);
          $unittasks = $this->makeRows2Task($rows);
          return $unittasks;
     }


     //部门Task汇总信息
     function getUnitTaskSummary($category, $unitId) {
          $sql = 'select count(ut.progressStatus = 71 or null) as nomalCount,
                              count(ut.progressStatus = 72 or null) as slowCount, 
                              count(ut.progressStatus = 73 or null) as fastCount,
                              count(ut.progressStatus = 91 or null) as doneCount, 
                              count(ut.progressStatus >= 0 or null) as taskCount 
                         from tbl_unittask ut left join tbl_task t on ut.taskId = t.id where t.category = '.$category.' and ut.unitId = '.$unitId;
          $result = $this->queryRow($sql);
          return $result;
     }


     //部门StayTask汇总信息(待报送及待审核)
     function getUnitStayTaskSummary($category, $unitId) {
          $sql = 'select count(tc.status = -1 or null) as staySignCount,
                         count(tc.status = -2 or null) as stayCompleteCount 
                         from tbl_trace tc left join tbl_unittask ut on tc.unitTaskId = ut.id left join tbl_task t on ut.taskId = t.id where tc.status < 0 and t.category = '.$category.' and ut.unitId = '.$unitId;
          $result = $this->queryRow($sql);
          return $result;
     }

     //部门未领取数量
     function getUnAcceptTaskSummary($category, $unitId) {
          $sql = 'select ifnull(count(ut.id), 0) as unAcceptCount from tbl_unittask ut left join tbl_task t on t.id = ut.taskId where t.category = '.$category.' and ut.unitId = '.$unitId.' and ut.status = 0';
          $result = $this->queryRow($sql);
          return $result;
     }


      //部门Trace汇总信息
     function getUnitTraceSummary($category, $unitId) {
          $sql = 'select count(tc.status = 50 or null) as overdueCount,
                              count(tc.status = 74 or null) as backCount,
                              count(tc.status = 73 or null) as fastCount, 
                              count(tc.status = 72 or null) as slowCount
                         from tbl_trace tc left join tbl_unittask ut on tc.unitTaskId = ut.id left join tbl_task t on ut.taskId = t.id where t.category = '.$category.' and ut.unitId = '.$unitId;
          $result = $this->queryRow($sql);
          return $result;
     }


     //获取报送领导任务列表
     function getReportTaskListByLeaderUnit($category = 0, $unitId = 0) {
          $tasks = $this->getTaskList($category, 0, 0, $unitId);
          return $tasks;
     }

     //获取县长分管工作
     function getTaskListByLeaderUnit($category = 0, $leaderUnitId = 0) {
          $tasks = $this->getTaskList($category, 0, $leaderUnitId, 0);
          return $tasks;
     }


     //获取领导分管工作的汇总信息
     private function getLeaderTaskSummary($category = 0, $leaderUnitId = 0) {
          $unitIds = ' (select id from tbl_unit where parentId = '.$leaderUnitId.')';
          $statusSql = ' (select ut.id, ut.status, ut.unitId from tbl_unittask ut left join tbl_task t on t.id = ut.taskId where ut.unitId in '.$unitIds.' and t.category = '.$category.')';
          $sql = 'select count(status = 71 or status = 0 or null) as nomalCount,
                              count(status = 72 or null) as slowCount, 
                              count(status = 73 or null) as fastCount,
                              count(status = 91 or null) as doneCount, 
                              count(status >= 0 or null) as taskCount 
                         from '.$statusSql.' as tmp';
          $result = $this->queryRow($sql);
          return $result;
     }

     //领导分管的Trace汇总
     private function getLeaderTraceSummary($category, $leaderUnitId) {
          $unitIds = ' (select id from tbl_unit where parentId = '.$leaderUnitId.')';
          $statusSql = 'select e.status, ut.unitId from tbl_trace e left join tbl_unittask ut on ut.id = e.unitTaskId left join tbl_task t on t.id = ut.taskId where ut.unitId in ('.$unitIds.')  and t.category = '.$category;
          $sql = 'select count(status = 50 or null) as overdueCount,
                              count(status = 74 or null) as backCount,
                              count(status = 73 or null) as fastCount, 
                              count(status = 72 or null) as slowCount
                         from ('.$statusSql.') as tmp';
          $result = $this->queryRow($sql);
          return $result;
     }

     //领导其它汇总数据：上报数量
     private function getLeaderReportSummary($category, $leaderUnitId) {
          $taskIds = '(select unitTaskId from tbl_reporttask where unitId = '.$leaderUnitId.')';
          $sql = 'select count(id) as reportCount from tbl_task where category = '.$category.' and id in '.$taskIds;
          $result = $this->queryRow($sql);
          return $result;
     }


     //领导分管工作汇总数据
     private function getLeaderPrimarySummary($category, $leaderUnitId) {
          $taskSummary = $this->getLeaderTaskSummary($category, $leaderUnitId);
          $traceSummary = $this->getLeaderTraceSummary($category, $leaderUnitId);
          return array(
            'taskSummary' => $taskSummary, 
            'traceSummary' => $traceSummary, 
            'unit' => $this->units[$leaderUnitId]);
     }


     //获取领导分管工作的当月任务汇总信息
     private function getLeaderMonthTaskSummary($category = 0, $leaderUnitId = 0, $month) {

          $timeSql = ' month(e.taskDate) = '.$month.' and year(e.taskDate) = YEAR(NOW()) ';

          $unitIds = ' (select id from tbl_unit where parentId = '.$leaderUnitId.')';
          $statusSql = 'select max(e.id), e.unitTaskId, e.status from tbl_trace e left join tbl_unittask ut on e.unitTaskId = ut.id left join tbl_task t on ut.taskId = t.id where t.category = '.$category.' and ut.unitId in '.$unitIds.' and '.$timeSql.' and (e.status = 0 or e.status = 71 or  e.status = 72 or e.status = 73 or  e.status = 91) group by e.unitTaskId';

          $sql = 'select count(status = 71 or null) as nomalCount,
                              count(status = 72 or null) as slowCount, 
                              count(status = 73 or null) as fastCount,
                              count(status = 91 or null) as doneCount, 
                              count(status >= 0 or null) as taskCount 
                         from ('.$statusSql.') as tmp';
          $result = $this->queryRow($sql);
          return $result;
     }

     //领导分管的当月Trace汇总
     private function getLeaderMonthTraceSummary($category, $leaderUnitId, $month) {
          $timeSql = ' month(e.taskDate) = '.$month.' and year(e.taskDate) = YEAR(NOW()) ';
          $unitIds = ' (select id from tbl_unit where parentId = '.$leaderUnitId.')';

          $statusSql = 'select e.status from tbl_trace e left join tbl_unittask ut on e.unitTaskId = ut.id left join tbl_task t on ut.taskId = t.id  where t.category = '.$category.' and ut.unitId in '.$unitIds.' and '.$timeSql.' and e.status >= 0';

          $sql = 'select count(status = 50 or null) as overdueCount,
                              count(status = 74 or null) as backCount,
                              count(status = 73 or null) as fastCount, 
                              count(status = 72 or null) as slowCount
                         from ('.$statusSql.') as tmp';
          $result = $this->queryRow($sql);
          return $result;
     }

     //领导分管工作的月汇总数据
     function getLeaderMonthSummary($category, $leaderUnitId, $month) {
          $taskSummary = $this->getLeaderMonthTaskSummary($category, $leaderUnitId, $month);
          $traceSummary = $this->getLeaderMonthTraceSummary($category, $leaderUnitId, $month);
          
          return array(
            'monthTaskSummary' => $taskSummary, 
            'monthTraceSummary' => $traceSummary,);
     }


     //获取领取分管工作事项数量汇总(分管事件数，上报的事件数)
     function getLeaderTaskTotalNumSummary($category, $leaderUnitId) {
          $taskSummary = $this->getLeaderTaskSummary($category, $leaderUnitId);
          $reportSummary = $this->getLeaderReportSummary($category, $leaderUnitId);
          $summarys = array('taskTotalNumSummary' => $taskSummary, 'taskReportNumSummary' => $reportSummary);
          return $summarys;
     }

     //分管工作和月工作汇总数据
     function getLeaderSummary($category, $leaderUnitId, $month) {
          $primarySummary = $this->getLeaderPrimarySummary($category, $leaderUnitId);
          $monthSummary = $this->getLeaderMonthSummary($category, $leaderUnitId, $month);
          $totalNUmSummary = $this->getLeaderTaskTotalNumSummary($category, $leaderUnitId);
          $summarys[] = $primarySummary;
          $summarys[] = $monthSummary;
          $summarys[] = $totalNUmSummary;
          return $summarys;
     }


     //所有领导的工作汇总数据
     function getAllLeaderSummary($category, $month) {
          $units = $this->query('select * from tbl_unit where role < 3 order by id');
          $summarys = array();
          foreach ($units as $unit) {
               $taskSummary = $this->getLeaderTaskSummary($category, $unit['id']);
               $traceSummary = $this->getLeaderTraceSummary($category, $unit['id']);
               $leaderSummary = array(
                                   'taskSummary' => $taskSummary, 
                                   'traceSummary' => $traceSummary, 
                                   'unit' => $unit);
               $summarys[] = $leaderSummary;

               //县长身份
               if ($unit['role'] == 1) {
                    //月报汇总
                    // $monthSummary = $this->getLeaderMonthSummary($category, $unit['id'], $month);
                    // $summarys[] = $monthSummary;

                    //事件数据汇总
                    $taskTotalNumSummary = $this->getLeaderTaskTotalNumSummary($category, $unit['id']);
                    $summarys[] = $taskTotalNumSummary;
               }
          }
          return $summarys;
     }



     //获取领导分管工作月报的任务列表
     function getLeaderUnitMonthTaskList($category = 0, $leaderUnitId = 0, $status = 0, $month = 0) {

          // $sql = 'select taskId from tbl_trace where unitId in (select id from tbl_unit where parentId = '.$leaderUnitId.') and status = '.$status.' and  month(createtime) = '.$month.' and year(createtime) = YEAR(NOW()) group by taskId';
          $sql = ' and ut.id in (select unitTaskId from tbl_trace where unitId in (select id from tbl_unit where parentId = '.$leaderUnitId.') and status = '.$status.' and  month(createtime) = '.$month.' and year(createtime) = YEAR(NOW()) group by unitTaskId)';

          $result = $this->getTaskList($category, 0, 0, 0, $sql);
          return $result;
     }
 


     //获取县长分管工作（根据status和月份）
     function getTaskListByLeaderUnitWithStatusAndMonth($category = 0, $leaderUnitId = 0, $status = 0, $month = 0) {

          $childTaskIdsSql = '(select id from tbl_childtask where leaderUnitId = '.$leaderUnitId.')';
          $where = 't.category = '.$category. ' and c.id in '. $childTaskIdsSql;
          $where .= ' and year(e.createtime) = YEAR(NOW()) and month(e.createtime) = '.$month;
          $where .= ' and e.status = '.$status;

          $result = $this->getTaskListByWhere($where);
          $result = $this->makeRows2Task($result);

          $result = $this->makeRows2ChildTaskList($result); 

          if ($category == 1) {
               $result = $this->makeRows2TreeList($result);
          } else {
               $result = $this->makeRows2ExpandList($result);
          }

          return $result;
     }


     //获取县长分管工作
     function getTaskListByCategory($category = 0) {
          $where = 't.category = '.$category;
          $result = $this->getTaskListByWhere($where);
          $result = $this->makeRows2Task($result);

          $result = $this->makeRows2ChildTaskList($result); 
 
          if ($category == 1) {
               $result = $this->makeRows2TreeList($result);
          } else {
               $result = $this->makeRows2ExpandList($result);
          }
          
          return $result;
     }

     //获取分管的某项任务的UnitTask List
     function getUnitTaskListByChildTask($childTaskId = 0) {
          $where = 'c.id = '.$childTaskId;
          $result = $this->getTaskListByWhere($where);
          $result = $this->makeRows2Task($result);


          $result = $this->makeRows2ChildTaskList($result); 
          
          //将childTask拆分为：task， childTask，unitTasks
          if (count($result) == 1) {
               $result = $result[0];
               $unittasks = $result['childTask']['unitTasks'];
               unset($result['childTask']['unitTasks']);
               $result['unitTasks'] = $unittasks;
               return $result;
          }

          return array();
     }

     private function makeRows2ChildTaskList($unitTasks = array()) {
          $childTasks = array();
          foreach ($unitTasks as $value) {
               if (array_key_exists($value['childTask']['id'], $childTasks)) {
                    $childTasks[$value['childTask']['id']]['childTask']['unitTasks'][] = $value['unitTask'];
               } else {
                    $childTask = array('task' => $value['task'], 'childTask' => $value['childTask']);
                    $childTask['childTask']['unitTasks'][] = $value['unitTask'];
                    $childTasks[$value['childTask']['id']]  = $childTask;
               }
          }
          return array_values($childTasks);
     }

     //转换为三级列表
     private function makeRows2TreeList($childTasks = array()) {
          $tasks = array();
          foreach ($childTasks as $value) {
               if (array_key_exists($value['task']['id'], $tasks)) {
                    $tasks[$value['task']['id']]['childTasks'][] = $value['childTask'];

               } else {
                    $task = $value['task'];
                    $task['childTasks'][] = $value['childTask'];
                    $tasks[$value['task']['id']]  = $task;
               }
          }
          return array_values($tasks);
     }

     //转换为二级列表（childtask为主，携带task）
     private function makeRows2ExpandList($childTasks = array()) {
          $tasks = array();
          foreach ($childTasks as $value) {
               $childTask = $value['childTask'];
               $childTask['task'] = $value['task'];
               $tasks[] = $childTask;
          }
          return $tasks;
     }


     function getUnitContentCountInfo($category = 0, $unitId = 0) {
          $where = 'uc.id >= 0 and t.category = '. $category. ' and ut.unitId = '.$unitId;

          $sql = 'select uc.id, uc.unitTaskId, uc.status from tbl_task t left join tbl_childtask c on t.id = c.taskId left join tbl_unittask ut on c.id = ut.childTaskId left join tbl_unitcontent uc on ut.id = uc.unitTaskId where '.$where;

 
          // $sql = 'select count(status = 0 or null) as modifyCount,
                         // count(status = 1 or null) as verifyCount
                  // from ('.$sql.') as tmp group by status';
          $sql = 'select count(status = 0 or null) as modifyCount,
                         count(status = 1 or null) as verifyCount
                  from ('.$sql.') as tmp';

          $unitContentCount = $this->queryRow($sql);
          return $unitContentCount;
     }


     //单位：多少项，(快，慢，正常，完成)各多少项
     function getUnitTaskCountInfo($category, $unitId) {
          $where = 't.category = '.$category.' and u.unitId = '.$unitId;
          $taskSql = 'select trace.unitTaskId, trace.status from tbl_task t left join tbl_childtask c on t.id = c.taskId left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace trace on u.id = trace.unitTaskId where ' . $where . ' group by unitTaskId';

          $countSql = 'select count(status = 71 or null) as nomalCount,
                              count(status = 72 or null) as slowCount, 
                              count(status = 73 or null) as fastCount,
                              count(status = 91 or null) as doneCount, 
                              count(status >= 0 or null) as taskCount 
                         from ('.$taskSql.') as tmp';

          $taskCount = $this->queryRow($countSql);
          return $taskCount;
     }


     //单位：trace状态汇总数据
     function getUnitTraceCountInfo($category, $unitId) {
          $where = 't.category = '.$category.' and u.unitId = '.$unitId;
          $taskSql = 'select trace.unitTaskId, trace.status from tbl_task t left join tbl_childtask c on t.id = c.taskId left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace trace on u.id = trace.unitTaskId where ' . $where;

          $countSql = 'select count(status = 50 or null) as overdueCount,
                              count(status = 74 or null) as backCount,
                              count(status = 73 or null) as fastCount, 
                              count(status = 72 or null) as slowCount
                         from ('.$taskSql.') as tmp';
          $traceCount = $this->queryRow($countSql);
          return $traceCount;
     }



     //县长分管工作：多少项，完成多少，未完成几项
     // private function getLeaderTaskCountInfo($category, $leaderUnitId) {
     //      $where = 't.category = '.$category.' and c.leaderUnitId = '.$leaderUnitId;
     //      $taskSql = 'select c.taskId as taskId, t.done  from tbl_task t left join tbl_childtask c on t.id = c.taskId where ' . $where . ' group by taskId';

     //      $countSql = 'select count(done = 0 or null) as doingCount, count(done = 1 or null) as doneCount, count(done >= 0 or null) as taskCount from ('.$taskSql.') as tmp';
     //      $taskCount = $this->queryRow($countSql);
     //      return $taskCount;
     // }
     
     //县长分管工作：多少项，完成多少，未完成几项
     private function getLeaderTaskCountInfo($category, $leaderUnitId, $month = 0) {
          $where = 't.category = '.$category.' and c.leaderUnitId ='.$leaderUnitId;
          if ($month) {
               $where.= ' and year(trace.createtime) = YEAR(NOW()) and month(trace.createtime) = '.$month;
          }
          //将task，unitask，trace进行笛卡尔积组合并根据traceId排序
          $taskSql = 'select t.id as taskId, trace.unitTaskId, trace.id as traceId, trace.status from tbl_task t left join tbl_childtask c on t.id = c.taskId left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace trace on u.id = trace.unitTaskId where ' . $where . ' order by trace.Id desc';
          //取每个unitTask的最后一条任务状态
          $taskSql = 'select * from ('.$taskSql.') as tmp group by unitTaskId';

          //用多个unittask状态换算task的状态
          $taskSql = 'select taskId, if (count(status = 72 or null) > 0, 1, 0) as slow,
                                   if (count(status = 73 or null) > 0, 1, 0) as fast,
                                   if (count(status = 91 or null) > 0, 1, 0) as done,
                                   if (count(status = 50 or null) > 0, 1, 0) as overdue,
                                   if (count(status = 74 or null) > 0, 1, 0) as back,
                                   if (count(status != 91 && status != 73 && status != 72) > 0, 1, 0) as doing 
                      from ('.$taskSql.') as tmp group by taskId';

          $taskSql = 'select count(slow = 1 or null) as slowCount,
                             count(fast = 1 or null) as fastCount,
                             count(done = 1 or null) as doneCount,
                             count(doing = 1 or null) as doingCount,
                             count(overdue = 1 or null) as overdueCount,
                             count(back = 1 or null) as backCount,
                             count(taskId or null) as taskCount
                       from ('.$taskSql.') as tmp';

          $taskCount = $this->queryRow($taskSql);
          return $taskCount;
     }

     //工作进展：任务项分布(快。。。慢)
     private function getLeaderTaskProgressCountInfo($category, $leaderUnitId) {
          $where = 't.category = '.$category.' and c.leaderUnitId = '.$leaderUnitId;
          $taskSql = 'select c.taskId as taskId, t.done  from tbl_task t left join tbl_childtask c on t.id = c.taskId where ' . $where . ' group by taskId';

          $countSql = 'select count(done = 0 or null) as doingCount, count(done = 1 or null) as doneCount, count(done >= 0 or null) as taskCount from ('.$taskSql.') as tmp';
          $taskCount = $this->queryRow($countSql);
          return $taskCount;
     }

     //分管工作trace status状态统计
     private function getLeaderTraceStatusCountInfo($category, $leaderUnitId) {
          $traceSql = 'select t.unitTaskId, t.status from tbl_childtask c left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace t on u.id = t.unitTaskId left join tbl_task k on c.taskId = k.id where k.category = '.$category.' and c.leaderUnitId = '.$leaderUnitId;
          $countSql = 'select count(status = 50 or null) as sumOverdueCount, 
                              count(status = 72 or null) as sumSlowCount, 
                              count(status = 74 or null) as sumBackCount,
                              count(status = 73 or null) as sumFastCount from ('.$traceSql.') as tmp';
          $traceStatusCount = $this->queryRow($countSql);
          return $traceStatusCount;
     }

     //trace status状态统计
     function getTraceStatusCountInfo($category = 0) {
          $traceSql = 'select t.unitTaskId, t.status from tbl_childtask c left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace t on u.id = t.unitTaskId left join tbl_task k on c.taskId = k.id where k.category = '.$category;
          $countSql = 'select count(status = 51 or null) as sumSysPressCount, 
                              count(status = 52 or null) as sumPressCount, 
                              count(status = 50 or null) as sumOverdueCount, 
                              count(status = 71 or null) as sumNomalCount, 
                              count(status = 72 or null) as sumSlowCount, 
                              count(status = 74 or null) as sumBackCount,
                              count(status = 73 or null) as sumFastCount from ('.$traceSql.') as tmp';
          $traceStatusCount = $this->queryRow($countSql);
          return $traceStatusCount;
     }

     //获取上报工作数量
     function getLeaderTaskReportCountInfo($category = 0, $leaderUnitId = 0) {
          $reportUnitTaskIds = 'select unitTaskId from tbl_reporttask where unitId = '.$leaderUnitId;
          $where = 't.category = '.$category.' and ut.id in ('.$reportUnitTaskIds.')';
          $taskSql = 'select c.taskId as taskId, t.done  from tbl_task t left join tbl_childtask c on t.id = c.taskId left join tbl_unittask ut on ut.childTaskId = c.id where ' . $where . ' group by taskId';

          $countSql = 'select count(*) as taskCount from ('.$taskSql.') as tmp';
          $taskCount = $this->queryRow($countSql);
          return $taskCount;
     }

     //获取领导分管工作的汇总信息
     function getTaskSummaryByLeaderUnit($category = 0, $leaderUnitId = 0) {
          $taskSummary = $this->getLeaderTaskCountInfo($category, $leaderUnitId);
          $traceSummary = $this->getLeaderTraceStatusCountInfo($category, $leaderUnitId);
          $reportSummary = $this->getLeaderTaskReportCountInfo($category, $leaderUnitId);
          return array('taskSummary' => $taskSummary, 'traceSummary' => $traceSummary, 'reportSummary' => $reportSummary);
     }

     //县长的分管，月报及副县长的分管统计
     function getTaskSummaryAllLeaderUnit($category = 0) {
          $this->load->model('unit_model');
          $leaderUnit = $this->unit_model->getLeaderUnit();
          $result = array();
          $taskSummary =$this->getTaskSummaryByLeaderUnit($category, $leaderUnit['id']);
          $taskSummary['unit'] = $leaderUnit;
          
          //县长分管数据汇总
          $result[] = $taskSummary;
          //月报
          $result[] = $this->getMonthSummaryByLeaderUnit($category, $leaderUnit['id']);
          //上报数据
          $result[] = $taskSummary;

          //副县长的分管数据
          $deputyLeaderUnits = $this->unit_model->getDeputyLeaderUnits();
          foreach ($deputyLeaderUnits as $value) {
               $taskSummary = $this->getTaskSummaryByLeaderUnit($category, $value['id']);
               $taskSummary['unit'] = $value;
               $result[] = $taskSummary;
          }

          return $result;
     }


     //月报
     function getMonthSummaryByLeaderUnit($category = 0, $leaderUnitId = 0, $month = 0) {

           $traceSql = 'select t.unitTaskId, t.status from tbl_childtask c left join tbl_unittask u on c.id = u.childTaskId left join tbl_trace t on u.id = t.unitTaskId left join tbl_task k on c.taskId = k.id where k.category = '.$category.' and c.leaderUnitId = '.$leaderUnitId.' and month(t.createtime) = '.$month.' and year(t.createtime) = YEAR(NOW())';
          $countSql = 'select count(status = 50 or null) as sumOverdueCount, 
                              count(status = 72 or null) as sumSlowCount, 
                              count(status = 74 or null) as sumBackCount,
                              count(status = 73 or null) as sumFastCount from ('.$traceSql.') as tmp';

          $traceStatusCount = $this->queryRow($countSql);


          $taskSummary = $this->getLeaderTaskCountInfo($category, $leaderUnitId, $month);

          return array('traceSummary' => $traceStatusCount, 'taskSummary' => $taskSummary);
     }

}
?>
