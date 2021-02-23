<?php
class Jpush extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
        date_default_timezone_set('PRC'); 
    }/*}}}*/


    /**
     * 每天早上8点和晚上5点
     *
     * 早上8点和傍晚5点提醒2次
     * 领取任务逾期提醒
     * */
    public function awakeAcceptTask()
    {/*{{{*/
        $this->load->model('trace_model');
        $this->load->model('task_model');
        $tasks = $this->task_model->get_where(array('progress > ' => 1, 'accept' => 0));

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
     * 每天早上8点和搬完5点 
     *
     * 提前3天提醒报送工作
     *
     * 当天逾期报送
     *
     */
    public function awakeReportTask()
    {/*{{{*/
        $this->load->model('trace_model');
        $this->load->model('task_model');
        //接受进行中得任务
        $tasks = $this->task_model->get_where(array('progress > ' => 1, 'accept' => 1));
        foreach($tasks as $task)
        {
            //当月报送的trace
            $sql = "select * from tbl_trace where taskId = ".$task->id." and type = 0 and date_format(createtime,'%Y-%m')=date_format(now(),'%Y-%m')";
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
                $days = date('d', strtotime("now")) - date('d', strtotime($reportDate));
//                var_dump($task->name);
//                var_dump(date('Y-m-d:H:i', strtotime($reportDate)));
//                var_dump($days);
//                echo '<br />';
                //提前3天提醒
                if ($days == -3)
                {
                    if ($this->createNotificationTrace($task, '报送工作提醒'))
                    {
                        $this->pushAwakeReportTask($task, '报送工作提醒');
                    }

                }
                //逾期
                if ($days == 0)
                {
                    if ($this->createNotificationTrace($task, '已逾期未报送工作'))
                    {
                        $this->pushAwakeReportTask($task, '已逾期未报送工作');
                    }

                }

                //逾期1天
                if ($days == 1)
                {
                    if ($this->createNotificationTrace($task, '已逾期一天未报送工作'))
                    {
                        $this->pushAwakeReportTask($task, '已逾期一天未报送工作');

                    }
                }
            }
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
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience(array('tag' => $task->unitId))
            ->androidNotification($task->name, 
            array('title' => '请及时领取任务','extras' => array('id' => $task->id, 'type' => 2))
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
        //$httpStat = json_decode(json_encode($response))->http_code;
        //$this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');

    }/*}}}*/

    /**
     * 报送trace推送(提前3天提醒，逾期当天，逾期一天)
     */
    private function pushAwakeReportTask($task, $title)
    {/*{{{*/
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience(array('tag' => $task->unitId))
            ->androidNotification($task->name, 
            array('title' => $title,'extras' => array('id' => $task->id, 'type' => 3))
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
        $httpStat = json_decode(json_encode($response))->http_code;
        $this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');

    }/*}}}*/



    public function pushAcceptTask()
    {/*{{{*/
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience(array('tag' => '49'))
            ->androidNotification('运营好居家养老信息平台', 
            array('title' => '请及时领取任务','extras' => array('id' => 388, 'type' => 2))
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

    public function pushReportTask()
    {/*{{{*/
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience(array('tag' => '49'))
            ->androidNotification('推进农村社区服务中心建设三年计划', 
            array('title' => '请及时报送工作','extras' => array('id' => '387', 'type' => 3))
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

    public function pushData($data = array())
    {/*{{{*/
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience(array('tag' => $data['tag']))
            ->androidNotification($data['title'], array('title' => $data['alert'],'extras' => $data['extra']));

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            print $e;
        }
        $httpStat = json_decode(json_encode($response))->http_code;
        $this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');
    }/*}}}*/

    public function push($data = array())
    {/*{{{*/
        require 'jpush/conf.php';
        $push_payload = $client->push()
            ->setPlatform('android')
            ->addAllAudience('all')
            ->setNotificationAlert('Hello JPush')
            ->androidNotification('Hello Android', array('title' => 'hello jpush','extras' => array('key' => 'value','jiguang'),))
            ->message('message content', array(
                            'title' => 'message title: hello jpush',
                            'extras' => array(
                            'key' => 'value',
                            'jiguang'
                            ),));

        try {
            $response = $push_payload->send();
        }catch (\JPush\Exceptions\APIConnectionException $e) {
            // try something here
            print $e;
        } catch (\JPush\Exceptions\APIRequestException $e) {
            // try something here
            print $e;
        }
        $httpStat = json_decode(json_encode($response))->http_code;
        $this->set_content($httpStat == 200, $httpStat == 200 ? '发送成功' : '发送失败', '');
    }/*}}}*/

    public function sendCode($phone)
    {/*{{{*/
        require 'jpush/JSMS.php';
        $client = new \JiGuang\JSMS("fba33a9922ac91da73db53c5", "167250cda8d95bb611e06f24");
        $response = $client->sendCode($phone, 1);
       // Array ( [body] => {"msg_id":"283966203619"} [http_code] => 200 ) 
       //{"signature":"","errorCode":"0","errorMsg":"\u53d1\u9001\u6210\u529f","content":""}
        if ($response['http_code'] == 200)
        {
            $this->set_content(0, '发送成功', '');
        }
        else
        {
            $this->set_content(-1, '发送失败', '');
        }
    }/*}}}*/

    public function sendText($phone)
    {/*{{{*/
        require 'jpush/JSMS.php';

        $client = new \JiGuang\JSMS("fba33a9922ac91da73db53c5", "167250cda8d95bb611e06f24");
        $response = $client->sendCode($phone, 1);
        print_r($response);
    }/*}}}*/

}
?>
