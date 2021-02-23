<?php

class Vote_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


     private function getOpinionVotes() {
         $sql = 'select opinionId, count(id) as voteCount from tbl_vote group by opinionId';
         $votes = $this->query($sql);
         if (count($votes)) {
            $votes = array_column($votes, NULL, 'opinionId');
         }
         return $votes;
     }


     function getVoteOpinions() {
        $sql = 'select * from tbl_opinion where status > 2';
        $opinions = $this->query($sql);
        if (count($opinions)) {
            $votes = $this->getOpinionVotes();
            for ($i=0; $i < count($opinions); $i++) { 
                if (array_key_exists($opinions[$i]['id'], $votes)) {
                    $opinions[$i]['voteCount'] = $votes[$opinions[$i]['id']]['voteCount'];
                } else {
                    $opinions[$i]['voteCount'] = 0;
                }
            }
        }
        
        // var_dump(array_column($opinions,'voteCount'));
        $sumVoteCount = array_sum(array_column($opinions,'voteCount'));
        // var_dump($sumVoteCount);

        $newClues = array();
        foreach ($opinions as $key => $value) {
            $value['ratio'] = $sumVoteCount == 0 ? 0 : (round(($value['voteCount'] / $sumVoteCount * 100), 2)).'%';
            $value['rank'] = '第'.($key + 1).'名';
            $newClues[] = $value;
        }
        return $newClues;
     }



     function getVoteOpinion($opinionId = 0) {
        $opinions = $this->getVoteOpinions();
        foreach ($opinions as $opinion) {
            if ($opinion['id'] == $opinionId) {
                return $opinion;
            }
        }
        return array();
     }

}
?>


