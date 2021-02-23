<?php

class Grade_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/

     function select_by_category()
     {/*{{{*/
         $result = array();
         for ($x=1; $x<=7; $x++)
         {
             $result[$x.''] = $this->get_where(array('category' => $x));
         }
         return $result;
     }/*}}}*/

}
?>

