<?php
class Teacher_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     public function  increZanCount($teacherId, $incre = true) 
     {
        return $this->increCount($teacherId, 'zanCount', 'zanCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     public function  increCommentCount($teacherId, $incre = true) 
     {
        return $this->increCount($teacherId, 'commentCount', 'commentCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     private function increCount($teacherId, $key, $value)
     {
        $this->db->set($key, $value, false);
        $this->db->where(array('id' => $teacherId));
        $this->db->update($this->getName());
        return $this->db->affected_rows(); 
     }

     public function updateScore($id, $score) {
        return $this->update_where(array('score' => $score), array('id' => $id));
     }
}
?>