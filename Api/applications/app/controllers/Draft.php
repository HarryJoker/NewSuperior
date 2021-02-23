<?php
class Draft extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getDrafts($category = 0)
    {/*{{{*/
        $sql = 'select draft.id, draft.category, draft.name, draft.createTime, draft.taskYear, (select count(*) from tbl_drafttask where draftId = draft.id) as taskCount from tbl_draft draft where category = '.$category;
        $drafts = $this->draft_model->query($sql);
        $this->set_content(0, '获取成功', $drafts);
    }/*}}}*/

    // public function updateBanner($bannerId = 0) {
    //     $id = $this->banner_model->update_where($this->input->post(), array('id' => $bannerId));
    //     $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '更新成功' : '更新失败', array('rows' => $id));
    // }

    public function newDraft() 
    {
        $data = $this->input->post();
        $id = $this->draft_model->create_id($data);
        if ($id) {
            $draft = $this->draft_model->get($id);
            $this->set_content(0, '创建成功', $draft);
        } else {
            $this->set_content(-1, '创建成功', '');
        }
        
    }

    public function deleteDrarft($id = 0)
    {
        $rows = $this->draft_model->delete(array('id'=>$id));
        $sql = 'delete from tbl_drafttask where  draftId = '.$id;
        $this->draft_model->excute($sql);
        $this->set_content(0, '删除成功', array('rows' => $rows));
    }
}
?>
