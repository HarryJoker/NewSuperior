<?php
class Wxpay extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    private function nonce_str(){
        $result = '';
        $str = 'QWERTYUIOPASDFGHJKLZXVBNMqwertyuioplkjhgfdsamnbvcxz';
        for ($i=0;$i<32;$i++){
            $result .= $str[rand(0,48)];
        }
        return $result;
    }

    public function notify() {
      $xml = file_get_contents('php://input');
      // log_message('info', 'notify:'.$xml);
      // $xml = '<xml><appid><![CDATA[wxf7f4a4e351e5b1d5]]></appid>
      //         <bank_type><![CDATA[CMB_DEBIT]]></bank_type>
      //         <cash_fee><![CDATA[100]]></cash_fee>
      //         <fee_type><![CDATA[CNY]]></fee_type>
      //         <is_subscribe><![CDATA[N]]></is_subscribe>
      //         <mch_id><![CDATA[1525909451]]></mch_id>
      //         <nonce_str><![CDATA[8mYR2BkKknMSZmz2Z8t3mDknphBH84TX]]></nonce_str>
      //         <openid><![CDATA[oeLIB5TjxuwYU5d0f_fT1Ano4Kbs]]></openid>
      //         <out_trade_no><![CDATA[c3b5375affe6c5fe431b625c4a8948bd]]></out_trade_no>
      //         <result_code><![CDATA[SUCCESS]]></result_code>
      //         <return_code><![CDATA[SUCCESS]]></return_code>
      //         <sign><![CDATA[261458BBC7835DCEDB0561FB4B5D8C70]]></sign>
      //         <time_end><![CDATA[20190221190151]]></time_end>
      //         <total_fee>100</total_fee>
      //         <trade_type><![CDATA[JSAPI]]></trade_type>
      //         <transaction_id><![CDATA[4200000274201902213461915846]]></transaction_id>
      //         </xml>';

     $value = json_decode(json_encode(simplexml_load_string($xml, 'SimpleXMLElement', LIBXML_NOCDATA)), true);

     if ($value['return_code'] == 'SUCCESS' && $value['return_code'] == 'SUCCESS') {
        $this->load->model('order_model');
        $rows = $this->order_model->update_where(array('status' => 1), array('out_trade_no' => $value['out_trade_no']));
        // log_message('info', 'update sql:'.$this->db->last_query());
        // log_message('info', 'update rows:'.$rows);
     }
     
    }


    
    /*统一支付接口*/
    public function unitedPayRequest() 
    {


    //统一支付签名
    $appid = 'wxf7f4a4e351e5b1d5';//appid必填
    $body = '测试商品购买';//商品名必填
    $mch_id = '1525909451';//商户号必填
    $nonce_str = $this->nonce_str();//随机字符串，不长于32位。  
    $notify_url = 'http://www.yooar.com/wxpay/notify';//通知地址必填
    $total_fee = 10; //价格，这是一分钱
    $trade_type = "JSAPI";
    $openid = 'oeLIB5TjxuwYU5d0f_fT1Ano4Kbs';
    // $key = ''; //商户key必填，在商户后台获得
    $out_trade_no = '8363669611c2c3c4ff7a55c8dec3d56b';//自定义订单号必填
 
    $unifiedPayment = 'appid=' . $appid . '&body=' . $body . '&mch_id=' . $mch_id . '&nonce_str=' . $nonce_str . '&notify_url=' . $notify_url . '&openid=' . $openid . '&out_trade_no='.$out_trade_no . '&total_fee=' . $total_fee . '&trade_type=' . $trade_type;
     // . '&key=' . key;
    var_dump("unifiedPayment:". $unifiedPayment);
    $sign = strtoupper(md5($unifiedPayment));
    var_dump("签名md5:".$sign);
 
    //封装统一支付xml参数
    $formData = "<xml>";
    $formData .= "<appid>" . $appid . "</appid>";
    $formData .= "<body>" . $body . "</body>";
    $formData .= "<mch_id>" . $mch_id . "</mch_id>";
    $formData .= "<nonce_str>" . $nonce_str . "</nonce_str>";
    $formData .= "<notify_url>" . $notify_url . "</notify_url>";
    $formData .= "<openid>" . $openid . "</openid>";
    $formData .= "<out_trade_no>" . 201829239820932439023 . "</out_trade_no>";
    $formData .= "<total_fee>" . $total_fee . "</total_fee>";
    $formData .= "<trade_type>" . $trade_type . "</trade_type>";
    $formData .= "<sign>" . $sign . "</sign>";
    $formData .= "</xml>";
    var_dump("$formData:".$formData);

    $postUrl = 'https://api.mch.weixin.qq.com/pay/unifiedorder';

    $ch = curl_init();//初始化curl
        curl_setopt($ch, CURLOPT_URL,$postUrl);//抓取指定网页
        curl_setopt($ch, CURLOPT_HEADER, 0);//设置header
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
        curl_setopt($ch, CURLOPT_POST, 1);//post提交方式
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($formData));
        $data = curl_exec($ch);//运行curl
        curl_close($ch);


  //   //统一支付
  //   wx.request({
  //     url: 'https://api.mch.weixin.qq.com/pay/unifiedorder', //别忘了把api.mch.weixin.qq.com域名加入小程序request白名单，这个目前可以加
  //     method: 'POST',
  //     head: 'application/x-www-form-urlencoded',
  //     data: $formData, //设置请求的 header
  //     success: function (res) {
  //       console.log("返回商户", res.data);
  //       $result_code = util.getXMLNodeValue('result_code', res.data.toString("utf-8"));
  //       $resultCode = result_code.split('[')[2].split(']')[0];
  //       if (resultCode == 'FAIL') {
  //         $err_code_des = util.getXMLNodeValue('err_code_des', res.data.toString("utf-8"));
  //         $errDes = err_code_des.split('[')[2].split(']')[0];
  //         wx.showToast({
  //           title: errDes,
  //           icon: 'none',
  //           duration: 3000
  //         })
  //       } else {
  //         //发起支付
  //         $prepay_id = util.getXMLNodeValue('prepay_id', res.data.toString("utf-8"));
  //         $tmp = prepay_id.split('[');
  //         $tmp1 = tmp[2].split(']');
  //         //签名  
  //         $key = '';//商户key必填，在商户后台获得
  //         $appId = '';//appid必填
  //         $timeStamp = util.createTimeStamp();
  //         $nonceStr = util.randomString();
  //         $stringSignTemp = "appId=" . appId . "&nonceStr=" . nonceStr . "&package=prepay_id=" . tmp1[0] . "&signType=MD5&timeStamp=" . timeStamp . "&key=" . key;
  //         console.log("签名字符串", stringSignTemp);
  //         $sign = md5.md5(stringSignTemp).toUpperCase();
  //         console.log("签名", sign);
  //         $param = { "timeStamp": timeStamp, "package": 'prepay_id=' . tmp1[0], "paySign": sign, "signType": "MD5", "nonceStr": nonceStr }
  //         console.log("param小程序支付接口参数", param);
  //         that.processPay(param);
  //       }
 
  //     },
  //   })
 
  // },//unitedPayRequest()
      }

}
?>