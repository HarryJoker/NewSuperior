<?php
class Jpush extends CA_Controller {

    private static $client = null;
    private static $ins = null;

    public function __construct()
    {/*{{{*/
        parent::__construct();
        require 'jpush/conf.php';
        $this::$client = $client;
    }/*}}}*/


    public static function getInstance()
    {/*{{{*/
        if (null == self::$ins && false == self::$ins instanceof self) {
            self::$ins = new self();
        }
        return self::$ins;
    }/*}}}*/

    public function push($title = '', $extra = array(), $tag = 'all')
    {/*{{{*/

        if ($tag == 'all')
        {
            $push_payload = self::$client->push()
            ->setPlatform('android')
            ->addAllAudience()
            ->androidNotification($title, array('title' => '最新通知','extras' => $extra));

        }
        else
        {
            $push_payload = self::$client->push()
                ->setPlatform('android')
                ->addTag(array($tag))
                ->androidNotification($title, array('title' => '最新通知','extras' => $extra));
        }
        try {
            $response = $push_payload->send();
        } catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            //print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            //print $e;
        }
        return ;
    }/*}}}*/

    public function pushTaskTip($title = '', $extra = array(), $tag = 'all')
    {/*{{{*/

        if ($tag == 'all')
        {
            $push_payload = self::$client->push()
            ->setPlatform('android')
            ->addAllAudience()
            ->androidNotification($title, array('title' => '请及时领取新任务','extras' => $extra));

        }
        else
        {
            $push_payload = self::$client->push()
                ->setPlatform('android')
                ->addTag(array($tag))
                ->androidNotification($title, array('title' => '请及时领取新任务','extras' => $extra));
        }
        try {
            $response = $push_payload->send();
        } catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            //print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            //print $e;
        }
        return ;
    }/*}}}*/
	
    public function pushTaskBack($title = '', $extra = array(), $tag = 'all')
    {/*{{{*/

        if ($tag == 'all')
        {
            $push_payload = self::$client->push()
            ->setPlatform('android')
            ->addAllAudience()
            ->androidNotification($title, array('title' => '报送任务被退回','extras' => $extra));

        }
        else
        {
            $push_payload = self::$client->push()
                ->setPlatform('android')
                ->addTag(array($tag))
                ->androidNotification($title, array('title' => '报送任务被退回','extras' => $extra));
        }
        try {
            $response = $push_payload->send();
        } catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            //print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            //print $e;
        }
        return ;
    }/*}}}*/

    /**
     * 每天早上8点和晚上4点
     *
     * 早上8点和傍晚4点各提醒次
     * 领取任务逾期提醒
     * */
    public function awakeAcceptTask()
    {/*{{{*/
        $this->load->model('trace_model');
        $this->load->model('task_model');
        //$tasks = $this->task_model->get_where(array('progress > ' => 1, 'accept' => 0, 'valid' => 1));
        $tasks = $this->task_model->get_where(array('progress <' => 5, 'progress > ' => 1, 'YEAR(updatetime) >' => '2017', 'accept' => 0, 'valid' => 1));

//        var_dump(date('Y-m-d:H:i'));
//        echo '<br />';
        foreach($tasks as $task)
        {
            //判断时间
            $hour = intval((strtotime("now") - strtotime($task->updatetime)) / 3600);
//            var_dump($task->name);
//            var_dump(date('Y-m-d:H:i', strtotime($task->updatetime)));
//            var_dump('---------------- '.$hour);
//            echo '<br />';
            //发两次
            if ($hour >= 0 && $hour < 24) 
            {
                if ($this->createNotificationTrace($task, '请及时领取任务'))
                {
                    $this->pushAwakeAcceptTask($task);
                }
            }
        }

    }/*}}}*/

    /**
     * 每天早上8点和搬完4点 
     *
     * 提前1天提醒报送工作
     *
     * 当天逾期报送
     *
     */
    public function awakeReportTask()
    {/*{{{*/
        $this->load->model('trace_model');
        $this->load->model('task_model');
        //领取任务进行中得任务
        //$tasks = $this->task_model->get_where(array('progress > ' => 1, 'accept' => 1, 'valid' => 1));
        $tasks = $this->task_model->get_where(array('progress <' => 5, 'progress > ' => 1, 'YEAR(updatetime) >' => '2017', 'accept' => 0, 'valid' => 1));
        foreach($tasks as $task)
        {
            if ($task->reportType == 3)
            {
                $this->handleWeekTask($task);
            }
            else
            {
                $this->handleMonthTask($task);
            }
        }
    }/*}}}*/

    //周报
    private function handleWeekTask($task)
    {/*{{{*/
        //当月报送的trace
        $sql = "select * from tbl_trace where taskId = ".$task->id." and type = 0 and weekofyear(createtime)=weekofyear(curdate())";
        $count = count($this->trace_model->query($sql));
        //没有报送工作
        if ($count == 0)
        {
            $week = date('w',strtotime("now"));
            $week = $week == 0 ? 7 : $week;
            //提前1天提醒
            $weeks = $week - $task->reportDate;
			//周日与周一相差一天
			if ($week == 7 && $task->reportDate == 1) $weeks = 1;
            //提前一天的下午提醒
            if (($weeks == 1 && date('a') == 'pm') || ($weeks == 0 && date('a') == 'am'))
            {
                if ($this->createNotificationTrace($task, '报送工作提醒'))
                {
                    $this->pushAwakeReportTask($task, '报送工作提醒');
                }
            }
            //当天的下午逾期
            if ($weeks == 0 && date('a') == 'pm')
            {
                if ($this->createNotificationTrace($task, '已逾期未报送工作'))
                {
                    $this->pushAwakeReportTask($task, '已逾期未报送工作');
                }
            }

            //逾期1天上午
            if ($weeks == 1 && date('a') == 'am')
            {
                if ($this->createNotificationTrace($task, '已逾期一天未报送工作'))
                {
                    $this->pushAwakeReportTask($task, '已逾期一天未报送工作');
                }
            }
        }

    }/*}}}*/


	/**
	 * 求date1小于date2之间相差的天数,正数是小于天数，负数是大于天数
	 * (针对1970年1月1日之后，求之前可以采用泰勒公式)
	 * @param string $a
	 * @param string $b
	 * @return number
	 */
	function lessThanDays($a,$b)
	{
		$a_dt = getdate($a);
		$b_dt = getdate($b);
		$a_new = mktime(12, 0, 0, $a_dt['mon'], $a_dt['mday'], $a_dt['year']);
		$b_new = mktime(12, 0, 0, $b_dt['mon'], $b_dt['mday'], $b_dt['year']);
		return round(($b_new - $a_new) / 86400);
	}

    //月报或具体时间
    private function handleMonthTask($task)
    {/*{{{*/
        //当月报送的trace(trace时间大于等于task并小于等于当前时间)
        $sql = "select * from tbl_trace where taskId = ".$task->id." and type = 0 and valid = 1 and date_format(createtime, '%Y-%m') >= date_format('".$task->updatetime."', '%Y-%m') and date_format(createtime,'%Y-%m') <= date_format(now(),'%Y-%m')";
        $count = count($this->trace_model->query($sql));
        //没有报送工作
        if ($count == 0)
        {
            $reportDate = $task->reportDate;
            if ($task->reportType == 1)
            {
                //拼接时间
                $reportDate = date('Y').'-'.date('m').'-'.$task->reportDate;
            }
			$days = $this->lessThanDays(strtotime($reportDate), strtotime(date('Y-m-d')));
            //提前1天提醒
            if (($days == 1 && date('a') == 'pm') || ($days == 0 && date('a') == 'am'))
            {
                if ($this->createNotificationTrace($task, '报送工作提醒'))
                {
                    $this->pushAwakeReportTask($task, '报送工作提醒');
                }

            }
            //逾期
            if ($days == 0 && date('a') == 'pm')
            {
                if ($this->createNotificationTrace($task, '已逾期未报送工作'))
                {
                    $this->pushAwakeReportTask($task, '已逾期未报送工作');
                }

            }

//            //逾期1天
//            if ($days == 1)
//            {
//                if ($this->createNotificationTrace($task, '已逾期一天未报送工作'))
//                {
//                    $this->pushAwakeReportTask($task, '已逾期一天未报送工作');
//
//                }
//            }
        }

    }/*}}}*/

    private function createNotificationTrace($task, $content ='系统催报任务')
    {/*{{{*/
        $params = array(
            'taskId' => $task->id,
            'userId' => 0, 
            'category' => $task->category,
            'unitId' => 0,
            'unitName' => '系统',
            'content' => $content,
            'type' => 1,
       );
        $id = $this->trace_model->create_id($params);
        return $id > 0;
    }/*}}}*/

    /**
     * 逾期领取task推送
     */
    private function pushAwakeAcceptTask($task)
    {/*{{{*/
        $push_payload = $this->client->push()
            ->setPlatform('android')
            ->addTag(array($task->unitId))
            ->androidNotification($task->name, 
            array('title' => '请及时领取任务','extras' => array('id' => $task->id, 'type' => 2))
            );

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            //print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            //print $e;
        }
        //$httpStat = json_decode(json_encode($response))->http_code;
        //$this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');

    }/*}}}*/

    /**
     * 报送trace推送(提前3天提醒，逾期当天，逾期一天)
     */
    private function pushAwakeReportTask($task, $title)
    {/*{{{*/
        $push_payload = $this->client->push()
            ->setPlatform('android')
            ->addTag(array($task->unitId))
            ->androidNotification($task->name, 
            array('title' => $title,'extras' => array('id' => $task->id, 'type' => 3))
            );

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            //print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            //print $e;
        }
        //$httpStat = json_decode(json_encode($response))->http_code;
        //$this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');

    }/*}}}*/

    /**********************************************/
    //模拟测试
    public function pushAcceptTask()
    {/*{{{*/
        $push_payload = $this->client->push()
            ->setPlatform('android')
            ->addTag(array('102'))
            ->androidNotification('测试push', 
            array('title' => '请及时领取任务','extras' => array('id' => 1473, 'type' => 2))
            );

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            print $e;
        }

    }/*}}}*/

    //模拟测试
    public function pushReportTask()
    {/*{{{*/
        $push_payload = $this->client->push()
            ->setPlatform('android')
            ->addTag(array('102'))
            ->androidNotification('推进农村社区服务中心建设三年计划', 
            array('title' => '请及时报送工作','extras' => array('id' => '635', 'type' => 3))
            );

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            print $e;
        }

    }/*}}}*/

}
?>
