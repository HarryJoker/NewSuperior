<?php

class User_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


	private function getFirstCharterBulk($str, $is_lowercase = true) {
		$return = '';
		/* 拆分字符串 */
		$chars = $this->str_split_unicode($str);
		foreach ($chars as $k => $v) {
			/* 获取单个拼音首字母 */
			$english_char = $this->getFirstCharterSingle($v);
			if($is_lowercase){
				$english_char = strtolower($english_char);
			}
			$return .= $english_char;
		}
		return $return;
	}

	private function getFirstCharterSingle($str) {
		if (empty($str)) {
			return '';
		}
		$fchar = ord($str{0});
		if ($fchar >= ord('A') && $fchar <= ord('z'))
			return strtoupper($str{0});
		$s1 = iconv('UTF-8', 'gb2312', $str);
		$s2 = iconv('gb2312', 'UTF-8', $s1);
		$s = $s2 == $str ? $s1 : $str;
		$asc = ord($s{0}) * 256 + ord($s{1}) - 65536;
		if ($asc >= -20319 && $asc <= -20284)
			return 'A';
		if ($asc >= -20283 && $asc <= -19776)
			return 'B';
		if ($asc >= -19775 && $asc <= -19219)
			return 'C';
		if ($asc >= -19218 && $asc <= -18711)
			return 'D';
		if ($asc >= -18710 && $asc <= -18527)
			return 'E';
		if ($asc >= -18526 && $asc <= -18240)
			return 'F';
		if ($asc >= -18239 && $asc <= -17923)
			return 'G';
		if ($asc >= -17922 && $asc <= -17418)
			return 'H';
		if ($asc >= -17417 && $asc <= -16475)
			return 'J';
		if ($asc >= -16474 && $asc <= -16213)
			return 'K';
		if ($asc >= -16212 && $asc <= -15641)
			return 'L';
		if ($asc >= -15640 && $asc <= -15166)
			return 'M';
		if ($asc >= -15165 && $asc <= -14923)
			return 'N';
		if ($asc >= -14922 && $asc <= -14915)
			return 'O';
		if ($asc >= -14914 && $asc <= -14631)
			return 'P';
		if ($asc >= -14630 && $asc <= -14150)
			return 'Q';
		if ($asc >= -14149 && $asc <= -14091)
			return 'R';
		if ($asc >= -14090 && $asc <= -13319)
			return 'S';
		if ($asc >= -13318 && $asc <= -12839)
			return 'T';
		if ($asc >= -12838 && $asc <= -12557)
			return 'W';
		if ($asc >= -12556 && $asc <= -11848)
			return 'X';
		if ($asc >= -11847 && $asc <= -11056)
			return 'Y';
		if ($asc >= -11055 && $asc <= -10247)
			return 'Z';
		return null;
	}
	/* 拆分字符串 */
	private function str_split_unicode($str, $l = 0) {
		if ($l > 0) {
			$ret = array();
			$len = mb_strlen($str, "UTF-8");
			for ($i = 0; $i < $len; $i += $l) {
				$ret[] = mb_substr($str, $i, $l, "UTF-8");
			}
			return $ret;
		}
		return preg_split("//u", $str, -1, PREG_SPLIT_NO_EMPTY);
	}


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
     		$this->createUser($unitId, $unit['name'], 1, 1);
     		return;
     	}
     	if ($unit['role'] == 4) {
     		$this->createUser($unitId, $unit['name'], 1, 1);
//     		$this->createUser($unitId, 2);
     		$this->createUser($unitId, $unit['name'], 3, 2);
     	}
     }


     function createUser($unitId = 0, $unitName = '', $rule = 0, $num = 0) {
     	if (!$unitId) return;
     	var_dump($unitName. ' first char '. 'bx'.$this->getFirstCharterBulk($unitName).$num);
     	$data = array('logo' => 'ic_avatar.png',
     		'name' => '未设置', 
			'account' => 'bx'.$this->getFirstCharterBulk($unitName).$num,
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

