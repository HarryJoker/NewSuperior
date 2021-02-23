<?php

class Reward_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     function getUnitTaskRewards() {
        $rewards = $this->getMetaRewards();
        if (count($rewards) == 0) {
            return $rewards;
        }

        $unitTaskIds = array_keys($rewards);
        $this->load->model('task_model');
        $extraSql = ' and ut.id in ('.implode(',', $unitTaskIds).')';
        $tasks = $this->task_model->getTaskList(0, 0, 0, 0, $extraSql);
        for ($i=0; $i < count($tasks); $i++) { 
            if (count($tasks[$i]['unitTasks'])) {
                for ($n=0; $n < count($tasks[$i]['unitTasks']); $n++) { 
                    $tasks[$i]['unitTasks'][$n]['reward'] = $rewards[ $tasks[$i]['unitTasks'][$n]['id']]['content'];
                }
            }
        }
        return $tasks;
     }


     private function getMetaRewards() {
        $rewards = $this->result();
        $newRewars = array();
        foreach ($rewards as $value) {
            $newRewars[$value['unitTaskId']] = $value;
        }
        return $newRewars;
     }

}
?>


