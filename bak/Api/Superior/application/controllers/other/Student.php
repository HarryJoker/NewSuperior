<?php
class Student extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $id = $this->student_model->create_id($this->input->json());
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function update($studentId = 0) 
    {
        $data = $this->input->json();
        $id = $this->student_model->update_where($data, array('id' => $studentId));
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }

    public function getStudent($studentId = 0)
    {/*{{{*/
        $student = $this->student_model->get_where(array('id' => $studentId));
        $this->set_content(0, '获取成功', $student);
    }/*}}}*/

    public function getGoodStudents()
    {/*{{{*/
        $student = $this->student_model->result();
        $this->set_content(0, '获取成功', $student);
    }/*}}}*/


    public function getStudentComments($studentId)
    {
        //好评
        $this->load->model('comment_model');
        $comments = $this->comment_model->select_where_with_limit_order(array(), array('module' => 3, 'moduleId' => $studentId), 0, 'createdTime'); 
        // $teacher['comments'] = $this->comment_model->select_where_with_limit_order(array(), array('module' => 2, 'moduleId' => $teacher['id']), 4); 

        if ($comments && count($comments)) 
        {
            $this->load->model('album_model');
            foreach ($comments as &$comment) $comment['albums'] = $this->album_model->getThumbs(6, $comment['id'], 1);
        }

        $this->set_content(0, '获取成功', $comments);
    }

     public function getMyTeacher($studentId = 0)
    {/*{{{*/
        $student = $this->student_model->get_where(array('id' => $studentId));
        $this->load->model('teacher_model');
        $teacher = $this->teacher_model->get_where(array('id' => $student['id']));
        $this->set_content(0, '获取成功', $teacher);
    }/*}}}*/

    public function getStudentsByUser($openId) 
    {
        $students = $this->student_model->select_where(array('openId' => $openId));
        foreach ($students as &$student) {
            $student['age'] = date('Y') - date('Y', strtotime($student['birthday']));
        }
        $this->set_content(0, '获取成功', $students);
    }

}
?>