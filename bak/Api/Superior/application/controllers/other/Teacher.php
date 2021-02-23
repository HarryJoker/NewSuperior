<?php
class Teacher extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $id = $this->teacher_model->create_id($this->input->post());
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function getTeacher($teacherId = 0)
    {/*{{{*/
        $teacher = $this->teacher_model->get_where(array('id' => $teacherId));
        $this->set_content(0, '获取成功', $teacher);
    }/*}}}*/

    public function getGoodTeachers()
    {/*{{{*/
        $student = $this->teacher_model->result();
        $this->set_content(0, '获取成功', $student);
    }/*}}}*/

    public function getTeachers()
    {/*{{{*/
        $students = $this->teacher_model->result();
        foreach ($students as &$student) {
            $student['labels'] = explode(' ', $student['major']);
            $student['age'] = date('Y') - date('Y', strtotime($student['birthday']));
        }
        $this->set_content(0, '获取成功', $students);
    }/*}}}*/

   
}
?>