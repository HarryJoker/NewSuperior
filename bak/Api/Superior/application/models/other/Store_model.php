<?php
class Store_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/




     public function getThumbStore() 
     {
     	$store = $this->store_model->select_where(array('id', 'logo', 'name','sign'), array('id' => 1));
     	$this->load->library('mongo_db');
        // Execute select query
        $result = $this->mongo_db->where('id', 1)->getOne('store');
        // First row
        $result = $this->mongo_db->row_array($result);
        $store = array_merge($store, $result);
        return $store;
     }


     //l老师或学员人数增加或减少
     public function  increCountByRole($role, $incre = true) 
     {
        if (isset($role) && $role) 
        {
            $role = is_int($role) ? $role : intval($role);
            return $role == 1 ? $this->increStudentCount($incre) : $this->increTeacherCount($incre);
        }
        return false;
     }

     public function  increStudentCount($incre = true) 
     {
        return $this->increCount('studentCount', 'studentCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     public function  increTeacherCount($incre = true) 
     {
        return $this->increCount('teacherCount', 'teacherCount'.($incre ? ' + 1 ' : ' - 1'));
     }

      public function  increZanCount($incre = true) 
     {
        return $this->increCount('zanCount', 'zanCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     public function  increCommentCount($incre = true) 
     {
        return $this->increCount('commentCount', 'commentCount'.($incre ? ' + 1 ' : ' - 1'));
     }

     private function increCount($key, $value)
     {
        $this->db->set($key, $value, false);
        $this->db->where(array('id' => 1));
        $this->db->update($this->getName());
        return $this->db->affected_rows(); 
     }

     public function updateScore($id, $score) {
        return $this->update_where(array('score' => $score), array('id' => $id));
     }

}
?>