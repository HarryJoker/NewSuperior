<?php
class Qiniu extends CA_Controller {

    private $accessKey = 'rioPBHsKpDgTXNpw8IGHOecdg6DMWaAO7_L3_7EB';
    private $secretKey = 'ue9VmhBrgefxMDq_Xxp3bRpXe8ufbuNsOKmhnQDD';

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function getAccessKey()
    {
        return $this->accessKey;
    }


    public function getToken($bucket='test', $key = null, $expires = 3600, $policy = null, $strictPolicy = true)
    {
        $auth = new Qiniu\Auth($this->accessKey, $this->secretKey);
        $token = $auth->uploadToken($bucket);
       return $token;
    }

    public function queryToken($bucket='test', $key = null, $expires = 3600, $policy = null, $strictPolicy = true)
    {
        $auth = new Qiniu\Auth($this->accessKey, $this->secretKey);
        $token = $auth->uploadToken($bucket);
        $this->set_content(0, '获取成功', array('token' => (array('token' => $token))));
    }
}

?>