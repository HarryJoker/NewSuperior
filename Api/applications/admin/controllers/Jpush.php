<?php
class Jpush {

    private static $ins = null;
    private static $client = null;

    protected function __construct()
    {/*{{{*/
        require 'jpush/conf.php';
        self::$client = $client;

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
}
?>
