<?php
class User extends CA_Controller {
function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getAllUnits()
    {/*{{{*/
        $result = $this->unit_model->result();
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    //用手机号or邀请码创建账号
    public function create()
    {/*{{{*/
        $data = array('phone' => $this->input->post('phone'));
        $users = $this->user_model->get_where($data);
        if (count($users) > 0)
        {
            $this->set_content(-1,'您手机号已注册', array('err' => 0));
        }
        else
        {
			//被邀请码
			//$exSpreadCode = $this->input->post('exSpreadCode');
			$data = $this->input->post();
			$data['createTime'] = date('Y-m-d H:i:s');
			//unset($data['exSpreadCode']); 

            $id = $this->user_model->create_id($data);
			if ($id > 0) 
			{
				//创建账号成功，有邀请码计算M币
				/**
				if (isset($exSpreadCode))
				{
					$this->calculateCoins($id, $exSpreadCode);
				}
				**/
				$user = $this->user_model->get($id);
				$this->set_content(0, '创建成功', $user);
			}
			else
			{
				$this->set_content(-1, '创建失败', array('id' => $id));
			}
        }
    }/*}}}*/

	//获取用户信息
	public function get($id = 0) 
	{/*{{{*/
		$sql = 'select * from tbl_user where id = '.$id;
		$result = $this->user_model->query($sql);
		$result = count($result) > 0 ? $result[0] : array();
		$this->set_content(0, '创建成功', $result);
	}/*}}}*/

    //更新个人信息
    public function update($id = 0)
    {/*{{{*/
		//验证手机号是否已经注册
		$phone = $this->input->post('phone');
		if (isset($phone) && strlen($phone) == 11)
		{
			$users = $this->user_model->get_where(array('phone' => $phone));
			if (count($users) > 0)
			{
				$this->set_content(-1,'您手机号已注册', array('err' => 0));
				return;
			}
		}
		//被邀请码
		$exSpreadCode = $this->input->post('exSpreadCode');
		$data = $this->input->post();
		unset($data['exSpreadCode']); 

        if (isset($data) && count($data) == 1)
        {
            $rows = $this->user_model->update_where($data, array('id'=> $id));
			//个人信息更新成功，有邀请码计算M币
			if ($rows && isset($exSpreadCode))
			{
				$this->calculateCoins($id, $exSpreadCode);
			}
			$user = $this->user_model->get($id);
			$this->set_content(0, '更新成功', $user);
        }
        else 
        {
            $this->set_content(-1, '参数错误', array('id' => '0'));
        }

    }/*}}}*/

	//效验邀请码
	public function verifySpreadCode($id = 0)
	{/*{{{*/
		//验证码有效性
		$result = $this->calculateCoins($id, $this->input->post('spreadCode'));
		if (count($result) == 1)
		{
			if (array_key_exists('-1', $result))
			{
				$this->set_content(-1, $result['-1'], array('id' => '0'));
			}
			else
			{
				$user = $this->user_model->get($id);
				$this->set_content(0, '已为您增加M币', $user);
			}
		}
		else
		{
			$this->set_content(-1, '邀请码效验失败', array());
		}
	}/*}}}*/

	private function calculateCoins($id = 0, $spreadCode = '')
	{/*{{{*/
		//验证码有效性
		$spreadUser = $this->user_model->get_where(array('spreadCode' => $spreadCode, 'id !=' => $id));
		if (count($spreadUser) == 0)
		{
			//$this->set_content(-1, '无效邀请码', array());
			return array('-1' => '无效邀请码');
		}

		//当前用户是否已经被邀请过
		$user = $this->user_model->get_where(array('id' => $id, 'hasInvite' => '0'));
		if (count($user) == 0)
		{
			//$this->set_content(-1, '您已被邀请过', array());
			return array('-1' => '您已被邀请过了');
		}

		//开启事务
		$this->db->trans_begin();

		//1,当前用户更新spread信息 
		$sql = "UPDATE `tbl_user` SET `coins` = coins + 500, `hasInvite` = '1' WHERE `id` =".$id ;
		$this->user_model->excute($sql);

		//2,更新邀请用户的spread
		$sql = "UPDATE `tbl_user` SET `coins` = ".$this->makeCoinsSql().", `inviters` = if(inviters = '', '".$id."', concat_ws(',',inviters, '".$id."')), `inviteCount` = inviteCount + 1 WHERE `spreadCode` = '".$spreadCode."'";
		$this->user_model->excute($sql);
		//推送提醒邀请用户M币增加
		/* xxxxxxxxxxxxxxxxxxx */

		if ($this->db->trans_status() === FALSE)
		{
			//执行sql失败
			$this->db->trans_rollback();
			//$this->set_content(-1, '验证邀请码失败', array());
			return array('-1' => '验证邀请码失败');
		}
		else
		{
			//sql事务成功
			$this->db->trans_commit();
			//$user = $this->user_model->get($id);
			//$this->set_content(0, '已为您增加M币', $user);
			return array('0' => '已为您增加M币');
		}
	}/*}}}*/

	//邀请码算积分sql
	private function makeCoinsSql()
	{/*{{{*/
		$inviters = array('1' => '0',   //一个人300 
						'2' => '100',  //700, 多增100 
						'3' => '100',  //1100,多增100 
						'4' => '100',  //1500，多增加100 
						'10' => '1400', //5000，多增加（5000 - 300 * 6 - 1500 - 300): 1400
						'50' => '-1',   //无限制
						'100' => '-2'   //无限制，无广告
						);
		$sqlExtra = "CASE inviteCount + 1 ";
		foreach ($inviters as $key => $value)
		{
			if ($key == 50) 
			{
				$sqlExtra.= " WHEN ".$key." THEN -1 ";
			}
			else if ($key == 100) 
			{
				$sqlExtra.= " WHEN ".$key." THEN -2 ";
			}
			else
			{
				$sqlExtra.= " WHEN ".$key." THEN coins + 300 + ".$value." ";
			}
		}
		$sqlExtra.= " ELSE  if(inviteCount + 1 > 100, coins, if(inviteCount + 1 > 50, coins, coins + 300)) ";
		$sqlExtra.=" end";
		return $sqlExtra;
	}/*}}}*/

    //通过phone获取用户
    public function login()
    {/*{{{*/
        //$sql = "select *, IFNULL((curdate() <= date(avalidTime)), 0) as isVip from tbl_user where phone = ".$phone;
        //$user = $this->user_model->query($sql);
		$users = $this->user_model->get_where($this->input->post());
        //$user = isset($users) && count($users) == 1 ? $users[0] : array('id' => '0');
		if(isset($users) && count($users))
		{
			$this->set_content(0, '登录成功', $users[0]);
		}
		else
		{
			$this->set_content(-1, '账号不存在', array('id' => 0));
		}



		/**
        if (is_array($user))
        {
            $this->set_content(-1, '账号不存在', $user);
        }
        else
        {
            if ($user->verify == 2)
            {
                $this->set_content(-1, '帐号被冻结，请与管理员联系', $user);
            }
            else
            {
                $sql = "select IFNULL(group_concat(distinct(crackId)),'') as favorites from tbl_favorite where userId = ".$user->id.";";
                $favorites = $this->user_model->query($sql);
                $favorites = $favorites[0]->favorites;
                $user->favorites = $favorites; 

                $sql = "select * from tbl_pay where isVerify = 0 and userId = ".$user->id.";";
                $pays = $this->user_model->query($sql);
                $user->pays = $pays;
                $info = '';
                if (count($pays))
                {
                    $info = '您有'.count($pays).'个会员信息待审核：【';
                    foreach($pays as $pay)
                    {
                        $info.= $pay->levelName;
                    }
                    $info.='】';
                }
                $user->payInfo = $info; 
                $this->set_content(0, '登录成功', $user);

            }
        }
		**/

    }/*}}}*/

    public function updateLogo($userId = 0)
    {/*{{{*/
        $config['upload_path']='./uploads/';
        //$config['allowed_types']='png|jpeg|jpg|gif';
        $config['allowed_types']='*';
        //$config['max_size']=100;
        //$config['max_width']=1024;
        //$config['max_height']=768;
        $config['overwrite'] = false;
        $config['encrypt_name'] = true;
        $config['remove_spaces'] = true;
        $this->load->library('upload',$config);
        if ($this->upload->do_upload('userfile')) 
        {
            //上传成功
            $data = $this->upload->data();
            $params = array('logo' => $data['file_name']);
            $id = $this->user_model->update_where($params, array('id' => $userId));
            $this->set_content(0, '上传成功', $params);
        } else {
            //上传失败
            $data = $this->upload->display_errors();
            $this->set_content(-1, '上传失败', $data);
        }
    }/*}}}*/

    public function favorite($userId, $postId, $favorite)
    {/*{{{*/
		if ($favorite)
		{
			$csql = 'if(LENGTH(favPhotos) > 0, \','.$postId.'\', \''.$postId.'\')';
			$sql = 'UPDATE `tbl_user` SET `favPhotos` = CONCAT(favPhotos,'.$csql.') WHERE `id` = '.$userId; 
			$row  = $this->user_model->excute($sql);
			$this->set_content(0, '收藏成功', array('id' => $postId));
		}
		else
		{
			$sql = "UPDATE `tbl_user` SET `favPhotos` = trim(both ',' from REPLACE(REPLACE(favPhotos, '".$postId."',''), ',,',',')) WHERE `id` = ".$userId.";";
			$row  = $this->user_model->excute($sql);
			$this->set_content(0, '取消成功', array('id' => $postId));
		}
    }/*}}}*/

}
?>

