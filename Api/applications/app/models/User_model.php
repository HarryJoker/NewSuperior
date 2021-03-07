<?php

class User_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


	//获取整条字符串所有汉字拼音首字母
	function pinyin_long($zh){
		$ret = "";
		$s1 = iconv("UTF-8","GBK//IGNORE", $zh);
		$s2 = iconv("GBK","UTF-8", $s1);
		if($s2 == $zh){$zh = $s1;}
		for($i = 0; $i < strlen($zh); $i++){
			$s1 = substr($zh,$i,1);
			$p = ord($s1);
			if($p > 160){
				$s2 = substr($zh,$i++,2);
				$ret .= getfirstchar($s2);
			}else{
				$ret .= $s1;
			}
		}
		return $ret;
	}

	//获取单个汉字拼音首字母。注意:此处不要纠结。汉字拼音是没有以U和V开头的
	/**
	 * 取汉字的第一个字的首字母
	 * @param string $str
	 * @return string|null
	 */
	function getFirstChar($str) {
		if (empty($str)) {
			return '';
		}

		$fir = $fchar = ord($str[0]);
		if ($fchar >= ord('A') && $fchar <= ord('z')) {
			return strtoupper($str[0]);
		}

		$s1 = @iconv('UTF-8', 'gb2312//IGNORE', $str);
		$s2 = @iconv('gb2312', 'UTF-8', $s1);
		$s = $s2 == $str ? $s1 : $str;
		if (!isset($s[0]) || !isset($s[1])) {
			return '';
		}

		$asc = ord($s[0]) * 256 + ord($s[1]) - 65536;

		if (is_numeric($str)) {
			return $str;
		}

		if (($asc >= -20319 && $asc <= -20284) || $fir == 'A') {
			return 'A';
		}
		if (($asc >= -20283 && $asc <= -19776) || $fir == 'B') {
			return 'B';
		}
		if (($asc >= -19775 && $asc <= -19219) || $fir == 'C') {
			return 'C';
		}
		if (($asc >= -19218 && $asc <= -18711) || $fir == 'D') {
			return 'D';
		}
		if (($asc >= -18710 && $asc <= -18527) || $fir == 'E') {
			return 'E';
		}
		if (($asc >= -18526 && $asc <= -18240) || $fir == 'F') {
			return 'F';
		}
		if (($asc >= -18239 && $asc <= -17923) || $fir == 'G') {
			return 'G';
		}
		if (($asc >= -17922 && $asc <= -17418) || $fir == 'H') {
			return 'H';
		}
		if (($asc >= -17417 && $asc <= -16475) || $fir == 'J') {
			return 'J';
		}
		if (($asc >= -16474 && $asc <= -16213) || $fir == 'K') {
			return 'K';
		}
		if (($asc >= -16212 && $asc <= -15641) || $fir == 'L') {
			return 'L';
		}
		if (($asc >= -15640 && $asc <= -15166) || $fir == 'M') {
			return 'M';
		}
		if (($asc >= -15165 && $asc <= -14923) || $fir == 'N') {
			return 'N';
		}
		if (($asc >= -14922 && $asc <= -14915) || $fir == 'O') {
			return 'O';
		}
		if (($asc >= -14914 && $asc <= -14631) || $fir == 'P') {
			return 'P';
		}
		if (($asc >= -14630 && $asc <= -14150) || $fir == 'Q') {
			return 'Q';
		}
		if (($asc >= -14149 && $asc <= -14091) || $fir == 'R') {
			return 'R';
		}
		if (($asc >= -14090 && $asc <= -13319) || $fir == 'S') {
			return 'S';
		}
		if (($asc >= -13318 && $asc <= -12839) || $fir == 'T') {
			return 'T';
		}
		if (($asc >= -12838 && $asc <= -12557) || $fir == 'W') {
			return 'W';
		}
		if (($asc >= -12556 && $asc <= -11848) || $fir == 'X') {
			return 'X';
		}
		if (($asc >= -11847 && $asc <= -11056) || $fir == 'Y') {
			return 'Y';
		}
		if (($asc >= -11055 && $asc <= -10247) || $fir == 'Z') {
			return 'Z';
		}

		return '';
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
     		$this->createUser($unitId, $unit['name'], 1);
     		return;
     	}
     	if ($unit['role'] == 4) {
     		$this->createUser($unitId, $unit['name'], 1);
//     		$this->createUser($unitId, 2);
     		$this->createUser($unitId, $unit['name'], 3);
     	}
     }


     function createUser($unitId = 0, $rule = 0) {
     	if (!$unitId) return;
     	$data = array('logo' => 'ic_avatar.png', 
     		'name' => '未设置', 
     		'account' => 'bx'.$rule,
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

