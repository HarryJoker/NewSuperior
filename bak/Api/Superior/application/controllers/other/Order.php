<?php
class Order extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/



    private $appid = 'wxf7f4a4e351e5b1d5';//appid必填
    // private $body = '测试商品购买';//商品名必填
    private $mch_id = '1525909451';//商户号必填
    // private $nonce_str = that.randomString();  //随机字符串，不长于32位。  
    private $notify_url = 'https://train.yooar.com';     //通知地址必填
    // private $total_fee = parseInt(0.01 * 100);           //价格，这是一分钱
    private $trade_type = "JSAPI";
    private $key = 'J092323JDUAyooarr828123JOSDFHOWN'; //商户key必填，在商户后台获得
    // private $out_trade_no = '8363669611c2c3c4ff7a55c8dec3d56b';//自定义订单号必填

    public function create()
    {/*{{{*/

        $good = $this->input->json();

        $data = array(
                    'out_trade_no'=> $this->order_number($good['openId']), 
                    'order_number'=> date('Ymd',time()).time().rand(10,99),
                    'user_id' => $good['userId'],
                    'product_amount' => $good['discount'] < 100 ? $good['discountprice'] : $good['price'], 
                    'order_amount' => $good['discount'] < 100 ? $good['discountprice'] : $good['price']
                );
        
        $id = $this->order_model->create_id($data);

        $data['id'] = $id;

        $this->load->model('orderitem_model');
        $item = array('order_id' => $id, 'good_id' => $good['id'], 'goods_price' => $good['discount'] < 100 ? $good['discountprice'] : $good['price'], 'subtotal' => $good['discount'] < 100 ? $good['discountprice'] : $good['price']);
        $itemId = $this->orderitem_model->create_id($item);

        $result = array('appInfo' =>array(
                    'appid' => $this->appid, 
                    'mch_id' => $this->mch_id, 
                    'notify_url' => $this->notify_url, 
                    'trade_type' => $this->trade_type, 
                    'key' => $this->key,
                ), 'order' => $data);



        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function update($orderId = 0) 
    {
        $data = $this->input->json();
        $id = $this->order_model->update_where($data, array('id' => $orderId));
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }

    public function getOrders($userId)
    {/*{{{*/
        $orders = $this->order_model->select_where(array('user_id' => $userId));

        $this->load->model('orderitem_model');
        $this->load->model('clasz_model');
        $this->load->model('student_model');
        foreach ($orders as &$order) {
            $items = $this->orderitem_model->select_where(array('order_id' => $order['id']));
            foreach ($items as &$item) $item['good'] = $this->clasz_model->get_where(array('id' => $item['good_id']));
            $order['items'] = $items;
            if ($order['student_id']) {
                $student = $this->student_model->get_where(array('id' => $order['student_id']));
                $student['age'] = date('Y') - date('Y', strtotime($student['birthday']));
                $order['student'] = $student;
            }

        }
        $this->set_content(0, '获取成功', $orders);
    }/*}}}*/

    






    //生成订单号 
    private function order_number($openid){
        // date('Ymd',time()).time().rand(10,99);//18位 
        return md5($openid.time().rand(10,99));//32位
    }
}
?>