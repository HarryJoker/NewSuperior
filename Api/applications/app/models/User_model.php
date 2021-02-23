<?php

class User_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     function getUsers($role = 0) {
          if ($role == 1) {
               $sql = 'select s.*, u.name as unitName from tbl_user s left join tbl_unit u on s.unitId = u.id where s.role = '.$role.' order by id desc';
          } else {
               $sql = 'select * from tbl_user where role = '.$role.' order by id desc';
          }
     	return $this->query($sql);
     }


     function getUnitUsers($unitId = 0) {
     	$users = $this->get_where(array('unitId' => $unitId));
     	return $users;
     }


     function autoGenerateUnitUsers($unitId = 0) {
     	if (!$unitId) return;
     	$this->load->model('unit_model');
     	$unit = $this->unit_model->get($unitId);
     	if (!count($unit)) return;
     	
     	if ($unit['role'] >= 1 && $unit['role'] <= 3) {
     		$this->createUser($unitId, 0);
     		return;
     	}
     	if ($unit['role'] == 4) {
     		$this->createUser($unitId, 1);
     		$this->createUser($unitId, 2);
     		$this->createUser($unitId, 3);
     	}
     }


     function createUser($unitId = 0, $rule = 0) {
     	if (!$unitId) return;
     	$data = array('logo' => 'ic_avatar.png', 
     		'name' => '未设置', 
     		'account' => 'zwdc'.$this->getRandNumber(),
     		'password' => '123456', 
     		'role' => 1, 
     		'unitId' => $unitId, 
     		'rule' => $rule);
     	$this->create_id($data);
     }

     /**
	 * 生成不重复的随机数字
	 * @param  int $start  需要生成的数字开始范围
	 * @param  int $end    结束范围
	 * @param  int $length 需要生成的随机数个数
	 * @return number      生成的随机数
	 */
	function getRandNumber($start=0,$end=9,$length=8){
		//初始化变量为0
		$connt = 0;
		//建一个新数组
		$temp = array();
		while($connt < $length){
		//在一定范围内随机生成一个数放入数组中
		$temp[] = mt_rand($start, $end);
		//$data = array_unique($temp);
		//去除数组中的重复值用了“翻翻法”，就是用array_flip()把数组的key和value交换两次。这种做法比用 array_unique() 快得多。	
		$data = array_flip(array_flip($temp));
		//将数组的数量存入变量count中	
		$connt = count($data);
		}
		//为数组赋予新的键名
		shuffle($data);
		//数组转字符串
		$str=implode(",", $data);
		//替换掉逗号
		$number=str_replace(',', '', $str);
		return $number;
	}


	function getPlatformUserCount() {
		$sql = 'select count(*) as userCount from tbl_user';
		$result = $this->queryRow($sql);
		return $result;
	}


	function getPlatformMassUserCount() {
		$sql = 'select count(*) as massUserCount from tbl_user where role = 0 and to_days(createTime) = to_days(now())';
		$result = $this->queryRow($sql);
		return $result;
	}

 }
 ?>

