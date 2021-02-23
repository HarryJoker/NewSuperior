<?php
class User_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     public function login($data = array())
    {/*{{{*/
        if ($data && array_key_exists('openId', $data)) {
            if (count($data) > 1) {
                $count = $this->result_count(array('openId' => $data['openId']));
                if ($count == 1) $this->update_where($data, array('openId' => $data['openId']));
                else $this->create_id($data);
            }
        }
    }/*}}}*/
}
?>