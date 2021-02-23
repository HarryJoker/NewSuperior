<?php
class Tumblr extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function create()
    {/*{{{*/
        $data = $this->input->post();
		if (!count($data))
		{
            $this->set_content(-1, '传入的Post数据为空', '');
			return;
		}
		$count = $this->tumblr_model->select_count(array('id' => $data['id'], 'type' => $data['type']));
		if (!$count)
		{
			$result = array();
			$ids = array();
			if ($data['type'] == 'regular')
			{
				$this->load->model('regular_model');
				$ids[] = $this->regular_model->create_id($data['regular']);
				$result['regular'] = $ids;
			}
			if ($data['type'] == 'video')
			{
				$this->load->model('video_model');
				$ids[] = $this->video_model->create_id($data['video']);
				$result['videos'] = $ids;
			}
			if ($data['type'] == 'photo')
			{
				$this->load->model('photo_model');
				foreach($data['photos'] as $photo)
				{
					$ids[] = $this->photo_model->create_id($photo);
				}
				$result['photos'] = $ids;
			}
			unset($data['photos']);
			unset($data['video']);
			unset($data['regular']);
			if (count($ids))
			{
				$id = $this->tumblr_model->create_id($data);
				$result['id'] = $id;
				$this->set_content(0, 'Write data success!', $result);
			}
			else
			{
				$this->set_content(-1, 'Write data fail...', array('tip' => 'fail'));
			}
		}
		else 
		{
            $result = array('id' => $data['id']);
            $this->set_content(-1, 'Post already exists', $result);
		}
    }/*}}}*/

	//更新关注的汤博号
	public function updateUsers()
	{/*{{{*/
		header("Access-Control-Allow-Origin: * ");

		$pageNum = $this->input->post('page');
		$names = $this->input->post('names');
		if ($pageNum  && $names)
		{

			$sql = 'INSERT INTO tbl_name (pageNum, names) VALUES ('.$pageNum.', "'.$names.'")  ON DUPLICATE KEY UPDATE names = "'.$names.'";';
			$code = $this->tumblr_model->excute($sql);

			if ($code)
			{
				$this->set_content(0, '持久化成功', $this->input->post());
			}
			else
			{
				$this->set_content(-1, '持久化失败', $this->input->post());
			}
		}
		else
		{
			$this->set_content(-1, '持久化失败', $this->input->post());
		}
	}/*}}}*/

	public function like($postId)
	{/*{{{*/
		$sql = 'UPDATE tbl_tumblr SET likeCount = likeCount + 1 WHERE id = '.$postId.';';
		$rows = $this->tumblr_model->excute($sql);
		if ($rows)
		{
			$this->set_content(0, '点赞成功', array('id' => $postId));
		}
		else
		{
			$this->set_content(-1, '点赞失败', array('id' => $postId));
		}

	}/*}}}*/

	//获取汤博号
	public function getTumblrNames()
	{/*{{{*/
		$sql = 'select GROUP_CONCAT(names) as names from tbl_name;';
		$result = $this->tumblr_model->query($sql);
		if(count($result) == 1 && $result[0]->names)
		{
			$this->output->set_output(json_encode($result[0]));
		}
		else
		{
			$this->output->set_output(json_encode(array('names' => '')));
		}
	}/*}}}*/

	//获取photo Names
	public function getPhotoNames($page = 1)
	{/*{{{*/
		$pageNum = 10000;
		if ($page <= 0) 
		{
			$this->output->set_output(json_encode(array()));
			return;
		}
		$sql ="select pid, photo_name_500, photo_name_400 from tbl_photo where photo_name_400 != '' limit ".(($page - 1) * $pageNum)." , ".$pageNum;
		$result = $this->tumblr_model->query($sql);
		$this->output->set_output(json_encode($result));
	}/*}}}*/

	public function getUrls($num =10, $lastPid = 0)
	{/*{{{*/
		$where = $lastPid ? ' and pid < '.$lastPid : '';
		$sql = "select pid, `photo-url-500`, `photo-url-400` from tbl_photo where photo_name_400 = '' and status = 1 ".$where."  order by pid desc limit ".$num;
		$result = $this->tumblr_model->query($sql);
		$this->output->set_output(json_encode($result));
	}/*}}}*/

	public function updatePhotos()
	{/*{{{*/
		$photos = $this->input->post();
		if ($photos)
		{
			$this->load->model('photo_model');
			$pRows = $this->photo_model->update_batch($photos, 'pid');
			$this->output->set_output($pRows);
		}
		else
		{
			$this->output->set_output(-1);
		}
	}/*}}}*/

	//更新本地图片
	public function updatePhoto($pid = 0)
	{/*{{{*/
		$data = $this->input->post();
		if (!count($data) || $pid <= 0) 
		{
			$this->set_content(-1, '参数错误', array('params' => 'error'));
			return;
		}
		$this->load->model('photo_model');
		$rows = $this->photo_model->update_where($data, array('pid' => $pid));
		if ($rows)
		{
			$this->set_content(0, '持久化成功', array('rows' => $rows));
		}
		else
		{
			$this->set_content(-1, '持久化失败', array('rows' => $rows));
		}
	}/*}}}*/

    public function download($source = '')
    {/*{{{*/
        //
        $attachments = array();
        if ($source)
        {
            $imgs = explode(',',$source);
            foreach($imgs as $img)
            { 
                $dir = '.'.substr(strtolower($img), 0, 19);
                if(!is_dir($dir)) mkdir($dir); //创建文件夹
                $pic = file_get_contents('http://www.weike006.com'.$img);
                if (file_put_contents('.'.strtolower($img), $pic))
                {
                    $attachments[] = $img;
                }
            } 
        }
        return implode(',', $attachments);
    }/*}}}*/

    public function getCrackIds()
    {/*{{{*/
        //$sql = "select group_concat(id) as ids from tbl_crack where verify =1 and title != '';";
        $sql = "select group_concat(id) as ids from tbl_crack where verify = 0 and id > 8548;";
        $result = $this->crack_model->query($sql);
        $result = count($result) >= 1 ? $result[0] : array('ids' => 0);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getPageCracksById($id = 0, $type = 0, $city = 0)
    {/*{{{*/
        $sql = "select * from tbl_crack where title != '' and id > ".$id; 
        if ($city > 0) {
            $sql = $sql." and city = ".$city;
        }
//        else
//        {
//            if ($province > 0 ) $sql = $sql." and province =".$province;
//        }
        if ($type > 0)
        {
            $sql = $sql." and type = ".$type;
        }
        $sql = $sql." limit 15;";
        $result = $this->crack_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getAreas()
    {/*{{{*/
        $sql = "select province as id, if(provinceName='', '全部', provinceName) as name from tbl_crack group by province;";
        $result = $this->crack_model->query($sql);
        foreach($result as $area)
        {
            $sql = "select city as id, if(cityName='选择县区', '全部', cityName) as name from tbl_crack where province=".$area->id." group by cityName order by city;";
            $citys = $this->crack_model->query($sql);
            $area->citys = $citys;
        }
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

	public function getPhotosByLastId($lastPid = 0, $lastTime = 0)
	{/*{{{*/
		if ($lastTime == 1530998051926 && !$lastPid)
		{
			$this->set_content(-1, '获取失败', array());
			return;
		}
		
		$colums = array('pid', 'postId', 'width', 'height', 'photo_name_400', 'photo_name_500', 'reader', 'utime');
		if ($lastTime)
		{
			$where = $lastTime == 1530998051926 ? 'and pid < '.$lastPid : 'and utime < '.$lastTime;
		}
		else
		{
			$where = '';
		}

		$sql = "select ".implode(',', $colums)." from tbl_photo where status = 1 and photo_name_400 != ''".$where." order by utime desc, pid desc limit 200";
		$result = $this->tumblr_model->query_array($sql);

		$photos = array();
		for($n = 0; $n < count($result); $n++)
		{
			if ($n && $result[$n]['postId'] == $result[$n - 1]['postId'])
			{
				$photos[count($photos) - 1][] = $result[$n];
			}
			else
			{
				if (count($photos) == 20) break;
				$photos[count($photos)][] = $result[$n];
			}
		}

        $this->set_content(0, '获取成功', $photos);

	}/*}}}*/

	public function extisPhoto($name = '')
	{/*{{{*/
		$this->load->model('photo_model');
		$result = $this->photo_model->get_where(array('photo_name_400' => $name));
		$count = count($result);
        $this->output->set_content_type('application/json')->set_output(json_encode($count));
	}/*}}}*/

	private function msectime() 
	{/*{{{*/
		list($msec, $sec) = explode(' ', microtime());
		$msectime =  (float)sprintf('%.0f', (floatval($msec) + floatval($sec)) * 1000);
		return $msectime;
	}/*}}}*/

	public function getFavoritePhotosIn()
	{/*{{{*/
		$colums = array('pid', 'postId', 'width', 'height', 'photo_name_400', 'photo_name_500', 'reader', 'utime');
		$postIds = $this->input->post('postIds');
		if (!$postIds) 
		{
			$this->set_content(-1, '参数错误', array());
			return;
		}
		$sql = "select ".implode(',', $colums)." from tbl_photo where postId in (".$postIds.")";
		$result = $this->tumblr_model->query_array($sql);

		$photos = array();
		for($n = 0; $n < count($result); $n++)
		{
			if ($n && $result[$n]['postId'] == $result[$n - 1]['postId'])
			{
				$photos[count($photos) - 1][] = $result[$n];
			}
			else
			{
				$photos[count($photos)][] = $result[$n];
			}
		}

        $this->set_content(0, '获取成功', $photos);

	}/*}}}*/

    public function getFavoriteByUser($userId)
    {/*{{{*/
        $sql = 'select favPhotos from tbl_photo where id = '.$userId.';';
		$this->load->model('user_model');
        $result = $this->user_model->query($sql);
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

	public function getCommunityPhotos($lastUtime = 0)
	{/*{{{*/
		$where = $lastUtime ? ' and utime > '.$lastUtime : '';
		$sql = "select pid, postId, photo_name_400 from tbl_photo where status = 1 and photo_name_400 != ''".$where." order by utime desc limit 100";
		$result = $this->tumblr_model->query_array($sql);

		$photos = array();
		$num = 0;
		for($n = 0; $n < count($result); $n++)
		{
			$photos[] = $result[$n]; 
			if ($n && $result[$n]['postId'] != $result[$n - 1]['postId'])
			{
				if ($num == 10) break;
				$num++;
			}
		}

        $this->set_content(0, '获取成功', $photos);

	}/*}}}*/

	public function getCommunityVideos($lastPid = 0)
	{/*{{{*/
		$sql = "select pid, video_name from tbl_video where status = 2 and video_name != '' and pid > ".$lastPid." limit 5";
		$result = $this->tumblr_model->query_array($sql);
        $this->set_content(0, '获取成功', $result);
	}/*}}}*/

	public function updatePhotoTime($size = 5)
	{/*{{{*/
		$sql = "select pid, postId, utime  from tbl_photo where status = 1 and photo_name_400 != '' and utime = 1530998051926 limit ".$size*10;
		$result = $this->tumblr_model->query_array($sql);

		$num = 0;
		$utime = $this->msectime();
		for($n = 0; $n < count($result); $n++)
		{
			if ($n && $result[$n]['postId'] != $result[$n - 1]['postId'])
			{
				if ($num == $size) break;
				$utime -= rand(2, 2 * 60) * 1000;
				$num++;
			}
			$photo = $result[$n];
			$photo['utime'] = $utime;
			unset($photo['postId']);
			$photos[] = $photo; 
		}

		$this->load->model('photo_model');
		$rows = $this->photo_model->update_batch($photos, $where = 'pid');
        $this->set_content(0, '获取成功', array('content' => '更新成功'. $size.'条, 共'.count($photos).'张'));

	}/*}}}*/

	public function getVideosByLastTime($lastTime = 0)
	{/*{{{*/
		$where = $lastTime ? ' and utime < '.$lastTime : '';
		$colums = 'pid, thumbnail_width, thumbnail_height, status, utime, reader, thumbnail_name, video_name';
		$sql = "select ".$colums." from tbl_video where status > 0 and thumbnail_name != '' ".$where." order by utime desc, pid desc limit 20";
		$result = $this->tumblr_model->query_array($sql);
        $this->set_content(0, '获取成功', $result);

	}/*}}}*/

	public function getSpiderVideos($limit = 2)
	{/*{{{*/
		$sql = "select pid, video_url,thumbnail_url,extension from tbl_video where status = 0 limit ".$limit;
		//$sql = "select pid, video_url, thumbnail_url from tbl_video limit ".$limit;
		$result = $this->tumblr_model->query_array($sql);
		$this->output->set_output(json_encode($result));

	}/*}}}*/

	public function updateVideos()
	{/*{{{*/
		$videos = $this->input->post();
		if ($videos)
		{
			$this->load->model('video_model');
			$pRows = $this->video_model->update_batch($videos, 'pid');
			$this->output->set_output($pRows);
		}
		else
		{
			$this->output->set_output(-1);
		}
	}/*}}}*/

}
?>

