<?php

class Album_model extends CA_Model {

     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/



    public function getThumbs($module = 0, $moduleId = 0, $type = 0)
    {/*{{{*/
        $sql = 'select id, id as uid, url as thumbUrl, "done" as status from tbl_album where valid = 0';
        if ($module > 0) $sql .= ' and module = '.$module;
        if ($moduleId > 0) $sql.= ' and moduleId = '. $moduleId;
        if ($type > 0) $sql.= ' and type = '.$type;
        $albums = $this->album_model->query_array($sql);
        return $albums;
    }/*}}}*/

    public function updateThumbs($albums = array(), $module, $moduleId) 
    {
        $newAlbums = array();
        foreach ($albums as $album) 
        {
            if (!array_key_exists('id',$album)) $newAlbums[] = array('module' => $module, 'moduleId' => $moduleId, 'type' => 1, 'url' => $album['thumbUrl']);   
        }
        if (count($newAlbums)) $rows = $this->album_model->create_batch($newAlbums);
    }
     
}
?>


