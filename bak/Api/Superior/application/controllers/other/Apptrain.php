<?php
class Apptrain extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    private function decryptData($phoneData, $sessionKey) {
        include_once "biz/wxBizDataCrypt.php";
        $pc = new WXBizDataCrypt('wxf7f4a4e351e5b1d5', $sessionKey);
        $errCode = $pc->decryptData($phoneData['encryptedData'], $phoneData['iv'], $data);
        if ($errCode == 0) {
            $data = json_decode($data, true);
            // var_dump($data);
            // var_dump($data['phoneNumber']);
            return $data['phoneNumber'];
        } else {
             // var_dump($errCode . "\n");
            return 0;
        }
    }

    public function createUser($code = '') 
    {
        $data = $this->input->json();

        $url = 'https://api.weixin.qq.com/sns/jscode2session?appid=wxf7f4a4e351e5b1d5&secret=2e6dd7d0824d07a7d54e408a36f9417f&js_code='.$code.'&grant_type=authorization_code';
        $response = json_decode(file_get_contents($url), true);

        if (array_key_exists('phoneData', $data)) {
            $phone = $this->decryptData($data['phoneData'], $response['session_key']);
            unset($data['phoneData']);
            if ($phone) $data['phone'] = $phone;
        }

        $data['openId'] = $response['openid'];

        $this->load->model('user_model');
        $this->user_model->login($data);    
        $result = $this->getUser($response['openid']);

        // $result = $this->getUser('oeLIB5TjxuwYU5d0f_fT1Ano4Kbs');

        $this->set_content($result ? 0 : -1, $result ? '创建成功' : '创建失败', $result);
    }

    private function getUser($openId) {
        $this->load->model('user_model');
        $data = $this->user_model->get_where(array('openId' => $openId));
        if ($data['role'] && $data['role'] == 1) {
            $this->load->model('student_model');
            $student = $this->student_model->get_where(array('openId' => $openId));
            if ($student) $data['student'] = $student;
        }
        if ($data['role'] && $data['role'] == 2) {
            $this->load->model('teacher_model');
            $teacher = $this->teacher_model->get_where(array('openId' => $openId));
            if ($teacher) $data['teacher'] = $teacher;
        }
        $this->load->model('store_model');
         $store = $this->store_model->get(1);
        if ($store) $data['store'] = $store;

        $data['open'] = 0;
        return $data;
    }

    public function login($openId = '') 
    {
        $data = $this->getUser($openId);
        $this->set_content(0, '创建成功', $data);
    }


    public function updateUser()
    {
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $this->load->model('user_model');
            $this->user_model->update_where($data,  array('id' => $data['id']));
            $data = $this->user_model->get_where(array('id' => $data['id']));
            $this->set_content(0, '更新成功', $data);
        }
        else
        {
            $this->set_content( -1 , '更新失败', '');
        }
    }



    public function zan() 
    {
        $this->load->model('store_model');
        $this->store_model->increZanCount();
        $this->set_content(0, '创建成功' , '');
    }

    public function zanTeacher($teacherId) 
    {
        $this->load->model('teacher_model');
        $this->teacher_model->increZanCount($teacherId);
        $this->set_content(0, '创建成功' , '');
    }

    public function zanStudent($studentId) 
    {
        $this->load->model('student_model');
        $this->student_model->increZanCount($studentId);
        $this->set_content(0, '创建成功' , '');
    }


    public function index()
    {/*{{{*/
        $this->load->model('store_model');
        $store = $this->store_model->get(1);

        $this->load->model('banner_model');
        $banners = $this->banner_model->result();
                   
        $this->load->model('clasz_model');
        $claszs = $this->clasz_model->select_where_with_limit_order(array(), array('module' => 1), 4);

        $this->load->model('student_model');
        $students = $this->student_model->getGoodStudents();

        $this->load->model('article_model');
        $articles = $this->article_model->select_where_with_limit_order(array(), array(), 2, "createdTime"); 

        $this->load->model('teacher_model');
        $teachers = $this->teacher_model->select_where_with_limit_order(array(), array(), 4); 

        $this->load->model('comment_model');
        $comments = $this->comment_model->select_where_with_limit_order(array(), array('module' => 1), 4, "createdTime"); 

        $this->load->model('album_model');
        if ($comments && count($comments)) 
        {
            foreach ($comments as &$comment) $comment['albums'] = $this->album_model->getThumbs(6, $comment['id'], 1);
        }
        $videos = $this->album_model->select_where_with_limit_order(array(), array('type' => 2), 4); 

        $result = array("store" => $store, 'banners' => $banners, 'videos' => $videos, 'claszs' => $claszs, 'comments' => $comments, "students" => $students, 'articles' => $articles, 'teachers' => $teachers);


        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    public function store()
    {
        //商铺基本信息
        $this->load->model('store_model');
        $store = $this->store_model->get(1);

        //滚动图
        $this->load->model('banner_model');
        $banners = $this->banner_model->result();
                   
        //课程类别
        $this->load->model('category_model');
        $categorys = $this->category_model->select_where_with_limit_order(array(), array());

        //优惠课程
        $this->load->model('clasz_model');
        $claszs = $this->clasz_model->select_where_with_limit_order(array(), array('module' => 1), 4);

        //优秀学员
        $this->load->model('student_model');
        $students = $this->student_model->getGoodStudents();

        //资讯
        $this->load->model('article_model');
        $articles = $this->article_model->select_where_with_limit_order(array(), array(), 2, "createdTime"); 

        //优秀教师
        $this->load->model('teacher_model');
        $teachers = $this->teacher_model->select_where_with_limit_order(array(), array()); 

        //好评
        $this->load->model('comment_model');
        $comments = $this->comment_model->select_where_with_limit_order(array(), array('module' => 1), 4, 'createdTime'); 

        //教学环境
        $this->load->model('album_model');
        if ($comments && count($comments)) 
        {
            foreach ($comments as &$comment) $comment['albums'] = $this->album_model->getThumbs(6, $comment['id'], 1);
        }

        $albums = $this->album_model->getThumbs(1, 0, 1); 

        //精彩实录
        $this->load->model('album_model');
        $videos = $this->album_model->select_where_with_limit_order(array(), array('type' => 2), 4); 

        $result = array("store" => $store, 'banners' => $banners, 'albums' => $albums, 'videos' => $videos, 'categorys' => $categorys, 'claszs' => $claszs, 'comments' => $comments, "students" => $students, 'teachers' => $teachers);


        $this->set_content(0, '获取成功', $result);
    }



    public function getClaszs($module = 0)
    {/*{{{*/
        $this->load->model('clasz_model');
        $where = $module == 0 ? array() : array('module' => $module);
        $claszs = $this->clasz_model->select_where_with_limit_order(array(), $where, 0, 'createdTime');
        $this->load->model('album_model');
        foreach ($claszs as &$clasz) $clasz['albums'] = $this->album_model->getThumbs(2, $clasz['id'], 1);
        $this->set_content(0, '获取成功', $claszs);
    }/*}}}*/

    public function getTeacher($teacherId = 0)
    {/*{{{*/
        $this->load->model('teacher_model');
        $teacher = $this->teacher_model->get_where(array('id' => $teacherId));
        $this->load->model('claszrecord_model');
        $teacher['sumClaszs'] = $this->claszrecord_model->getClaszRecordCount($teacherId);
        $teacher['sumStudents'] = $this->claszrecord_model->getStudentCount($teacherId);

        $this->load->model('album_model');
        $teacher['albums'] = $this->album_model->getThumbs(3, $teacher['id'], 1);
        $teacher['videos'] = $this->album_model->getThumbs(3, $teacher['id'], 2);

        //好评
        $this->load->model('comment_model');
        $teacher['comments'] = $this->comment_model->select_where_with_limit_order(array(), array('module' => 2), 4, 'createdTime'); 
        // $teacher['comments'] = $this->comment_model->select_where_with_limit_order(array(), array('module' => 2, 'moduleId' => $teacher['id']), 4); 

        if ($teacher['comments'] && count($teacher['comments'])) 
        {
            foreach ($teacher['comments'] as &$comment) $comment['albums'] = $this->album_model->getThumbs(6, $comment['id'], 1);
        }

        $this->set_content(0, '获取成功', $teacher);
    }/*}}}*/


    public function getThumbTeacherByopenId($openId = 0)
    {/*{{{*/
        $this->load->model('teacher_model');
        $teacher = $this->teacher_model->get_where(array('openId' => $openId));
        if (count($teacher) == 1) $teacher = $teacher[0];
        $this->set_content(0, '获取成功', $teacher);
    }/*}}}*/


    public function getThumbStudents($openId)
    {
        $this->load->model('student_model');
        $students = $this->student_model->select_where(array('openId' => $openId));
        foreach ($students as &$student) $student['age'] = date('Y') - date('Y', strtotime($student['birthday']));

        $this->load->model('claszrecord_model');
        foreach ($students as &$student) {
            $kpi = $this->claszrecord_model->getStudentScores($student['id']);
            $student['scores'] = $kpi['scores'];
            $student['claszIndexs'] = $kpi['claszIndexs'];
        }
        $this->set_content(0, '获取成功', $students);
    }

    public function getStudent($studentId = 0)
    {/*{{{*/
        $this->load->model('student_model');
        $student = $this->student_model->get_where(array('id' => $studentId));
        $student['age'] = date('Y') - date('Y', strtotime($student['birthday']));

        $this->load->model('claszrecord_model');
        $student['labels'] = $this->claszrecord_model->getStudentLabels($studentId);

        $kpi = $this->claszrecord_model->getStudentScores($student['id']);
        $student['scores'] = $kpi['scores'];
        $student['claszIndexs'] = $kpi['claszIndexs'];

        $this->load->model('studentclass_model');
        $student['claszCount'] = $this->studentclass_model->getClassCount($studentId);
        $student['claszRecordCount'] = count($kpi);

        $this->set_content(0, '获取成功', $student);
    }/*}}}*/
}
?>