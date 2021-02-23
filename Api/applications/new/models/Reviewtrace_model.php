<?php

class Reviewtrace_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/




     function createReviewTrace($data = array()) {
          if (empty($data)) return 0;
          return $this->create_id($data);
     }


}
?>

