<?php
class Student_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



     public function getGoodStudents() 
     {
     	// $this->load->library('mongo_db');
     	$this->load->model('claszrecord_model');
     	$students = $this->select_where_with_limit_order(array(), array(), 4);
     	foreach ($students as &$student) {
     		$student['age'] = date('Y') - date('Y', strtotime($student['birthday']));
     		$student['labels'] = $this->claszrecord_model->getStudentLabels($student['id']);
            $kpi = $this->claszrecord_model->getStudentScores($student['id']);
            $student['scores'] = $kpi['scores'];
            $student['claszIndexs'] = $kpi['claszIndexs'];
            $student['claszRecordCount'] = count($kpi);
     	}
        return $students;
     }



     public function  increZanCount($studentId, $incre = true) 
     {
        return $this->increCount($studentId, 'zanCount', 'zanCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     public function  increCommentCount($studentId, $incre = true) 
     {
        return $this->increCount($studentId, 'commentCount', 'commentCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     private function increCount($studentId, $key, $value)
     {
        $this->db->set($key, $value, false);
        $this->db->where(array('id' => $studentId));
        $this->db->update($this->getName());
        return $this->db->affected_rows(); 
     }

     public function updateScore($id, $score) {
        return $this->update_where(array('score' => $score), array('id' => $id));
     }
}
?>