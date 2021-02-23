<?php

class Clue_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     // function getClues() {
     //    $sql = 'select o.*, c.opinionId, count(c.id) as clueCount from tbl_clue c left join tbl_opinion o on c.opinionId = o.id group by opinionId order by clueCount desc';
     //    $clues = $this->query($sql);
     //    $sumClueCount = array_sum(array_column($clues,'clueCount'));
     //    $newClues = array();
     //    foreach ($clues as $key => $value) {
     //        $value['ratio'] = (round(($value['clueCount'] / $sumClueCount * 100), 2)).'%';
     //        $value['rank'] = '第'.($key + 1).'名';
     //        $newClues[] = $value;
     //    }
     //    return $newClues;
     // }

}
?>


