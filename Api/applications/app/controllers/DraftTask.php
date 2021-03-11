<?php
class DraftTask extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function createDraftTasks() {
        $data = $this->input->post();
        $rows = $this->drafttask_model->create_batch($data);
        $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '创建成功' : '创建失败', array('rows' => $rows));
    }


    public function getDraftTasks($draftId = 0) {
        $draftTasks = $this->drafttask_model->get_where_with_order_for_asc(array('draftId'=> $draftId), 'taskSerial');
        for ($i=0; $i < count($draftTasks); $i++) { 
            $draftTasks[$i]['unitIds'] = explode(',', $draftTasks[$i]['unitIds']);
        }
        $this->set_content(0, '获取成功', $draftTasks);
    }


    public function deployDraftTasks($ids = array()) {
        if (empty($ids)) {
            $ids = $this->input->post('ids');
        }
        if ($ids && count($ids) > 0) {
            $this->drafttask_model->deployDraftTasks($ids);
            $this->set_content(0, '发布成功', array());
        } else {
            $this->set_content(-1, '发布失败', array());
        }
        
    }

    public function deployDraftTask($id = 0) {
        $this->deployDraftTasks(array($id));
    }

    public function updateDraftTask($id = 0) {
        $data = $this->input->post();
        $rows = $this->drafttask_model->update_where($data, array('id'=> $id));
        $draftTask = $this->drafttask_model->get($id);
        $draftTask['unitIds'] = explode(',', $draftTask['unitIds']);
        $this->set_content($rows >= 0 ? 0 : -1, $rows > 0 ? '创建成功' : '创建失败', $draftTask);
    }



    public function deleteDraftTasks() {
        $ids = $this->input->post('ids');
        if ($ids && count($ids) > 0) {
            $sql = 'delete from tbl_drafttask where id in ('.implode(',', $ids).')';
            $rows = $this->drafttask_model->excute($sql);
            $this->set_content($rows > 0 ? 0 : -1, $rows > 0 ? '删除成功' : '删除失败', array('rows' => $rows));
        } else {
            $this->set_content(-1, '删除失败', array());
        }
    }

}
?>
