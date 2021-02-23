<?php
class Comment extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    private function increCommentCount($moudle, $moduleId) {
         //module 1店铺，2老师，3学生
        switch ($moudle) {
            case '1':
                $this->load->model('store_model');
                $rows = $this->store_model->increCommentCount();
                break;
            case '2':
                $this->load->model('teacher_model');
                $rows = $this->teacher_model->increCommentCount($moduleId);
                break;
            case '3':
               $this->load->model('student_model');
                $rows = $this->student_model->increCommentCount($moduleId);
                break;
        }
    }


    private function updateAvgScore($moudle, $moduleId, $socre) {
         //module 1店铺，2老师，3学生
        switch ($moudle) {
            case '1':
                $this->load->model('store_model');
                $rows = $this->store_model->updateScore($moduleId, $socre);
                break;
            case '2':
                $this->load->model('teacher_model');
                $rows = $this->teacher_model->updateScore($moduleId, $socre);
                break;
            case '3':
               $this->load->model('student_model');
                $rows = $this->student_model->updateScore($moduleId, $socre);
                break;
        }
    }

    public function create()
    {/*{{{*/

        $data = $this->input->json();
        if (count($data)) 
        {
            $albums = $data['albums'];
            unset($data['albums']);
            $id = $this->comment_model->create_id($data);

            //算平均分
            $result = $this->comment_model->get_where_with_fields(array('module' => $data['module'], 'moduleId' => $data['moduleId']), array('AVG(score) as score'));
            //更新得分
            $this->updateAvgScore($data['module'], $data['moduleId'], $result['score']);
            //自增评论数量
            $this->increCommentCount($data['module'], $data['moduleId']);

            if ($id && isset($albums) && count($albums)) {
                $newAblums = array();
                foreach ($albums as $album) $newAblums[] = array('module' => 6, 'moduleId' => $id, 'type' => 1, 'url' => $album['url']);
                $this->load->model('album_model');
                $rows = $this->album_model->create_batch($newAblums);
            }
            $this->set_content(0, '提交成功', array('id' => $id));
        }
        else 
        {
            $this->set_content(-1, '提交失败', '');
        }
    }/*}}}*/

    public function getStoreComments($storeId = 0)
    {/*{{{*/
        $this->getCommentsBy(0, $storeId);
    }/*}}}*/

    public function getTeacherComments($teacherId = 0)
    {/*{{{*/
        $this->getCommentsBy(0, $teacherId);
    }/*}}}*/

    public function getStudentComments($studentId = 0)
    {/*{{{*/
        $this->getCommentsBy(0, $studentId);
    }/*}}}*/

    private function getCommentsBy($module, $moduleId)
    {/*{{{*/
        $where = array();
        if ($module) $where['module'] = $module;
        if ($moduleId) $where['moduleId'] = $moduleId;
        if ($where) 
        {
            $comments = $this->comment_model->get_where($where);
            $this->set_content(0, '获取成功', $comments);
        }
        else
        {
            $this->set_content(-1, '参数错误', array());
        }
    }/*}}}*/

}
?>